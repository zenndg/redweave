package io.github.zenndg.redweave.menu;

import java.util.Optional;

/**
 * What a button of the Signal Timer Screen asks the server. The value is the step of `Interval` or
 * `Pulse`, the connection, or the position in the network choice (answers 398, 400 and 404).
 */
public record TimerAction(Kind kind, int value) {
  /** The value of a connection on the edge; a channel is its color plus one. */
  public static final int EDGE = ModuleScreenValues.EDGE;

  private static final String INVALID = "Invalid value %d for %s";

  public enum Kind {
    TOGGLE,
    INTERVAL,
    PULSE,
    OUTPUT,
    PAUSE,
    CHOOSE
  }

  public TimerAction {
    if (!fits(kind, value)) {
      throw new IllegalArgumentException(String.format(INVALID, value, kind));
    }
  }

  public static TimerAction toggle() {
    return new TimerAction(Kind.TOGGLE, 0);
  }

  /** A step of `Interval` or `Pulse`: shorter or longer, by 0.1 s or, with Shift, 1 s. */
  public static TimerAction step(Kind kind, boolean longer, boolean shift) {
    return new TimerAction(kind, IntervalSteps.indexOf(longer, shift));
  }

  public static TimerAction choose(int position) {
    return new TimerAction(Kind.CHOOSE, position);
  }

  /** The ticks a step of `Interval` or `Pulse` adds, negative to shorten it. */
  public int ticks() {
    return IntervalSteps.ticks(value);
  }

  /** The channel of a connection, by its color, or none on the edge. */
  public Optional<Integer> channel() {
    return value == EDGE ? Optional.empty() : Optional.of(value - 1);
  }

  static boolean fits(Kind kind, int value) {
    return switch (kind) {
      case TOGGLE -> value == 0;
      case INTERVAL, PULSE -> value >= 0 && value < IntervalSteps.COUNT;
      case OUTPUT, PAUSE -> value >= EDGE && value < ModuleScreenValues.CONNECTIONS;
      case CHOOSE -> value >= 0;
    };
  }
}
