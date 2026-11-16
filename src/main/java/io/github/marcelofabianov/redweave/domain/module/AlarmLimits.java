package io.github.marcelofabianov.redweave.domain.module;

/**
 * The numbers of the Signal Alarm that the pack sets in the config: the shortest and the longest
 * interval between its rings, in ticks, the most rings of one alarm, and the longest it rings
 * (answers 646 and 648). The domain defines it, and the config implements it.
 */
public interface AlarmLimits extends IntervalLimits {
  int maxRepeats();

  int maxDurationTicks();
}
