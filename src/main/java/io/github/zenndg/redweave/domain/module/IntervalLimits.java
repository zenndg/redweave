package io.github.zenndg.redweave.domain.module;

/**
 * The shortest and the longest interval a pack sets in the config for a module that ticks on its
 * own, in ticks (answers 384 and 477). The domain defines it, and the config implements it.
 */
public interface IntervalLimits {
  int minIntervalTicks();

  int maxIntervalTicks();
}
