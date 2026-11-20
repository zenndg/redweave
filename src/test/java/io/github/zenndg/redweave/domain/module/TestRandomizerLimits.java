package io.github.zenndg.redweave.domain.module;

/** Fixed randomizer limits for the tests, with the starting values of _docs/limites.md (477). */
record TestRandomizerLimits(int minIntervalTicks, int maxIntervalTicks)
    implements RandomizerLimits {
  private static final int MIN_INTERVAL = 4;
  private static final int MAX_INTERVAL = 6000;

  static final TestRandomizerLimits STARTING = new TestRandomizerLimits(MIN_INTERVAL, MAX_INTERVAL);
}
