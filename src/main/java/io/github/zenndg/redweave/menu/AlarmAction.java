package io.github.zenndg.redweave.menu;

import java.util.Optional;

/**
 * What a button of the Signal Alarm Screen asks the server: the next listeners, a step of the
 * rings, of the interval or of the duration, a connection of `Input`, or the position in the
 * network choice (answers 643, 645, 646 and 648).
 */
public record AlarmAction(Kind kind, int value) {
  /** The value of a connection on the edge; a channel is its color plus one. */
  public static final int EDGE = ModuleScreenValues.EDGE;

  private static final int LESS = 0;
  private static final int MORE = 1;
  private static final String INVALID = "Invalid value %d for %s";

  public enum Kind {
    LISTENERS,
    REPEATS,
    INTERVAL,
    DURATION,
    INPUT,
    CHOOSE
  }

  public AlarmAction {
    if (!fits(kind, value)) {
      throw new IllegalArgumentException(String.format(INVALID, value, kind));
    }
  }

  /** The next listeners: `Owner`, `Network` and `Range` in turn (answer 645). */
  public static AlarmAction listeners() {
    return new AlarmAction(Kind.LISTENERS, 0);
  }

  /** A step of the rings: `−` one fewer and `+` one more. */
  public static AlarmAction repeats(boolean more) {
    return new AlarmAction(Kind.REPEATS, more ? MORE : LESS);
  }

  /** A step of the interval: shorter or longer, by 0.1 s or, with Shift, 1 s (answer 398). */
  public static AlarmAction interval(boolean longer, boolean shift) {
    return new AlarmAction(Kind.INTERVAL, IntervalSteps.indexOf(longer, shift));
  }

  /** A step of the duration: shorter or longer, by 0.1 s or, with Shift, 1 s (answer 398). */
  public static AlarmAction duration(boolean longer, boolean shift) {
    return new AlarmAction(Kind.DURATION, IntervalSteps.indexOf(longer, shift));
  }

  /** A connection of `Input`, on the edge or on the channel of a color. */
  public static AlarmAction connect(Optional<Integer> channel) {
    return new AlarmAction(Kind.INPUT, channel.map(index -> index + 1).orElse(EDGE));
  }

  public static AlarmAction choose(int position) {
    return new AlarmAction(Kind.CHOOSE, position);
  }

  /** The code the game sends with the click. */
  public int button() {
    return ModuleScreenCodes.button(kind.ordinal(), value);
  }

  /** The action of a code, or none for a code no button sends. */
  public static Optional<AlarmAction> of(int button) {
    int kind = ModuleScreenCodes.kindOf(button);
    int value = ModuleScreenCodes.valueOf(button);
    if (button < 0 || kind >= Kind.values().length || !fits(Kind.values()[kind], value)) {
      return Optional.empty();
    }
    return Optional.of(new AlarmAction(Kind.values()[kind], value));
  }

  /** The rings a step adds: one more or one fewer. */
  public int step() {
    return value == MORE ? 1 : -1;
  }

  /** The ticks a step of the interval or the duration adds, negative to shorten it. */
  public int ticks() {
    return IntervalSteps.ticks(value);
  }

  /** The channel of the input, by its color, or none on the edge. */
  public Optional<Integer> channel() {
    return value == ModuleScreenValues.EDGE ? Optional.empty() : Optional.of(value - 1);
  }

  private static boolean fits(Kind kind, int value) {
    return switch (kind) {
      case LISTENERS -> value == 0;
      case REPEATS -> value == LESS || value == MORE;
      case INTERVAL, DURATION -> value >= 0 && value < IntervalSteps.COUNT;
      case INPUT -> value >= ModuleScreenValues.EDGE && value < ModuleScreenValues.CONNECTIONS;
      case CHOOSE -> value >= 0;
    };
  }
}
