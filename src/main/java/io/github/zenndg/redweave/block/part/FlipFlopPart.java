package io.github.zenndg.redweave.block.part;

import io.github.zenndg.redweave.block.ModuleMessages;
import io.github.zenndg.redweave.block.PartSpaceBlockEntity;
import io.github.zenndg.redweave.domain.module.Connection;
import io.github.zenndg.redweave.domain.module.FlipFlopInput;
import io.github.zenndg.redweave.domain.module.SignalFlipFlop;
import io.github.zenndg.redweave.domain.signal.DelayedOutput;
import io.github.zenndg.redweave.domain.signal.OutputDelay;
import io.github.zenndg.redweave.domain.signal.Signal;
import io.github.zenndg.redweave.menu.FlipFlopMenu;
import io.github.zenndg.redweave.menu.FlipFlopScreenTexts;
import io.github.zenndg.redweave.registry.RedweaveItems;
import io.github.zenndg.redweave.server.ModuleServices;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Item;

/**
 * The Signal Flip-Flop as a part: _docs/funcionalidades/signal-flip-flop.md. Its rule, its two
 * connections and its delay are in its NBT, none in the save of the network (answers 387 and 886).
 * The rule stays in the domain.
 */
public final class FlipFlopPart extends ModulePart implements DelayedPart {
  public static final String ID = "signal_flip_flop";

  /** The looks of the top: the arrow off or lit (answer 888). */
  public static final List<String> LOOKS = List.of(ModuleLooks.NONE, ModuleLooks.ON);

  private final OutputDelaySlot delay = new OutputDelaySlot();
  private SignalFlipFlop flipFlop = SignalFlipFlop.placed();
  private Connection input = Connection.EDGE;
  private Connection output = Connection.EDGE;

  private FlipFlopPart(Direction face) {
    super(face);
  }

  /** A flip-flop on the side of its space, from its NBT, or a new one from an empty tag. */
  public static FlipFlopPart load(Direction face, CompoundTag tag) {
    FlipFlopPart part = new FlipFlopPart(face);
    part.loadPlate(tag);
    if (!tag.isEmpty()) {
      part.readRule(tag);
    }
    return part;
  }

  /** What the Configurator copies: its settings, never its state nor what waits (answer 887). */
  @Override
  protected List<String> copiedKeys() {
    return List.of(
        FlipFlopSave.INPUT, FlipFlopSave.OUTPUT, StrengthSave.STRENGTH, OutputDelaySlot.COPIED);
  }

  @Override
  protected void dropChannels(ConnectionMapper drop) {
    input = drop.of(input);
    output = drop.of(output);
  }

  /**
   * Reads its rule from its save, as it loads and as the Configurator pastes; a paste merges only
   * the copied keys into its own save, so it keeps its state (answers 886 and 887).
   */
  @Override
  protected void readRule(CompoundTag tag) {
    input = ModuleSave.readConnection(tag, FlipFlopSave.INPUT);
    output = ModuleSave.readConnection(tag, FlipFlopSave.OUTPUT);
    flipFlop = FlipFlopSave.readFlipFlop(tag);
    delay.load(tag, ID, flipFlop.strength());
  }

  @Override
  protected String id() {
    return ID;
  }

  @Override
  public Item item() {
    return RedweaveItems.SIGNAL_FLIP_FLOP.get();
  }

  @Override
  protected String lookSuffix() {
    return powered() ? ModuleLooks.ON : ModuleLooks.NONE;
  }

  /** A new flip-flop, off, on its edges, with its input not read yet (answers 878 and 881). */
  @Override
  protected void placeRule() {
    flipFlop = SignalFlipFlop.placed();
    input = Connection.EDGE;
    output = Connection.EDGE;
    delay.place();
  }

  /**
   * Each rising edge of the input flips it in this tick, and its output waits for the delay; a
   * channel that is not available keeps the last reading (answers 879, 889 and 892).
   */
  @Override
  protected void tickRule(ModuleServices modules, PartSpaceBlockEntity space) {
    Optional<Signal> reading = ModuleTicks.reading(modules, space, this, input);
    SignalFlipFlop next = flipFlop.tick(reading);
    boolean saved = !next.equals(flipFlop);
    flipFlop = next;
    if (delay.tick(next.output(), next.strength()) || saved) {
      markDirty();
    }
    ModuleTicks.write(modules, space, this, delay.output());
  }

  @Override
  protected void saveRule(CompoundTag tag) {
    ModuleSave.writeConnection(tag, FlipFlopSave.INPUT, input);
    ModuleSave.writeConnection(tag, FlipFlopSave.OUTPUT, output);
    FlipFlopSave.writeFlipFlop(tag, flipFlop);
    delay.save(tag);
  }

  @Override
  public DelayedOutput delayed() {
    return delay.delayed();
  }

  @Override
  public void setDelay(OutputDelay next) {
    delay.set(next);
    changed();
  }

  public SignalFlipFlop flipFlop() {
    return flipFlop;
  }

  /** Whether the last reading of the input had a signal, for the diagram (answer 884). */
  public boolean inputOn() {
    return flipFlop.input() == FlipFlopInput.ON;
  }

  public Connection input() {
    return input;
  }

  @Override
  public Connection output() {
    return output;
  }

  /** The `Toggle` works with the network `Offline`, as on the Signal Lamp (answer 882). */
  @Override
  public boolean togglesOffline() {
    return true;
  }

  /** The `Toggle` flips it as an edge would, and says so with its name (answer 882). */
  @Override
  public Optional<Component> toggle() {
    setFlipFlop(flipFlop.toggle());
    String key = flipFlop.on() ? ModuleMessages.FLIP_FLOP_ON : ModuleMessages.FLIP_FLOP_OFF;
    return module().map(found -> Component.translatable(key, found.displayName()));
  }

  /** `On` or `Off`, the state kept now, even while the output waits (answers 851 and 890). */
  @Override
  public Optional<String> stateKey() {
    return Optional.of(FlipFlopScreenTexts.of(flipFlop.on()));
  }

  @Override
  public void openScreen(ServerPlayer viewer) {
    PartSpot spot = spot();
    viewer.openMenu(
        new SimpleMenuProvider(
            (id, inventory, opener) -> new FlipFlopMenu(id, viewer, spot), item().getDescription()),
        extra -> FlipFlopMenu.writeOpening(extra, viewer, spot));
  }

  /** The owner flipped it or changed its strength (answers 880 and 882). */
  public void setFlipFlop(SignalFlipFlop next) {
    flipFlop = next;
    changed();
  }

  /** The owner moved the input to its edge or to a channel (answers 878 and 884). */
  public void connectInput(Connection connection) {
    input = connection;
    changed();
  }

  /** The owner moved the output to its edge or to a channel (answers 878 and 884). */
  public void connectOutput(Connection connection) {
    output = connection;
    changed();
  }
}
