package io.github.marcelofabianov.redweave.block.part;

import io.github.marcelofabianov.redweave.block.BlockLocations;
import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.config.NeoForgeLampLimits;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.SignalLamp;
import io.github.marcelofabianov.redweave.domain.network.NodeState;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.menu.LampMenu;
import io.github.marcelofabianov.redweave.menu.LeverScreenTexts;
import io.github.marcelofabianov.redweave.menu.NodeScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.ModuleServices;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The Signal Lamp as a part: _docs/funcionalidades/lampada.md. An LED bar on any side of a block,
 * in the color of its intensity channel, whose strength is its light in the world, and which gives
 * the strength of its screen to its block of support while lit, as a Signal Node, and to a cable or
 * the game wire beside it (answers 262, 574 and 650 to 653). An optional control channel turns it
 * off, and its light changes at most every `lamp.min_light_update_ticks` (answers 655 to 657).
 */
public final class LampPart extends ModulePart {
  public static final String ID = "signal_lamp";

  /** The looks: lit by its channel, and switched off by its control, with the red light (657). */
  public static final List<String> LOOKS = List.of(ModuleLooks.NONE, ModuleLooks.OFF);

  private SignalLamp lamp = SignalLamp.placed();
  private Connection intensity = Connection.EDGE;
  private Connection control = Connection.EDGE;
  private int light;

  /** Ticks until the light may change again; not saved, as a new tick starts it again (655). */
  private int lightWait;

  /** The network as the bar sees it, for its label; not saved, as each tick reads it again. */
  private NodeState network = NodeState.NO_NETWORK;

  private LampPart(Direction face) {
    super(face);
  }

  /** A lamp on the side of its space, from its NBT, or a new one from an empty tag. */
  public static LampPart load(Direction face, CompoundTag tag) {
    LampPart part = new LampPart(face);
    part.loadPlate(tag);
    if (!tag.isEmpty()) {
      part.readRule(tag);
    }
    return part;
  }

  /** What the Configurator copies: its settings, never what it is doing now (answer 725). */
  @Override
  protected List<String> copiedKeys() {
    return List.of(StrengthSave.STRENGTH, LampSave.MODE, LampSave.INTENSITY, LampSave.CONTROL);
  }

  @Override
  protected void dropChannels(ChannelDrop drop) {
    intensity = drop.of(intensity);
    control = drop.of(control);
  }

  /** Reads its rule from its save, as it loads and as the Configurator pastes (answer 725). */
  @Override
  protected void readRule(CompoundTag tag) {
    lamp = LampSave.read(tag);
    light = LampSave.readLight(tag);
    intensity = LampSave.readChannel(tag, LampSave.INTENSITY);
    control = LampSave.readChannel(tag, LampSave.CONTROL);
  }

  @Override
  protected String id() {
    return ID;
  }

  @Override
  public Item item() {
    return RedweaveItems.SIGNAL_LAMP.get();
  }

  /** The owner switches the lamp with the `Toggle` even with its network off, as the Lock (716). */
  @Override
  public boolean togglesOffline() {
    return true;
  }

  /** The bar, long along its front, which the Configurator `Rotate` turns (answer 652). */
  @Override
  public VoxelShape shape() {
    return LampShapes.of(face(), front());
  }

  /** The red light while its control keeps it off, from what the client receives (657). */
  @Override
  protected String lookSuffix() {
    return lamp.off(lamp.controlWasOn()) ? ModuleLooks.OFF : ModuleLooks.NONE;
  }

  /** A new lamp: no channel yet, giving 15, with its control in `HOLD` (651 and 656). */
  @Override
  protected void placeRule() {
    lamp = SignalLamp.placed();
    intensity = Connection.EDGE;
    control = Connection.EDGE;
  }

  /** Its channels of this tick, then what it gives and its light (answers 650 to 657). */
  @Override
  protected void tickRule(ModuleServices modules, PartSpaceBlockEntity space) {
    BlockPos pos = space.getBlockPos();
    network = modules.show().show(module().get(), BlockLocations.of(space.getLevel(), pos)).state();
    Signal strength = channel(modules, space, intensity);
    boolean held = channel(modules, space, control).isOn();
    SignalLamp next = lamp.control(held);
    if (!next.equals(lamp)) {
      lamp = next;
      markDirty();
    }
    ModuleTicks.give(space, this, lamp.output(strength, held));
    shine(space, lamp.light(strength, held).strength());
  }

