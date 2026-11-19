package io.github.zenndg.redweave.domain.signal;

/**
 * How long the output of a module waits before it follows a change, in whole seconds kept as ticks
 * (answers 830 and 837). Steps and saved delays are clamped to the limits of the config instead of
 * refused, as the buttons of the screen step past them; a longest of 0 turns the delay off.
 */
public record OutputDelay(int ticks) {
  /** One step of the screen, a whole second (answer 830). */
  public static final int STEP_TICKS = 20;

  /** No delay, the default of every module (answer 844). */
  public static final OutputDelay NONE = new OutputDelay(0);

  private static final String INVALID = "A delay of whole seconds from 0 ticks, got %d";

  public OutputDelay {
    if (ticks < 0 || ticks % STEP_TICKS != 0) {
      throw new IllegalArgumentException(String.format(INVALID, ticks));
    }
  }

  /** The delay of the ticks, down to a whole second and within the limits. */
  public static OutputDelay within(int ticks, OutputDelayLimits limits) {
    int clamped = Math.clamp(ticks, 0, longestOf(limits));
    return new OutputDelay(clamped - clamped % STEP_TICKS);
  }

  /** The delay a `−` or `+` of the screen steps to, one second at a time. */
  public OutputDelay stepped(int seconds, OutputDelayLimits limits) {
    return within(ticks + seconds * STEP_TICKS, limits);
  }

  /** Whether it is within the limits, as a saved delay after the config changed. */
  public boolean fits(OutputDelayLimits limits) {
    return ticks <= longestOf(limits);
  }

  /** The same delay within the limits, for a saved one after the config changed. */
  public OutputDelay fittedTo(OutputDelayLimits limits) {
    return within(ticks, limits);
  }

  public int seconds() {
    return ticks / STEP_TICKS;
  }

  private static int longestOf(OutputDelayLimits limits) {
    int longest = Math.max(limits.maxOutputDelayTicks(), 0);
    return longest - longest % STEP_TICKS;
  }
}
