package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

/**
 * The keys of the output delay of a module, written here again, so a renamed key fails the tests
 * that keep a save whole (answers 844 and 854, rule 15): a delay of two seconds and a change still
 * waiting.
 */
final class DelayKeys {
  static final String DELAY = "output_delay_ticks";
  static final String LINE = "delay_line";
  static final String SIGNAL = "signal";
  static final String TICKS = "ticks";
  static final int TWO_SECONDS = 40;
  static final int WAITING = 12;

  private DelayKeys() {}

  /** Adds a delay and a change that turns off, still waiting, to a full save. */
  static CompoundTag addTo(CompoundTag tag) {
    tag.putInt(DELAY, TWO_SECONDS);
    CompoundTag change = new CompoundTag();
    change.putInt(SIGNAL, Signal.OFF.strength());
    change.putInt(TICKS, WAITING);
    ListTag line = new ListTag();
    line.add(change);
    tag.put(LINE, line);
    return tag;
  }
}
