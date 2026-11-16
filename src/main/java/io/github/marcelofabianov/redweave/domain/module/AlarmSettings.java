package io.github.marcelofabianov.redweave.domain.module;

import java.util.Objects;

/**
 * What the owner of a Signal Alarm sets on its screen: who hears it, how many times it rings, the
 * ticks between rings, and the ticks it lasts (answers 645, 646 and 648). Each one is clamped to
 * the limits of the config instead of refused (answer 490).
 */
public record AlarmSettings(
    AlarmListeners listeners, int repeats, int intervalTicks, int durationTicks) {
  /** The rings of an alarm just placed. */
  public static final int PLACED_REPEATS = 3;

  /** The ticks between rings of an alarm just placed, 1 s. */
  public static final int PLACED_INTERVAL_TICKS = 20;

  /** The ticks an alarm just placed lasts, 5 s (answer 648). */
  public static final int PLACED_DURATION_TICKS = 100;

  /** The shortest valid interval or duration: one step of the screen, 0.1 s. */
  public static final int SHORTEST_TICKS = Interval.STEP_TICKS;

  private static final String MISSING = "An alarm needs its listeners";
  private static final String INVALID = "An alarm needs 1+ rings and %d+ ticks, got %d, %d, %d";

  public AlarmSettings {
    Objects.requireNonNull(listeners, MISSING);
    if (repeats < 1 || intervalTicks < SHORTEST_TICKS || durationTicks < SHORTEST_TICKS) {
      throw new IllegalArgumentException(
          String.format(INVALID, SHORTEST_TICKS, repeats, intervalTicks, durationTicks));
    }
  }

  /** A new alarm: its owner hears it, 3 rings, 1 s apart, for 5 s, within the limits. */
  public static AlarmSettings placed(AlarmLimits limits) {
    return new AlarmSettings(AlarmListeners.OWNER, 1, SHORTEST_TICKS, SHORTEST_TICKS)
        .withRepeats(PLACED_REPEATS, limits)
        .withInterval(PLACED_INTERVAL_TICKS, limits)
        .withDuration(PLACED_DURATION_TICKS, limits);
  }

  public AlarmSettings withListeners(AlarmListeners next) {
    return new AlarmSettings(next, repeats, intervalTicks, durationTicks);
  }

  /** Other rings, from 1 to the most the config allows, with the duration following (737). */
  public AlarmSettings withRepeats(int next, AlarmLimits limits) {
    int most = Math.max(limits.maxRepeats(), 1);
    return new AlarmSettings(listeners, Math.clamp(next, 1, most), intervalTicks, durationTicks)
        .withDuration(durationTicks, limits);
  }

  /** Another interval between rings, within the limits of the config (answer 646). */
  public AlarmSettings withInterval(int ticks, AlarmLimits limits) {
    int clamped = Interval.clamped(ticks, limits, SHORTEST_TICKS);
    return new AlarmSettings(listeners, repeats, clamped, durationTicks)
        .withDuration(durationTicks, limits);
  }

  /**
   * Another duration, never shorter than its rings, so every ring the owner chose sounds (answer
   * 737), and up to the longest the config allows, or the rings if they take longer (648).
   */
  public AlarmSettings withDuration(int ticks, AlarmLimits limits) {
    int shortest = Math.max(ringsTicks(), SHORTEST_TICKS);
    int longest = Math.max(limits.maxDurationTicks(), shortest);
    return new AlarmSettings(
        listeners, repeats, intervalTicks, Math.clamp(ticks, shortest, longest));
  }

  /** The ticks its rings take: one ring at the start and one at each interval after it. */
  public int ringsTicks() {
    return repeats * intervalTicks;
  }

  /** Whether every number fits the limits of the config of now (answer 425). */
  public boolean fits(AlarmLimits limits) {
    return equals(fittedTo(limits));
  }

  /** The same settings within the limits of now (answer 425). */
  public AlarmSettings fittedTo(AlarmLimits limits) {
    return withRepeats(repeats, limits)
        .withInterval(intervalTicks, limits)
        .withDuration(durationTicks, limits);
  }
}
