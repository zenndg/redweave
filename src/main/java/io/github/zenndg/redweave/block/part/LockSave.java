package io.github.zenndg.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.zenndg.redweave.domain.module.LockPulse;
import io.github.zenndg.redweave.domain.module.SignalLock;
import io.github.zenndg.redweave.domain.signal.Signal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.slf4j.Logger;

/**
 * The NBT of a Signal Lock besides its module, part of the save (rule 15): `key`, `strength`,
 * `pulse`, the ticks of the pulse of its `Toggle`, `remaining`, the ticks left of a pulse under
 * way, and its two connections, `input` and `output`. An invalid value goes back to a safe one with
 * a warning (rule 14): the key and the strength to 15, the pulse to 1 s, and the ticks left to 0.
 */
final class LockSave {
  static final String INPUT = InverterSave.INPUT;
  static final String OUTPUT = InverterSave.OUTPUT;

  static final String KEY = "key";
  private static final String PULSE = "pulse";
  private static final String REMAINING = "remaining";
  private static final String MODULE = "Signal Lock";
  private static final int WEAKEST = 1;
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String DROPPED = "Dropped an invalid {} from a Signal Lock: {}";

  private LockSave() {}

  static void write(CompoundTag tag, SignalLock lock) {
    tag.putInt(KEY, lock.key().strength());
    StrengthSave.write(tag, lock.strength());
    tag.putInt(PULSE, lock.pulse().ticks());
    tag.putInt(REMAINING, lock.remaining());
  }

  static SignalLock read(CompoundTag tag) {
    return new SignalLock(key(tag), StrengthSave.read(tag, MODULE), pulse(tag), remaining(tag));
  }

  private static Signal key(CompoundTag tag) {
    if (!tag.contains(KEY, Tag.TAG_INT)) {
      return Signal.MAX;
    }
    int key = tag.getInt(KEY);
    if (key < WEAKEST || key > Signal.MAX.strength()) {
      LOGGER.warn(DROPPED, KEY, key);
      return Signal.MAX;
    }
    return new Signal(key);
  }

  private static LockPulse pulse(CompoundTag tag) {
    int ticks = tag.getInt(PULSE);
    if (ticks < LockPulse.SHORTEST_TICKS) {
      LOGGER.warn(DROPPED, PULSE, ticks);
      return new LockPulse(LockPulse.PLACED_TICKS);
    }
    return new LockPulse(ticks);
  }

  private static int remaining(CompoundTag tag) {
    int ticks = tag.getInt(REMAINING);
    if (ticks < 0) {
      LOGGER.warn(DROPPED, REMAINING, ticks);
      return 0;
    }
    return ticks;
  }
}
