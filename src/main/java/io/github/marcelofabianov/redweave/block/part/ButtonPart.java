package io.github.marcelofabianov.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.config.NeoForgeButtonLimits;
import io.github.marcelofabianov.redweave.domain.module.ButtonLimits;
import io.github.marcelofabianov.redweave.domain.module.ButtonTime;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.SignalButton;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.menu.ButtonMenu;
import io.github.marcelofabianov.redweave.menu.ButtonScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.ModuleServices;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
public final class ButtonPart extends ModulePart {
  public static final String ID = "signal_button";

  /** The looks of the top: released, with the red light, and pressed, with the green one (595). */
  public static final List<String> LOOKS = List.of(ModuleLooks.NONE, ModuleLooks.ON);

  private static final float VOLUME = 1.0F;
  private static final float PITCH = 1.0F;
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String FITTED = "Fitted the Signal Button at {} to the config: {}";

  private SignalButton button =
      new SignalButton(false, Signal.MAX, new ButtonTime(ButtonTime.PLACED_TICKS), 0);
  private Connection output = Connection.EDGE;

  private ButtonPart(Direction face) {
    super(face);
  }

  /** A button on the side of its space, from its NBT, or a new one from an empty tag. */
  public static ButtonPart load(Direction face, CompoundTag tag) {
    ButtonPart part = new ButtonPart(face);
    part.loadPlate(tag);
    if (!tag.isEmpty()) {
      part.button = ButtonSave.readButton(tag);
      part.output = ModuleSave.readConnection(tag, ButtonSave.OUTPUT);
    }
    return part;
  }

  @Override
  protected String id() {
    return ID;
  }

  @Override
  public Item item() {
    return RedweaveItems.SIGNAL_BUTTON.get();
  }

  /** The light and the push button follow the button, never what reaches its channel (581). */
  @Override
  protected String lookSuffix() {
    return button.on() ? ModuleLooks.ON : ModuleLooks.NONE;
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
    output = Connection.EDGE;
  }

  /**
   * Gives what the button holds, each tick, on its edges or its channel (553), and counts its on
   * time down; the last tick releases it with the click of the stone button (answer 589).
   */
  @Override
  protected void tickRule(ModuleServices modules, PartSpaceBlockEntity space) {
    ModuleTicks.write(modules, space, this, button.output());
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
  protected boolean feedsSupport() {
    return true;
  }

  @Override
  public boolean togglesOffline() {
    return true;
  }

  @Override
  protected void saveRule(CompoundTag tag) {
    ButtonSave.writeButton(tag, button);
    ModuleSave.writeConnection(tag, ButtonSave.OUTPUT, output);
  }

  public SignalButton button() {
    return button;
  }

  @Override
  public Connection output() {
    return output;
  }

  /** The `Toggle` presses it, and says so with its name (answers 555 and 593). */
  @Override
  public Optional<Component> toggle() {
    setButton(button.press());
    return module()
        .map(found -> Component.translatable(ModuleMessages.BUTTON_PRESSED, found.displayName()));
  }

  /** The hand of the owner presses it; anyone else is told whose it is (576, 577 and 588). */
  @Override
  public void emptyHand(Player player) {
    if (!isOwner(player)) {
      player.displayClientMessage(Component.translatable(ModuleMessages.NOT_OWNER), true);
      return;
    }
    setButton(button.press());
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

  /** The owner moved the output to its edges or to a channel (answers 548 and 549). */
  public void connectOutput(Connection connection) {
    output = connection;
    changed();
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
    level()
        .ifPresent(found ->
            found.playSound(
                null,
                spot().pos(),
                pressed ? SoundEvents.STONE_BUTTON_CLICK_ON : SoundEvents.STONE_BUTTON_CLICK_OFF,
                SoundSource.BLOCKS,
                VOLUME,
                PITCH));
  }
}
