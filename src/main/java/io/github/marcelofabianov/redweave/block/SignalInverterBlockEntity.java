package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.InverterState;
import io.github.marcelofabianov.redweave.domain.module.SignalInverter;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.menu.InverterMenu;
import io.github.marcelofabianov.redweave.menu.InverterScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveBlockEntities;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.state.BlockState;

/**
 * What a Signal Inverter block keeps besides its module: its rule and its two connections, in its
 * NBT and none in the save of the network (answers 387 and 450). The rule stays in the domain.
 */
public final class SignalInverterBlockEntity extends ModuleBlockEntity {
  private SignalInverter inverter = SignalInverter.placed();
  private Connection input = Connection.EDGE;
  private Connection output = Connection.EDGE;

  /** The input on the last tick, for the diagram of the screen; not saved (answer 450). */
  private boolean inputOn;

  public SignalInverterBlockEntity(BlockPos pos, BlockState state) {
    super(RedweaveBlockEntities.SIGNAL_INVERTER.get(), pos, state);
  }

  /** A new inverter, on its edges, which emits on its first tick (answers 441 and 442). */
  @Override
  void place(SignalModule placed) {
    inverter = SignalInverter.placed();
    input = Connection.EDGE;
    output = Connection.EDGE;
    setModule(placed);
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
    viewer.openMenu(
        new SimpleMenuProvider(
            (id, inventory, opener) -> new InverterMenu(id, viewer, worldPosition),
            RedweaveBlocks.SIGNAL_INVERTER.get().getName()),
        extra -> InverterMenu.writeOpening(extra, viewer, worldPosition));
  }

  /** Keeps the rule and the input after a tick; saves only when the burnout changed. */
  void setInverter(SignalInverter next, Signal input) {
    inputOn = input.isOn();
    boolean burnoutChanged = next.burnoutTicks() != inverter.burnoutTicks();
    inverter = next;
    if (burnoutChanged) {
      setChanged();
    }
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

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    ModuleSave.writeConnection(tag, InverterSave.INPUT, input);
    ModuleSave.writeConnection(tag, InverterSave.OUTPUT, output);
    InverterSave.writeBurnout(tag, inverter.burnoutTicks());
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    input = ModuleSave.readConnection(tag, InverterSave.INPUT);
    output = ModuleSave.readConnection(tag, InverterSave.OUTPUT);
    inverter = SignalInverter.restore(InverterSave.readBurnout(tag));
  }
}