  /** A channel of the lamp, or nothing while it has none (answers 650 and 656). */
  private Signal channel(ModuleServices modules, PartSpaceBlockEntity space, Connection channel) {
    return channel.isEdge() ? Signal.OFF : ModuleTicks.read(modules, space, this, channel);
  }

  /**
   * The light follows the intensity, but changes at most every `lamp.min_light_update_ticks`, and
   * only then lights the world again and goes to the clients (answer 655, rule 18).
   */
  private void shine(PartSpaceBlockEntity space, int target) {
    if (lightWait > 0) {
      lightWait--;
    }
    if (target == light || lightWait > 0) {
      return;
    }
    light = target;
    lightWait = NeoForgeLampLimits.minLightUpdateTicks();
    space.changed();
    space.refreshLight();
  }

  /** Gone, its light leaves the world at once, even if other parts stay in the space (650). */
  @Override
  public void removed(PartSpaceBlockEntity space) {
    super.removed(space);
    light = 0;
    space.refreshLight();
  }

  @Override
  protected void saveRule(CompoundTag tag) {
    LampSave.write(tag, lamp, light);
    LampSave.writeChannel(tag, LampSave.INTENSITY, intensity);
    LampSave.writeChannel(tag, LampSave.CONTROL, control);
  }

  @Override
  public int light() {
    return light;
  }

  /** Only the block of support gets what it gives, with strength, as a Signal Node (653). */
  @Override
  public int signal(Direction side) {
    return side == facing() ? emitted() : 0;
  }

  /** A cable or the game wire beside the space, on the same side, gets it too (answer 574). */
  @Override
  public int signal(BlockGetter level, BlockPos pos, Direction side) {
    Direction toward = side.getOpposite();
    if (toward.getAxis() == face().getAxis()) {
      return signal(side);
    }
    return NodeBeside.takes(level, pos.relative(toward), face()) ? emitted() : signal(side);
  }

  @Override
  public boolean turnsWire(Direction toward) {
    return NodeBeside.turnsWire(face(), toward);
  }

  @Override
  protected boolean feedsSupport() {
    return true;
  }

  public SignalLamp lamp() {
    return lamp;
  }

  public Connection intensity() {
    return intensity;
  }

  public Connection control() {
    return control;
  }

  /** The color of the lamp, the one of its intensity channel, if it has one (answer 650). */
  public Optional<ChannelColor> color() {
    return intensity.channel();
  }

  /** The label shows the intensity channel, the one that gives the lamp its color (650). */
  @Override
  public Connection output() {
    return intensity;
  }

  /** The `Toggle` switches it off or on again, as a pulse of its control (answer 658). */
  @Override
  public Optional<Component> toggle() {
    setLamp(lamp.toggle());
    String key = lamp.switchedOff() ? ModuleMessages.LEVER_OFF : ModuleMessages.LEVER_ON;
    return module().map(found -> Component.translatable(key, found.displayName()));
  }

  /** `Offline` or `Out of range` as a node, or `On` while lit and `Off` while dark (654). */
  @Override
  public Optional<String> stateKey() {
    if (network == NodeState.OFFLINE) {
      return Optional.of(NodeScreenTexts.STATE_OFFLINE);
    }
    if (network == NodeState.OUT_OF_RANGE) {
      return Optional.of(NodeScreenTexts.STATE_OUT_OF_RANGE);
    }
    return Optional.of(LeverScreenTexts.of(powered()));
  }

  @Override
  public void openScreen(ServerPlayer viewer) {
    PartSpot spot = spot();
    viewer.openMenu(
        new SimpleMenuProvider(
            (id, inventory, opener) -> new LampMenu(id, viewer, spot), item().getDescription()),
        extra -> LampMenu.writeOpening(extra, viewer, spot));
  }

  /** Keeps the lamp after a change and shows it at once. */
  public void setLamp(SignalLamp next) {
    lamp = next;
    changed();
  }

  /** The owner moved the intensity to a channel, or to none (answers 650 and 654). */
  public void connectIntensity(Connection connection) {
    intensity = connection;
    changed();
  }

  /** The owner moved the control to a channel, or to none (answer 656). */
  public void connectControl(Connection connection) {
    control = connection;
    changed();
  }
}
