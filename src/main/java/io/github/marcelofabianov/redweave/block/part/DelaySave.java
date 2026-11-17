package io.github.marcelofabianov.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.marcelofabianov.redweave.config.NeoForgeOutputDelayLimits;
import io.github.marcelofabianov.redweave.domain.module.DelayLine;
import io.github.marcelofabianov.redweave.domain.module.DelayedOutput;
import io.github.marcelofabianov.redweave.domain.module.OutputDelay;
import io.github.marcelofabianov.redweave.domain.module.OutputDelayLimits;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.slf4j.Logger;

/**
 * The output delay of a module in its NBT, the same in every module with a delay (answers 844 and
 * 854): `output_delay_ticks`, the delay in ticks, and `delay_line`, the changes still waiting, each
 * `{signal, ticks}` with the ticks it still waits. A save without them has no delay and nothing
 * waiting, with no migration, and a change `{on, ticks}` saved by the #171 is read at the strength
 * of the module (rule 15); an invalid delay is fitted to whole seconds within the config, and an
 * invalid change is dropped, both with a warning (rule 14).
 */
final class DelaySave {
  static final String DELAY = "output_delay_ticks";
  static final String LINE = "delay_line";

  private static final String SIGNAL = "signal";
  private static final String ON = "on";
  private static final String TICKS = "ticks";
  private static final OutputDelayLimits LIMITS = new NeoForgeOutputDelayLimits();
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String FITTED = "Fitted an invalid output delay of a {}: {}";
  private static final String DROPPED = "Dropped an invalid delayed change of a {}: {}";

  private DelaySave() {}

  /** Writes the delay and what waits of a module with a delayed output. */
  static void write(CompoundTag tag, DelayedOutput delayed) {
    writeDelay(tag, delayed.delay());
    writeLine(tag, delayed.line());
  }

  /**
   * The delayed output of the module from its NBT, at its strength; a paste, which never carries
   * the line, keeps what waits now (answer 841).
   */
  static DelayedOutput read(CompoundTag tag, String module, Signal strength, DelayedOutput now) {
    OutputDelay delay = readDelay(tag, module);
    DelayLine line = tag.contains(LINE) ? readLine(tag, module, strength) : now.line();
    return DelayedOutput.restore(delay, line, strength);
  }

  static void writeDelay(CompoundTag tag, OutputDelay delay) {
    tag.putInt(DELAY, delay.ticks());
  }

  static void writeLine(CompoundTag tag, DelayLine line) {
    ListTag list = new ListTag();
    for (DelayLine.Pending change : line.pending()) {
      CompoundTag entry = new CompoundTag();
      entry.putInt(SIGNAL, change.signal().strength());
      entry.putInt(TICKS, change.ticks());
      list.add(entry);
    }
    tag.put(LINE, list);
  }

  /** The saved delay of the module, named in the warning, within the config; none if missing. */
  static OutputDelay readDelay(CompoundTag tag, String module) {
    int ticks = tag.getInt(DELAY);
    OutputDelay fitted = OutputDelay.within(ticks, LIMITS);
    if (ticks < 0 || ticks % OutputDelay.STEP_TICKS != 0) {
      LOGGER.warn(FITTED, module, ticks);
    }
    return fitted;
  }

  /** The saved changes of the module still waiting, in order; empty if missing. */
  static DelayLine readLine(CompoundTag tag, String module, Signal strength) {
    ListTag list = tag.getList(LINE, Tag.TAG_COMPOUND);
    List<DelayLine.Pending> changes = new ArrayList<>();
    for (int index = 0; index < list.size(); index++) {
      CompoundTag entry = list.getCompound(index);
      Optional<Signal> signal = signalOf(entry, strength);
      int ticks = entry.getInt(TICKS);
      if (signal.isEmpty() || ticks < 0) {
        LOGGER.warn(DROPPED, module, entry);
        continue;
      }
      changes.add(new DelayLine.Pending(signal.get(), ticks));
    }
    return DelayLine.restore(changes);
  }

  /** The signal of a change, or of an older `{on}` at the strength of the module (854). */
  private static Optional<Signal> signalOf(CompoundTag entry, Signal strength) {
    if (entry.contains(SIGNAL, Tag.TAG_INT)) {
      int value = entry.getInt(SIGNAL);
      boolean valid = value >= Signal.OFF.strength() && value <= Signal.MAX.strength();
      return valid ? Optional.of(new Signal(value)) : Optional.empty();
    }
    if (entry.contains(ON, Tag.TAG_BYTE)) {
      return Optional.of(entry.getBoolean(ON) ? strength : Signal.OFF);
    }
    return Optional.empty();
  }
}
