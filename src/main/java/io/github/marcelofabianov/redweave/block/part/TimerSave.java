package io.github.marcelofabianov.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.marcelofabianov.redweave.domain.module.SignalTimer;
import io.github.marcelofabianov.redweave.domain.module.TimerCycle;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import org.slf4j.Logger;

/**
 * The NBT of the cycle of a Signal Timer, under the keys of answer 411, part of the save (rule 15).
 * The module and the connections are in ModuleSave. An invalid cycle is dropped with a warning
 * (14).
 */
final class TimerSave {
  static final String OUTPUT = "output";
  static final String PAUSE = "pause";

  private static final String RUNNING = "running";
  private static final String INTERVAL = "interval_ticks";
  private static final String PULSE = "pulse_ticks";
  private static final String ELAPSED = "elapsed_ticks";

  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String DROPPED = "Dropped an invalid {} from a Signal Timer: {}";

  private TimerSave() {}

  static void writeTimer(CompoundTag tag, SignalTimer timer) {
    tag.putBoolean(RUNNING, timer.isRunning());
    tag.putInt(INTERVAL, timer.cycle().intervalTicks());
    tag.putInt(PULSE, timer.cycle().pulseTicks());
    tag.putInt(ELAPSED, timer.elapsedTicks());
  }

  /**
   * The timer as saved, or none when its cycle is invalid, so the caller starts a new one. The
   * limits of the config are applied later, when the block loads (answer 425).
   */
  static Optional<SignalTimer> readTimer(CompoundTag tag) {
    TimerCycle cycle;
    try {
      cycle = new TimerCycle(tag.getInt(INTERVAL), tag.getInt(PULSE));
    } catch (IllegalArgumentException invalid) {
      LOGGER.warn(DROPPED, INTERVAL, invalid.getMessage());
      return Optional.empty();
    }
    boolean running = tag.getBoolean(RUNNING);
    int elapsed = running ? tag.getInt(ELAPSED) : 0;
    if (elapsed < 0 || elapsed >= cycle.intervalTicks()) {
      LOGGER.warn(DROPPED, ELAPSED, elapsed);
      elapsed = 0;
    }
    return Optional.of(SignalTimer.restore(cycle, running, elapsed));
  }
}
