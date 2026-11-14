package io.github.marcelofabianov.redweave.domain.module;

/**
 * The cycle of a Signal Timer: every interval, the output is on for the pulse. The pulse is at
 * least 2 ticks, the step of the screen, and leaves at least 2 ticks off (answers 379, 382 and
 * 384). Changes are clamped instead of refused, as the buttons of the screen step past the limits.
 */
public record TimerCycle(int intervalTicks, int pulseTicks) {
  /** The shortest pulse, and the shortest time off: one step of the screen, 0.1 s. */
  public static final int STEP_TICKS = 2;

  private static final int SHORTEST_INTERVAL = STEP_TICKS + STEP_TICKS;
  private static final int INITIAL_INTERVAL = 20;
  private static final int INITIAL_PULSE = 10;
  private static final String INVALID = "A pulse of 2+ ticks with 2 off, got %d/%d";

  public TimerCycle {
    if (pulseTicks < STEP_TICKS || pulseTicks > intervalTicks - STEP_TICKS) {
      throw new IllegalArgumentException(String.format(INVALID, intervalTicks, pulseTicks));
    }
  }

  /** A new timer: one second with a half-second pulse, within the limits (answer 390). */
  public static TimerCycle initial(TimerLimits limits) {
    return new TimerCycle(INITIAL_INTERVAL, INITIAL_PULSE).fittedTo(limits);
  }

  /** Another interval, clamped to the limits, with the pulse shortened to fit (answer 409). */
  public TimerCycle withInterval(int ticks, TimerLimits limits) {
    int interval = Math.clamp(ticks, shortestOf(limits), longestOf(limits));
    return new TimerCycle(interval, Math.min(pulseTicks, interval - STEP_TICKS));
  }

  /** Another pulse, clamped between 2 ticks and the interval less 2 (answer 384). */
  public TimerCycle withPulse(int ticks) {
    return new TimerCycle(intervalTicks, Math.clamp(ticks, STEP_TICKS, intervalTicks - STEP_TICKS));
  }

  /** Whether the interval is within the limits, as a saved timer after the config changed. */
  public boolean fits(TimerLimits limits) {
    return intervalTicks >= shortestOf(limits) && intervalTicks <= longestOf(limits);
  }

  /** The same cycle within the limits, for a saved timer after the config changed (answer 425). */
  public TimerCycle fittedTo(TimerLimits limits) {
    return withInterval(intervalTicks, limits);
  }

  /** The limits never go below the shortest valid cycle, whatever the config says. */
  private static int shortestOf(TimerLimits limits) {
    return Math.max(limits.minIntervalTicks(), SHORTEST_INTERVAL);
  }

  private static int longestOf(TimerLimits limits) {
    return Math.max(limits.maxIntervalTicks(), shortestOf(limits));
  }
}
