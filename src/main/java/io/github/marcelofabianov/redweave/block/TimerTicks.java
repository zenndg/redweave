package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.domain.module.SignalTimer;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.server.ModuleServices;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Each tick of a loaded Signal Timer: it reads its `Pause`, ticks its timer in the domain, emits on
 * its front edge or is sighted on its channel, and lights its LEDs (signal-timer.md). An unloaded
 * timer gets no tick, so it freezes where it is and is not sighted (answers 381 and 386).
 */
final class TimerTicks {
  private TimerTicks() {}

  static void tick(Level level, BlockPos pos, BlockState state, SignalTimerBlockEntity block) {
    Optional<ModuleServices> modules = ModuleTicks.start(level, pos, block);
    if (modules.isEmpty() || block.timer().isEmpty()) {
      return;
    }
    Signal pause = ModuleTicks.read(modules.get(), level, pos, state, block, block.pause());
    SignalTimer next = block.timer().get().tick(pause);
    block.setTimer(next);
    ModuleTicks.write(modules.get(), level, pos, state, block, next.output());
    light(level, pos, state, next);
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
