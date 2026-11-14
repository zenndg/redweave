package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.application.ModuleSighting;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.ModuleInput;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.server.ModuleServices;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * What the tick of every loaded Signal Module shares: it lets a deleted network go, reads an input
 * from the back edge or a channel, and writes its output on the front edge or is sighted on a
 * channel (answers 375, 385, 387, 407 and 412). Each module adds its rule between the two.
 */
final class ModuleTicks {
  private ModuleTicks() {}

  /** The services of the modules, after a deleted network is let go, or none to skip the tick. */
  static Optional<ModuleServices> start(Level level, BlockPos pos, ModuleBlockEntity block) {
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (server.isEmpty() || block.module().isEmpty()) {
      return Optional.empty();
    }
    ModuleServices modules = server.get().modules();
    if (modules.show().show(block.module().get(), BlockLocations.of(level, pos)).gone()) {
      block.loseNetwork();
    }
    return Optional.of(modules);
  }

  /** An input from the back edge, or from a channel of the last tick (answers 387 and 412). */
  static Signal read(
      ModuleServices modules,
      Level level,
      BlockPos pos,
      BlockState state,
      ModuleBlockEntity block,
      Connection input) {
    if (input.isEdge()) {
      return fromBehind(level, pos, state.getValue(ModulePlateBlock.FRONT).getOpposite());
    }
    BlockLocation plate = BlockLocations.of(level, pos);
    return modules.read().read(block.module().get(), plate, input.channel().orElseThrow());
  }

  /** The output on the front edge, or sighted on its channel with the edge off (376 and 387). */
  static void write(
      ModuleServices modules,
      Level level,
      BlockPos pos,
      BlockState state,
      ModuleBlockEntity block,
      Signal output) {
    sight(modules, block, BlockLocations.of(level, pos), output);
    emit(level, pos, state, block, block.output().isEdge() ? output : Signal.OFF);
  }

  /**
   * What reaches the back edge, as the input of a repeater: the signal of the block behind, or the
   * power of a wire there whatever its shape (answer 412). An unloaded block gives none (rule 6).
   */
  private static Signal fromBehind(Level level, BlockPos pos, Direction back) {
    BlockPos behind = pos.relative(back);
    if (!level.isLoaded(behind)) {
      return Signal.OFF;
    }
    int strength = level.getSignal(behind, back);
    BlockState wire = level.getBlockState(behind);
    if (wire.is(Blocks.REDSTONE_WIRE)) {
      strength = Math.max(strength, wire.getValue(RedStoneWireBlock.POWER));
    }
    return Signal.clamped(strength);
  }

  /** With its output on a channel of its network, the module writes into it this tick (387). */
  private static void sight(
      ModuleServices modules, ModuleBlockEntity block, BlockLocation plate, Signal output) {
    SignalModule module = block.module().get();
    Optional<ChannelColor> channel = block.output().channel();
    if (channel.isEmpty() || module.network().isEmpty()) {
      return;
    }
    ModuleInput input = new ModuleInput(module.id(), plate, channel.get(), output);
    modules
        .sightings()
        .add(new ModuleSighting(module.network().get(), input, module.displayName()));
  }

  /**
   * Feeds the block in front with strength, as a repeater, and wakes it only when the value
   * changes, so there are no cascading block updates (answer 407 and rule 18).
   */
  private static void emit(
      Level level, BlockPos pos, BlockState state, ModuleBlockEntity block, Signal signal) {
    if (block.emitted() == signal.strength()) {
      return;
    }
    block.setEmitted(signal.strength());
    ModulePlateBlock.wakeFront(level, pos, state);
  }
}
