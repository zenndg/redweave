package io.github.marcelofabianov.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.config.NeoForgeButtonLimits;
import io.github.marcelofabianov.redweave.domain.module.ButtonLimits;
import io.github.marcelofabianov.redweave.domain.module.ButtonTime;
import io.github.marcelofabianov.redweave.domain.module.Emitter;
import io.github.marcelofabianov.redweave.domain.module.SignalButton;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.menu.ButtonMenu;
import io.github.marcelofabianov.redweave.menu.ButtonScreenTexts;
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
import org.slf4j.Logger;

/**
 * The Signal Button as a part: _docs/funcionalidades/botao.md. A source Signal Module that the
 * owner presses by hand, with any item but the Configurator, or with its `Toggle`, even with the
 * network `Offline`, and that turns off on its own after its on time (answers 549, 556 and 588).
 * On, it feeds its block of support and the block in front with strength, or writes on its channel
 * (answer 553). Only the player presses it: no arrow nor other entity does (answer 594).
 */
public final class ButtonPart extends EmitterPart {
  public static final String ID = "signal_button";

  /** The looks of the top: released, with the red light, and pressed, with the green one (595). */
  public static final List<String> LOOKS = EmitterPart.LOOKS;

  private static final float VOLUME = 1.0F;
  private static final float PITCH = 1.0F;
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String FITTED = "Fitted the Signal Button at {} to the config: {}";

  private SignalButton button =
      new SignalButton(false, Signal.MAX, new ButtonTime(ButtonTime.PLACED_TICKS), 0);

  private ButtonPart(Direction face) {
    super(face);
  }

  /** A button on the side of its space, from its NBT, or a new one from an empty tag. */
  public static ButtonPart load(Direction face, CompoundTag tag) {
    ButtonPart part = new ButtonPart(face);
    part.loadPlate(tag);
    if (!tag.isEmpty()) {
      part.readRule(tag);
    }
    return part;
  }

  /** What the Configurator copies: its settings, never what it is doing now (answer 725). */
  @Override
  protected List<String> copiedKeys() {
    return List.of(
        StrengthSave.STRENGTH, ButtonSave.INTERVAL, EmitterPart.OUTPUT, OutputDelaySlot.COPIED);
  }

  /** Reads its rule from its save, as it loads and as the Configurator pastes (answer 725). */
  @Override
  protected void readRule(CompoundTag tag) {
    button = ButtonSave.readButton(tag);
    loadOutput(tag);
  }

  @Override
  protected String id() {
    return ID;
  }

  @Override
  public Item item() {
    return RedweaveItems.SIGNAL_BUTTON.get();
  }

  /** The plate and the push button over it, so the button can be aimed at too. */
  @Override
  public VoxelShape shape() {
    return Shapes.or(super.shape(), ButtonShapes.of(face()));
  }

  /** A button just placed: off, at 15, with 1 s of on time, on its edge (552 and 556). */
  @Override
  protected void placeRule() {
    button = SignalButton.placed(new NeoForgeButtonLimits());
    placeOutput();
  }

  @Override
  protected Emitter emitter() {
    return button;
  }

  /**
   * After its output, it counts its on time down; the last tick releases it with the click of the
   * stone button (answer 589).
   */
  @Override
  protected void afterOutput(PartSpaceBlockEntity space) {
    SignalButton next = button.tick();
    if (next.equals(button)) {
      return;
    }
    button = next;
    markDirty();
    if (!next.on()) {
      click(false);
    }
  }

  @Override
  protected void writeEmitter(CompoundTag tag) {
    ButtonSave.writeButton(tag, button);
  }

  public SignalButton button() {
    return button;
  }

  /** The `Toggle` presses it, and says so with its name (answers 555 and 593). */
  @Override
  public Optional<Component> toggle() {
    setButton(button.press());
    return module()
        .map(found -> Component.translatable(ModuleMessages.BUTTON_PRESSED, found.displayName()));
  }

  /** A Signal Remote presses it, as the `Toggle`, with its own on time (answer 781). */
  @Override
  public Optional<Component> fireRemotely(int pulseTicks) {
    return toggle();
  }

  /** The hand of the owner presses it; anyone else is told whose it is (576, 577 and 588). */
  @Override
  public void emptyHand(Player player) {
    byOwner(player, () -> setButton(button.press()));
  }

  @Override
  public boolean takesAnyItem() {
    return true;
  }

  /** `On` or `Off`, as the Signal Lever (answer 595). */
  @Override
  public Optional<String> stateKey() {
    return Optional.of(ButtonScreenTexts.of(button.on()));
  }

  @Override
  public void openScreen(ServerPlayer viewer) {
    PartSpot spot = spot();
    viewer.openMenu(
        new SimpleMenuProvider(
            (id, inventory, opener) -> new ButtonMenu(id, viewer, spot), item().getDescription()),
        extra -> ButtonMenu.writeOpening(extra, viewer, spot));
  }

  /** Keeps the button after a change and shows it at once; a press clicks (answer 589). */
  public void setButton(SignalButton next) {
    boolean pressed = next.on() && !button.on();
    button = next;
    changed();
    if (pressed) {
      click(true);
    }
  }

  /**
   * On the server, a loaded button fits the limits of the config of now: an on time outside them
   * goes to the nearest limit, with a warning (answers 425 and 592, rule 14).
   */
  @Override
  public void loaded(PartSpaceBlockEntity space) {
    ButtonLimits limits = new NeoForgeButtonLimits();
    if (module().isPresent() && !button.fits(limits)) {
      LOGGER.warn(FITTED, space.getBlockPos(), button.time().ticks());
      button = button.fittedTo(limits);
      markDirty();
    }
  }

  /** The click of the stone button of the game: on when pressed, off when released (589). */
  private void click(boolean pressed) {
    click(
        pressed ? SoundEvents.STONE_BUTTON_CLICK_ON : SoundEvents.STONE_BUTTON_CLICK_OFF,
        VOLUME,
        PITCH);
  }
}
