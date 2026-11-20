package io.github.zenndg.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.zenndg.redweave.block.ModuleMessages;
import io.github.zenndg.redweave.block.PartSpaceBlockEntity;
import io.github.zenndg.redweave.config.NeoForgeKeypadLimits;
import io.github.zenndg.redweave.domain.module.Connection;
import io.github.zenndg.redweave.domain.module.KeypadCode;
import io.github.zenndg.redweave.domain.module.KeypadEntry;
import io.github.zenndg.redweave.domain.module.KeypadLimits;
import io.github.zenndg.redweave.domain.module.KeypadTry;
import io.github.zenndg.redweave.domain.module.SignalKeypad;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.domain.signal.Signal;
import io.github.zenndg.redweave.menu.KeypadEntryMenu;
import io.github.zenndg.redweave.menu.KeypadMenu;
import io.github.zenndg.redweave.menu.KeypadScreenTexts;
import io.github.zenndg.redweave.registry.RedweaveItems;
import io.github.zenndg.redweave.server.ModuleServices;
import io.github.zenndg.redweave.server.RedweaveServer;
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
 * The Signal Keypad as a part: _docs/funcionalidades/teclado.md. Its code, the tries and the cards
 * are secret keys of its save, which never reach a client (answers 896 to 899 and 910). Its input
 * is only a channel, and a channel that is not available is no power (answers 661 and 906). It is
 * guarded as the Signal Lock and never copied (answers 641, 708 and 731). The rule stays in the
 * domain.
 */
public final class KeypadPart extends ModulePart implements SecuredPart, SecretPart {
  public static final String ID = "signal_keypad";

  /** The looks of the top: locked or unlocked (answers 667 and 908). */
  public static final List<String> LOOKS = List.of(ModuleLooks.NONE, ModuleLooks.ON);

  private static final double KEYPAD_EDGE = 5;
  private static final double KEYPAD_FAR_EDGE = 11;
  private static final double KEYPAD_HEIGHT = 3;
  private static final RaisedShapes KEYPAD =
      new RaisedShapes(KEYPAD_EDGE, KEYPAD_FAR_EDGE, KEYPAD_HEIGHT);
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String FITTED = "Fitted the Signal Keypad at {} to the config: {}";

  private SignalKeypad keypad = SignalKeypad.placed();
  private Connection input = Connection.EDGE;
  private Connection output = Connection.EDGE;

  /** Whether its input channel had a signal on the last tick; not saved, as it is read again. */
  private boolean powered;

  private KeypadPart(Direction face) {
    super(face);
  }

  /** A keypad on the side of its space, from its NBT, or a new one from an empty tag. */
  public static KeypadPart load(Direction face, CompoundTag tag) {
    KeypadPart part = new KeypadPart(face);
    part.loadPlate(tag);
    if (!tag.isEmpty()) {
      part.readRule(tag);
    }
    return part;
  }

  /** The Signal Keypad is never copied nor pasted (answer 731). */
  @Override
  protected List<String> copiedKeys() {
    return List.of();
  }

  @Override
  protected void dropChannels(ConnectionMapper drop) {
    input = drop.of(input);
    output = drop.of(output);
  }

  @Override
  protected void readRule(CompoundTag tag) {
    keypad = KeypadSave.read(tag);
    input = ModuleSave.readConnection(tag, KeypadSave.INPUT);
    output = ModuleSave.readConnection(tag, KeypadSave.OUTPUT);
  }

  @Override
  protected String id() {
    return ID;
  }

  @Override
  public Item item() {
    return RedweaveItems.SIGNAL_KEYPAD.get();
  }

  /** The owner tests it with the `Toggle` even with its network off (answer 906). */
  @Override
  public boolean togglesOffline() {
    return true;
  }

  /** The plate and the keypad over it, so the keypad can be aimed at too (answer 909). */
  @Override
  public VoxelShape shape() {
    return Shapes.or(super.shape(), KEYPAD.of(face()));
  }

  @Override
  protected String lookSuffix() {
    return powered() ? ModuleLooks.ON : ModuleLooks.NONE;
  }

  /** A new keypad: locked, with no code, no input channel, on its front edge (660, 663). */
  @Override
  protected void placeRule() {
    keypad = SignalKeypad.placed();
    input = Connection.EDGE;
    output = Connection.EDGE;
  }

