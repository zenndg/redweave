package io.github.zenndg.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.zenndg.redweave.domain.module.Interval;
import io.github.zenndg.redweave.domain.module.RandomizerMode;
import io.github.zenndg.redweave.domain.module.SignalRandomizer;
import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import org.slf4j.Logger;

/**
 * The NBT of a Signal Randomizer besides its module, under the keys of answer 481, part of the save
 * (rule 15): all of it, so it goes on from where it was. An invalid part is dropped with a warning
 * (rule 14); an invalid interval drops the randomizer, and the caller starts a new one.
 */
final class RandomizerSave {
  static final String OUTPUT = "output";

  static final String MODE = "mode";
  private static final String RUNNING = "running";
  static final String INTERVAL = "interval_ticks";
  private static final String ELAPSED = "elapsed_ticks";
  private static final String VALUE = "value";
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String DROPPED = "Dropped an invalid {} from a Signal Randomizer: {}";

  private RandomizerSave() {}

  static void writeRandomizer(CompoundTag tag, SignalRandomizer randomizer) {
    tag.putString(MODE, randomizer.mode().key());
    tag.putBoolean(RUNNING, randomizer.running());
    tag.putInt(INTERVAL, randomizer.interval().ticks());
    tag.putInt(ELAPSED, randomizer.elapsedTicks());
    tag.putInt(VALUE, randomizer.value().strength());
  }

  /** The randomizer as saved, or none when its interval is invalid. Limits apply on load (425). */
  static Optional<SignalRandomizer> readRandomizer(CompoundTag tag) {
    Interval interval;
    try {
      interval = new Interval(tag.getInt(INTERVAL));
    } catch (IllegalArgumentException invalid) {
      LOGGER.warn(DROPPED, INTERVAL, invalid.getMessage());
      return Optional.empty();
    }
    boolean running = tag.getBoolean(RUNNING);
    return Optional.of(
        new SignalRandomizer(
            mode(tag), interval, running, elapsed(tag, interval), value(tag, running)));
  }

  private static RandomizerMode mode(CompoundTag tag) {
    String key = tag.getString(MODE);
    Optional<RandomizerMode> mode = RandomizerMode.fromKey(key);
    if (mode.isEmpty()) {
      LOGGER.warn(DROPPED, MODE, key);
      return RandomizerMode.BINARY;
    }
    return mode.get();
  }

  private static int elapsed(CompoundTag tag, Interval interval) {
    int elapsed = tag.getInt(ELAPSED);
    if (elapsed < 0 || elapsed >= interval.ticks()) {
      LOGGER.warn(DROPPED, ELAPSED, elapsed);
      return 0;
    }
    return elapsed;
  }

  /** The value on the output; a stopped randomizer holds none (answer 480). */
  private static Signal value(CompoundTag tag, boolean running) {
    int strength = tag.getInt(VALUE);
    if (strength < Signal.OFF.strength() || strength > Signal.MAX.strength()) {
      LOGGER.warn(DROPPED, VALUE, strength);
      return Signal.OFF;
    }
    return running ? new Signal(strength) : Signal.OFF;
  }
}
