package io.github.marcelofabianov.redweave.domain.module;

/** Fixed alarm limits for the tests, with the starting values of _docs/limites.md (646, 648). */
public record TestAlarmLimits(
    int minIntervalTicks, int maxIntervalTicks, int maxRepeats, int maxDurationTicks)
    implements AlarmLimits {
  private static final int MIN_INTERVAL = 20;
  private static final int MAX_INTERVAL = 200;
  private static final int MAX_REPEATS = 5;
  private static final int MAX_DURATION = 600;

  public static final TestAlarmLimits STARTING =
      new TestAlarmLimits(MIN_INTERVAL, MAX_INTERVAL, MAX_REPEATS, MAX_DURATION);
}
