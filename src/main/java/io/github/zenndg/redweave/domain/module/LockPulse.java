package io.github.zenndg.redweave.domain.module;

/**
 * How long the `Toggle` of the Signal Lock keeps it open, in whole ticks, 1 s when placed, chosen
 * by its owner on the screen (answer 640). It follows the rule of every interval of a module,
 * clamped to the limits of the config instead of refused (answer 490), with a floor of one step of
 * the screen, 2 ticks.
 */
public record LockPulse(int ticks) {
  /** The shortest valid pulse: one step of the screen, 0.1 s (answer 640). */
  public static final int SHORTEST_TICKS = Interval.STEP_TICKS;

  /** The pulse of a lock just placed, 1 s (answer 640). */
  public static final int PLACED_TICKS = 20;

  private static final String INVALID = "A pulse of %d+ ticks, got %d";

  public LockPulse {
    if (ticks < SHORTEST_TICKS) {
      throw new IllegalArgumentException(String.format(INVALID, SHORTEST_TICKS, ticks));
    }
  }

  /** The pulse of the ticks, clamped to the limits. */
  public static LockPulse within(int ticks, LockLimits limits) {
    return new LockPulse(Interval.clamped(ticks, limits, SHORTEST_TICKS));
  }

  /** Whether it is within the limits, as a saved pulse after the config changed. */
  public boolean fits(LockLimits limits) {
    return Interval.fitting(ticks, limits, SHORTEST_TICKS);
  }
}
