package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.application.ModuleSighting;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.module.SignalTimer;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Each tick of a loaded Signal Timer: it reads its `Pause`, ticks its timer in the domain, emits on
 * its front edge or is sighted on its channel, and lights its LEDs (signal-timer.md). An unloaded
 * timer gets no tick, so it freezes where it is and is not sighted (answers 381 and 386).
 */
final class TimerTicks {
  private TimerTicks() {}

  static void tick(Level level, BlockPos pos, BlockState state, SignalTimerBlockEntity block) {
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (server.isEmpty() || block.module().isEmpty() || block.timer().isEmpty()) {
      return;
    }
    ModuleServices modules = server.get().modules();
    BlockLocation plate = BlockLocations.of(level, pos);
    if (modules.show().show(block.module().get(), plate).gone()) {
      block.loseNetwork();
    }
    Signal pause = pauseOf(modules, level, pos, state, block, plate);
    SignalTimer next = block.timer().get().tick(pause);
    block.setTimer(next);
    sight(modules, block, plate, next.output());
    Signal edge = block.output().isEdge() ? next.output() : Signal.OFF;
    emit(level, pos, state, block, edge);
    light(level, pos, state, next);
  }

  /** The `Pause` from the back edge or from a channel of the last tick (412 and 387). */
  private static Signal pauseOf(
      ModuleServices modules,
      Level level,
      BlockPos pos,
      BlockState state,
      SignalTimerBlockEntity block,
      BlockLocation plate) {
    Connection pause = block.pause();
    if (pause.isEdge()) {
      return fromBehind(level, pos, state.getValue(SignalTimerBlock.FRONT).getOpposite());
    }
    return modules.read().read(block.module().get(), plate, pause.channel().orElseThrow());
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

  /** With its output on a channel of its network, the timer writes into it this tick (387). */
  private static void sight(
      ModuleServices modules, SignalTimerBlockEntity block, BlockLocation plate, Signal output) {
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
      Level level, BlockPos pos, BlockState state, SignalTimerBlockEntity block, Signal signal) {
    if (block.emitted() == signal.strength()) {
      return;
    }
    block.setEmitted(signal.strength());
    ModulePlateBlock.wakeFront(level, pos, state);
  }

  /** The LEDs, only sent to the clients, which do not wake the neighbors (answer 401). */
  private static void light(Level level, BlockPos pos, BlockState state, SignalTimer timer) {
    BlockState lit =
        state
            .setValue(SignalTimerBlock.POWERED, timer.output().isOn())
            .setValue(SignalTimerBlock.RUNNING, timer.isRunning());
    if (lit != state) {
      level.setBlock(pos, lit, Block.UPDATE_CLIENTS);
    }
  }
}
