package io.github.marcelofabianov.redweave.block.part;

import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.domain.module.Emitter;
import io.github.marcelofabianov.redweave.domain.module.SignalLever;
import io.github.marcelofabianov.redweave.menu.LeverMenu;
import io.github.marcelofabianov.redweave.menu.LeverScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The Signal Lever as a part: _docs/funcionalidades/alavanca.md. A source Signal Module that the
 * owner turns on and off by hand, with any item but the Configurator, or with its `Toggle`, even
 * with the network `Offline` (answers 549, 576, 577 and 578). On, it feeds its block of support and
 * the block in front with strength, or writes on its channel (answer 553). Its light and its handle
 * show whether it is on (answers 554 and 581).
 */
public final class LeverPart extends EmitterPart {
  public static final String ID = "signal_lever";

  /** The looks of the top: off, with the red light, and on, with the green one (554). */
  public static final List<String> LOOKS = EmitterPart.LOOKS;

  private static final float VOLUME = 0.3F;
  private static final float PITCH_ON = 0.6F;
  private static final float PITCH_OFF = 0.5F;

  private SignalLever lever = SignalLever.placed();

  private LeverPart(Direction face) {
    super(face);
  }

  /** A lever on the side of its space, from its NBT, or a new one from an empty tag. */
  public static LeverPart load(Direction face, CompoundTag tag) {
    LeverPart part = new LeverPart(face);
    part.loadPlate(tag);
    if (!tag.isEmpty()) {
      part.lever = LeverSave.readLever(tag);
      part.loadOutput(tag);
    }
    return part;
  }

  @Override
  protected String id() {
    return ID;
  }

  @Override
  public Item item() {
    return RedweaveItems.SIGNAL_LEVER.get();
  }

  /** The plate and the handle over it, so the handle can be aimed at too. */
  @Override
  public VoxelShape shape() {
    return Shapes.or(super.shape(), LeverShapes.of(face()));
  }

  /** A lever just placed: off, at 15, on its edge (answer 552). */
  @Override
  protected void placeRule() {
    lever = SignalLever.placed();
    placeOutput();
  }

  @Override
  protected Emitter emitter() {
    return lever;
  }

  @Override
  protected void writeEmitter(CompoundTag tag) {
    LeverSave.writeLever(tag, lever);
  }

  public SignalLever lever() {
    return lever;
  }

  /** The `Toggle` turns it on or off, and says so with its name, as the timer (555 and 421). */
  @Override
  public Optional<Component> toggle() {
    setLever(lever.toggle());
    String key = lever.on() ? ModuleMessages.LEVER_ON : ModuleMessages.LEVER_OFF;
    return module().map(found -> Component.translatable(key, found.displayName()));
  }

  /** The hand of the owner turns it on or off; anyone else is told whose it is (576 and 577). */
  @Override
  public void emptyHand(Player player) {
    byOwner(player, () -> setLever(lever.toggle()));
  }

  @Override
  public boolean takesAnyItem() {
    return true;
  }

  /** `On` or `Off` (answer 582). */
  @Override
  public Optional<String> stateKey() {
    return Optional.of(LeverScreenTexts.of(lever.on()));
  }

  @Override
  public void openScreen(ServerPlayer viewer) {
    PartSpot spot = spot();
    viewer.openMenu(
        new SimpleMenuProvider(
            (id, inventory, opener) -> new LeverMenu(id, viewer, spot), item().getDescription()),
        extra -> LeverMenu.writeOpening(extra, viewer, spot));
  }

  /**
   * Keeps the lever after a change and shows it at once; turned on or off, it clicks as the lever
   * of the game, higher on and lower off (answer 580).
   */
  public void setLever(SignalLever next) {
    boolean turned = next.on() != lever.on();
    lever = next;
    changed();
    if (turned) {
      click(SoundEvents.LEVER_CLICK, VOLUME, next.on() ? PITCH_ON : PITCH_OFF);
    }
  }
}
