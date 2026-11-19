package io.github.zenndg.redweave.domain.module;

/**
 * The interval of a module that ticks on its own, as the Signal Timer and the Signal Randomizer, in
 * whole ticks (answers 382 and 490). Changes are clamped to the limits of the config instead of
 * refused, as the buttons of the screen step past them, and the limits never go below the shortest
 * valid interval, whatever the config says (answers 384 and 398).
 */
public record Interval(int ticks) {
  /** One step of the screen, 0.1 s (answer 382). */
  public static final int STEP_TICKS = 2;

  /** The shortest valid interval: a step on and a step off for the pulse of a timer. */
  public static final int SHORTEST_TICKS = STEP_TICKS + STEP_TICKS;

  private static final String INVALID = "An interval of %d+ ticks, got %d";

  public Interval {
    if (ticks < SHORTEST_TICKS) {
      throw new IllegalArgumentException(String.format(INVALID, SHORTEST_TICKS, ticks));
    }
  }

  /** The interval of the ticks, clamped to the limits. */
  public static Interval within(int ticks, IntervalLimits limits) {
    return new Interval(clamped(ticks, limits, SHORTEST_TICKS));
  }

  /** Whether it is within the limits, as a saved interval after the config changed. */
  public boolean fits(IntervalLimits limits) {
    return fitting(ticks, limits, SHORTEST_TICKS);
  }

  /** The same interval within the limits, for a saved one after the config changed (425). */
  public Interval fittedTo(IntervalLimits limits) {
    return within(ticks, limits);
  }

  /**
   * Ticks clamped to the limits, never below the floor, whatever the config says: the one rule of
   * every interval of a module, as the on time of the Signal Button too (answers 490 and 592).
   */
  static int clamped(int ticks, IntervalLimits limits, int floor) {
    return Math.clamp(ticks, shortestOf(limits, floor), longestOf(limits, floor));
  }

  /** Whether the ticks are within the limits, never below the floor. */
  static boolean fitting(int ticks, IntervalLimits limits, int floor) {
    return ticks >= shortestOf(limits, floor) && ticks <= longestOf(limits, floor);
  }

  private static int shortestOf(IntervalLimits limits, int floor) {
    return Math.max(limits.minIntervalTicks(), floor);
  }

  private static int longestOf(IntervalLimits limits, int floor) {
    return Math.max(limits.maxIntervalTicks(), shortestOf(limits, floor));
  }
}
