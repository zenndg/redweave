package io.github.marcelofabianov.redweave.block.part;

import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.config.NeoForgeInverterLimits;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.InverterLimits;
import io.github.marcelofabianov.redweave.domain.module.InverterSettings;
import io.github.marcelofabianov.redweave.domain.module.InverterState;
import io.github.marcelofabianov.redweave.domain.module.SignalInverter;
import io.github.marcelofabianov.redweave.domain.signal.DelayLine;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.menu.InverterMenu;
import io.github.marcelofabianov.redweave.menu.InverterScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.ModuleServices;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Item;

/**
 * The Signal Inverter as a part: _docs/funcionalidades/signal-inverter.md. Its rule and its two
 * connections are in its NBT, none in the save of the network (answers 387 and 450), and its burnt
 * look comes from what is left of its burnout (answer 455). The rule stays in the domain.
 */
public final class InverterPart extends ModulePart {
  public static final String ID = "signal_inverter";

  /** The looks of the top: off, on or burnt, which wins over on (answers 452 and 455). */
  public static final List<String> LOOKS =
      List.of(ModuleLooks.NONE, ModuleLooks.ON, ModuleLooks.BURNT);

  private static final InverterLimits LIMITS = new NeoForgeInverterLimits();

  private SignalInverter inverter = SignalInverter.placed();
  private Connection input = Connection.EDGE;
  private Connection output = Connection.EDGE;

  /** The input on the last tick, for the diagram of the screen; not saved (answer 450). */
  private boolean inputOn;

  private InverterPart(Direction face) {
    super(face);
  }

  /** An inverter on the side of its space, from its NBT, or a new one from an empty tag. */
  public static InverterPart load(Direction face, CompoundTag tag) {
    InverterPart part = new InverterPart(face);
    part.loadPlate(tag);
    if (!tag.isEmpty()) {
      part.readRule(tag);
    }
    return part;
  }

  /** What the Configurator copies: its settings, never what it is doing now (answer 725). */
  @Override
  protected List<String> copiedKeys() {
    return List.of(InverterSave.INPUT, InverterSave.OUTPUT, StrengthSave.STRENGTH, DelaySave.DELAY);
  }

  @Override
  protected void dropChannels(ConnectionMapper drop) {
    input = drop.of(input);
    output = drop.of(output);
  }

  /** Reads its rule from its save, as it loads and as the Configurator pastes (answer 725). */
  @Override
  protected void readRule(CompoundTag tag) {
    input = ModuleSave.readConnection(tag, InverterSave.INPUT);
    output = ModuleSave.readConnection(tag, InverterSave.OUTPUT);
    InverterSettings settings =
        new InverterSettings(StrengthSave.read(tag, ID), DelaySave.readDelay(tag, ID));
    DelayLine line =
        tag.contains(DelaySave.LINE)
            ? DelaySave.readLine(tag, ID, settings.strength())
            : inverter.line();
    inverter = SignalInverter.restore(InverterSave.readBurnout(tag), settings, line);
  }

  @Override
  protected String id() {
    return ID;
  }

  @Override
  public Item item() {
    return RedweaveItems.SIGNAL_INVERTER.get();
  }

  @Override
  protected String lookSuffix() {
    if (inverter.state() == InverterState.BURNT_OUT) {
      return ModuleLooks.BURNT;
    }
    return powered() ? ModuleLooks.ON : ModuleLooks.NONE;
  }

  /** A new inverter, on its edges, which emits on its first tick (answers 441 and 442). */
  @Override
  protected void placeRule() {
    inverter = SignalInverter.placed();
    input = Connection.EDGE;
    output = Connection.EDGE;
  }

  /** The NOT of its input, with the delay of the edge or of the channel (answers 444 and 446). */
  @Override
  protected void tickRule(ModuleServices modules, PartSpaceBlockEntity space) {
    Signal read = ModuleTicks.read(modules, space, this, input);
    int delay = input.isEdge() ? SignalInverter.EDGE_DELAY : SignalInverter.CHANNEL_DELAY;
    SignalInverter next = inverter.tick(read, delay, LIMITS, ModuleDelays.LINE_LIMITS);
    setInverter(next, read);
    ModuleTicks.write(modules, space, this, next.output());
  }

  @Override
  protected void saveRule(CompoundTag tag) {
    ModuleSave.writeConnection(tag, InverterSave.INPUT, input);
    ModuleSave.writeConnection(tag, InverterSave.OUTPUT, output);
    InverterSave.writeBurnout(tag, inverter.burnoutTicks());
    StrengthSave.write(tag, inverter.settings().strength());
    DelaySave.writeDelay(tag, inverter.settings().delay());
    DelaySave.writeLine(tag, inverter.line());
  }

  public SignalInverter inverter() {
    return inverter;
  }

  public boolean inputOn() {
    return inputOn;
  }

  public Connection input() {
    return input;
  }

  @Override
  public Connection output() {
    return output;
  }

  /** The inverter has nothing to toggle, and says so (answers 443 and 447). */
  @Override
  public Optional<Component> toggle() {
    return Optional.of(Component.translatable(ModuleMessages.INVERTER_NOTHING_TO_TOGGLE));
  }

  /** `On`, `Off` or `Burnt out` (answer 448). */
  @Override
  public Optional<String> stateKey() {
    return Optional.of(stateKeyOf(inverter.state()));
  }

  private static String stateKeyOf(InverterState state) {
    return switch (state) {
      case ON -> InverterScreenTexts.ON;
      case OFF -> InverterScreenTexts.OFF;
      case BURNT_OUT -> InverterScreenTexts.BURNT_OUT;
    };
  }

  @Override
  public void openScreen(ServerPlayer viewer) {
    PartSpot spot = spot();
    viewer.openMenu(
        new SimpleMenuProvider(
            (id, inventory, opener) -> new InverterMenu(id, viewer, spot), item().getDescription()),
        extra -> InverterMenu.writeOpening(extra, viewer, spot));
  }

  /** Keeps the rule and the input after a tick; saves only when the burnout or the line changed. */
  void setInverter(SignalInverter next, Signal read) {
    inputOn = read.isOn();
    boolean saved =
        next.burnoutTicks() != inverter.burnoutTicks() || !next.line().equals(inverter.line());
    inverter = next;
    if (saved) {
      markDirty();
    }
  }

  /** The owner changed the strength or the delay; what waits in the line stays (839 and 843). */
  public void setSettings(SignalInverter next) {
    inverter = next;
    changed();
  }

  /** The owner moved the input to its edge or to a channel (answers 441 and 451). */
  public void connectInput(Connection connection) {
    input = connection;
    changed();
  }

  /** The owner moved the output to its edge or to a channel (answers 441 and 451). */
  public void connectOutput(Connection connection) {
    output = connection;
    changed();
  }
}