  /** Reads its input channel, gives its output while open, and runs its open time (907). */
  @Override
  protected void tickRule(ModuleServices modules, PartSpaceBlockEntity space) {
    powered =
        !input.isEdge()
            && ModuleTicks.reading(modules, space, this, input).map(Signal::isOn).orElse(false);
    ModuleTicks.write(modules, space, this, keypad.output());
    if (keypad.unlocked()) {
      keypad = keypad.tick();
      markDirty();
    }
  }

  @Override
  protected void saveRule(CompoundTag tag) {
    KeypadSave.write(tag, keypad);
    ModuleSave.writeConnection(tag, KeypadSave.INPUT, input);
    ModuleSave.writeConnection(tag, KeypadSave.OUTPUT, output);
  }

  /** The code, the tries and the cards never reach a client (answers 896 to 898 and 910). */
  @Override
  public List<String> secretKeys() {
    return KeypadSave.SECRET;
  }

  /**
   * A code typed on the keypad screen: the entry of the domain, with the warning to the owner past
   * the free tries (answers 663 to 666 and 914).
   */
  public KeypadEntry enter(ServerPlayer player, KeypadCode code) {
    long now = player.serverLevel().getGameTime();
    KeypadLimits limits = new NeoForgeKeypadLimits();
    boolean owner = isOwner(player);
    KeypadTry attempt = new KeypadTry(new PlayerId(player.getUUID()), owner, code, powered, now);
    KeypadEntry entry = keypad.pruned(now, limits).enter(attempt, limits);
    setKeypad(entry.keypad());
    if (entry.warnsOwner() && module().isPresent()) {
      RedweaveServer.running()
          .ifPresent(server ->
              KeypadWarnings.missed(
                  player.server, server.modules(), module().get(), player, entry.misses()));
    }
    return entry;
  }

  /** An empty hand opens the keypad screen, only with power and a code (answers 660 to 662). */
  @Override
  public void emptyHand(Player player) {
    if (!powered) {
      player.displayClientMessage(Component.translatable(ModuleMessages.KEYPAD_NO_POWER), true);
      return;
    }
    if (keypad.secret().isEmpty()) {
      player.displayClientMessage(Component.translatable(ModuleMessages.KEYPAD_NO_CODE), true);
      return;
    }
    if (player instanceof ServerPlayer viewer) {
      PartSpot spot = spot();
      viewer.openMenu(
          new SimpleMenuProvider(
              (id, inventory, opener) -> new KeypadEntryMenu(id, viewer, spot),
              item().getDescription()),
          extra -> KeypadEntryMenu.writeOpening(extra, spot));
    }
  }

  public SignalKeypad keypad() {
    return keypad;
  }

  /** Whether its input channel has a signal now. */
  public boolean inputOn() {
    return powered;
  }

  public Connection input() {
    return input;
  }

  @Override
  public Connection output() {
    return output;
  }

  /** The label shows the input channel, where its power comes from (answer 908). */
  @Override
  public Connection labelConnection() {
    return input;
  }

  /** The `Toggle` opens it for its open time, and says so with its name (answer 667). */
  @Override
  public Optional<Component> toggle() {
    setKeypad(keypad.toggle());
    return module()
        .map(found -> Component.translatable(ModuleMessages.KEYPAD_UNLOCKED, found.displayName()));
  }

  /** `Locked` or `Unlocked`, never the code (answer 908). */
  @Override
  public Optional<String> stateKey() {
    return Optional.of(KeypadScreenTexts.of(keypad.unlocked()));
  }

  /** Nobody breaks it by hand in survival, not even its owner (answers 641 and 667). */
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
            (id, inventory, opener) -> new KeypadMenu(id, viewer, spot), item().getDescription()),
        extra -> KeypadMenu.writeOpening(extra, viewer, spot));
  }

  /** Keeps the keypad after a change and shows it at once. */
  public void setKeypad(SignalKeypad next) {
    keypad = next;
    changed();
  }

  /** The owner chose the channel of the input; it is never the edge (answer 661). */
  public void connectInput(Connection connection) {
    input = connection;
    changed();
  }

  /** The owner moved the output to its edge or to a channel (answer 663). */
  public void connectOutput(Connection connection) {
    output = connection;
    changed();
  }

  /** On the server, a loaded keypad fits the open time of the config of now (425, rule 14). */
  @Override
  public void loaded(PartSpaceBlockEntity space) {
    KeypadLimits limits = new NeoForgeKeypadLimits();
    SignalKeypad fitted = keypad.withOpen(keypad.openTicks(), limits);
    if (module().isPresent() && fitted.openTicks() != keypad.openTicks()) {
      LOGGER.warn(FITTED, space.getBlockPos(), keypad.openTicks());
      keypad = fitted;
      markDirty();
    }
  }
}
