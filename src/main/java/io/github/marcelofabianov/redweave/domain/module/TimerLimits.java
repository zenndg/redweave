package io.github.marcelofabianov.redweave.domain.module;

/**
 * The numbers of the Signal Timer that the pack sets in the config: the shortest and the longest
 * interval, in ticks (answer 384). The domain defines it, and the config implements it.
 */
public interface TimerLimits {
  int minIntervalTicks();

  int maxIntervalTicks();
}
