package io.github.marcelofabianov.redweave.domain.module;

/**
 * How long the Signal Button stays on once pressed, in whole ticks, 1 s when placed (answer 556).
 * It follows the rule of every interval of a module, clamped to the limits of the config instead of
 * refused (answer 490), with a floor of one step of the screen, 2 ticks, instead of the 4 of a
 * timer, which needs a step on and a step off (answer 592).
 */
public record ButtonTime(int ticks) {
  /** The shortest valid on time: one step of the screen, 0.1 s (answer 592). */
  public static final int SHORTEST_TICKS = Interval.STEP_TICKS;

  /** The on time of a button just placed, 1 s, as the stone button of the game (answer 556). */
  public static final int PLACED_TICKS = 20;

  private static final String INVALID = "An on time of %d+ ticks, got %d";

  public ButtonTime {
    if (ticks < SHORTEST_TICKS) {
      throw new IllegalArgumentException(String.format(INVALID, SHORTEST_TICKS, ticks));
    }
  }

  /** The on time of the ticks, clamped to the limits. */
  public static ButtonTime within(int ticks, ButtonLimits limits) {
    return new ButtonTime(Interval.clamped(ticks, limits, SHORTEST_TICKS));
  }

  /** Whether it is within the limits, as a saved on time after the config changed. */
  public boolean fits(ButtonLimits limits) {
    return Interval.fitting(ticks, limits, SHORTEST_TICKS);
  }

  /** The same on time within the limits, for a saved one after the config changed (425). */
  public ButtonTime fittedTo(ButtonLimits limits) {
    return within(ticks, limits);
  }
}
