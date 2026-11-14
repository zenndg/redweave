package io.github.marcelofabianov.redweave.domain.module;

/** Fixed timer limits for the tests, with the starting values of _docs/limites.md (answer 384). */
record TestTimerLimits(int minIntervalTicks, int maxIntervalTicks) implements TimerLimits {
  private static final int MIN_INTERVAL = 4;
  private static final int MAX_INTERVAL = 6000;

  static final TestTimerLimits STARTING = new TestTimerLimits(MIN_INTERVAL, MAX_INTERVAL);
}
