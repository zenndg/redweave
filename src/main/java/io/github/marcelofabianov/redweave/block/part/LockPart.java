package io.github.marcelofabianov.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.config.NeoForgeLockLimits;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.LockLimits;
import io.github.marcelofabianov.redweave.domain.module.SignalLock;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.menu.LockMenu;
import io.github.marcelofabianov.redweave.menu.LockScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.ModuleServices;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.slf4j.Logger;

/**
 * The Signal Lock as a part: _docs/funcionalidades/fechadura.md. A Signal Module of transformation
 * that gives its strength on its front edge or a channel while its input, on its back edge or a
 * channel, has exactly the strength of its key, and the `Toggle` of its owner opens it for its
 * pulse (answers 633 to 636 and 640). Only its owner sees the key (answer 637). Nobody breaks it by
 * hand in survival, and no explosion takes it nor its block of support (answer 641).
 */
public final class LockPart extends ModulePart implements SecuredPart {
  public static final String ID = "signal_lock";

  /** The looks of the top: locked, with the red light, and unlocked, with the green one (638). */
  public static final List<String> LOOKS = List.of(ModuleLooks.NONE, ModuleLooks.ON);

  private static final double PADLOCK_EDGE = 6;
  private static final double PADLOCK_FAR_EDGE = 10;
  private static final double PADLOCK_HEIGHT = 6;
  private static final RaisedShapes PADLOCK =
      new RaisedShapes(PADLOCK_EDGE, PADLOCK_FAR_EDGE, PADLOCK_HEIGHT);
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String FITTED = "Fitted the Signal Lock at {} to the config: {}";

  private SignalLock lock = SignalLock.placed(new NeoForgeLockLimits());
  private Connection input = Connection.EDGE;
  private Connection output = Connection.EDGE;

  /** The strength its input had on the last tick; not saved, as the next tick reads it again. */
  private Signal read = Signal.OFF;

  private LockPart(Direction face) {
    super(face);
  }

  /** A lock on the side of its space, from its NBT, or a new one from an empty tag. */
  public static LockPart load(Direction face, CompoundTag tag) {
    LockPart part = new LockPart(face);
    part.loadPlate(tag);
    if (!tag.isEmpty()) {
      part.lock = LockSave.read(tag);
      part.input = ModuleSave.readConnection(tag, LockSave.INPUT);
      part.output = ModuleSave.readConnection(tag, LockSave.OUTPUT);
    }
    return part;
  }

  @Override
  protected String id() {
    return ID;
  }

  @Override
  public Item item() {
    return RedweaveItems.SIGNAL_LOCK.get();
  }

  /**
   * The owner tests the lock with the `Toggle` even with its network off, as the emitters (707).
   */
  @Override
  public boolean togglesOffline() {
    return true;
  }

  /** The plate and the padlock over it, so the padlock can be aimed at too. */
  @Override
  public VoxelShape shape() {
    return Shapes.or(super.shape(), PADLOCK.of(face()));
  }

  /** Unlocked while it gives a signal, which the client receives as `powered` (answer 638). */
  @Override
  protected String lookSuffix() {
    return powered() ? ModuleLooks.ON : ModuleLooks.NONE;
  }

  /** A new lock: key 15, giving 15, a pulse of 1 s, on its edges (answers 635, 636 and 640). */
  @Override
  protected void placeRule() {
    lock = SignalLock.placed(new NeoForgeLockLimits());
    input = Connection.EDGE;
    output = Connection.EDGE;
  }

  /** Its input against its key, and then one tick of the pulse of its `Toggle` (634 and 640). */
  @Override
  protected void tickRule(ModuleServices modules, PartSpaceBlockEntity space) {
    read = ModuleTicks.read(modules, space, this, input);
    ModuleTicks.write(modules, space, this, lock.output(read));
    SignalLock next = lock.tick();
    if (!next.equals(lock)) {
      lock = next;
      markDirty();
    }
  }

  @Override
  protected void saveRule(CompoundTag tag) {
    LockSave.write(tag, lock);
    ModuleSave.writeConnection(tag, LockSave.INPUT, input);
    ModuleSave.writeConnection(tag, LockSave.OUTPUT, output);
  }

  public SignalLock lock() {
    return lock;
  }

  /** The strength its input had on the last tick. */
  public Signal read() {
    return read;
  }

  public Connection input() {
    return input;
  }

  @Override
  public Connection output() {
    return output;
  }

  /** The `Toggle` opens it for its pulse, and says so with its name (answer 640). */
  @Override
  public Optional<Component> toggle() {
    setLock(lock.unlock());
    return module()
        .map(found -> Component.translatable(ModuleMessages.LOCK_UNLOCKED, found.displayName()));
  }

  /** `Locked` or `Unlocked`, never the key (answer 637). */
  @Override
  public Optional<String> stateKey() {
    return Optional.of(LockScreenTexts.of(powered()));
  }

  /** Nobody breaks it by hand in survival, not even its owner (answer 641). */
  @Override
  public boolean mayBreak(Player player) {
    return false;
  }

  @Override
  public boolean breakBy(PartSpaceBlockEntity space, Player player) {
    return false;
  }

  @Override
  public void openScreen(ServerPlayer viewer) {
    PartSpot spot = spot();
    viewer.openMenu(
        new SimpleMenuProvider(
            (id, inventory, opener) -> new LockMenu(id, viewer, spot), item().getDescription()),
        extra -> LockMenu.writeOpening(extra, viewer, spot));
  }

  /** Keeps the lock after a change and shows it at once. */
  public void setLock(SignalLock next) {
    lock = next;
    changed();
  }

  /** The owner moved the input to its edge or to a channel (answer 635). */
  public void connectInput(Connection connection) {
    input = connection;
    changed();
  }

  /** The owner moved the output to its edge or to a channel (answer 635). */
  public void connectOutput(Connection connection) {
    output = connection;
    changed();
  }

  /**
   * On the server, a loaded lock fits the limits of the config of now: a pulse outside them goes to
   * the nearest limit, with a warning (answers 425 and 640, rule 14).
   */
  @Override
  public void loaded(PartSpaceBlockEntity space) {
    LockLimits limits = new NeoForgeLockLimits();
    if (module().isPresent() && !lock.fits(limits)) {
      LOGGER.warn(FITTED, space.getBlockPos(), lock.pulse().ticks());
      lock = lock.fittedTo(limits);
      markDirty();
    }
  }
}
