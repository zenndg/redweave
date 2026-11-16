package io.github.marcelofabianov.redweave.menu;

import java.util.Optional;

/**
 * What a button of the Signal Lock Screen asks the server: a step of the key, of `Strength` or of
 * the pulse, a connection of `Input` or `Output`, or the position in the network choice (answers
 * 635, 636 and 640).
 */
public record LockAction(Kind kind, int value) {
  /** The value of a connection on the edge; a channel is its color plus one. */
  public static final int EDGE = ModuleScreenValues.EDGE;

  private static final int LESS = 0;
  private static final int MORE = 1;
  private static final String INVALID = "Invalid value %d for %s";

  public enum Kind {
    KEY,
    STRENGTH,
    PULSE,
    INPUT,
    OUTPUT,
    CHOOSE
  }

  public LockAction {
    if (!fits(kind, value)) {
      throw new IllegalArgumentException(String.format(INVALID, value, kind));
    }
  }

  /** A step of the key: `−` one weaker and `+` one stronger. */
  public static LockAction key(boolean stronger) {
    return new LockAction(Kind.KEY, stronger ? MORE : LESS);
  }

  /** A step of `Strength`: `−` one weaker and `+` one stronger. */
  public static LockAction strength(boolean stronger) {
    return new LockAction(Kind.STRENGTH, stronger ? MORE : LESS);
  }

  /** A step of the pulse: shorter or longer, by 0.1 s or, with Shift, 1 s (answer 398). */
  public static LockAction pulse(boolean longer, boolean shift) {
    return new LockAction(Kind.PULSE, IntervalSteps.indexOf(longer, shift));
  }

  /** A connection of `Input` or `Output`, on the edge or on the channel of a color. */
  public static LockAction connect(Kind kind, Optional<Integer> channel) {
    return new LockAction(kind, channel.map(index -> index + 1).orElse(EDGE));
  }

  public static LockAction choose(int position) {
    return new LockAction(Kind.CHOOSE, position);
  }

  /** The code the game sends with the click. */
  public int button() {
    return ModuleScreenCodes.button(kind.ordinal(), value);
  }

  /** The action of a code, or none for a code no button sends. */
  public static Optional<LockAction> of(int button) {
    int kind = ModuleScreenCodes.kindOf(button);
    int value = ModuleScreenCodes.valueOf(button);
    if (button < 0 || kind >= Kind.values().length || !fits(Kind.values()[kind], value)) {
      return Optional.empty();
    }
    return Optional.of(new LockAction(Kind.values()[kind], value));
  }

  /** The strength a step of the key or of `Strength` adds: one more or one less. */
  public int step() {
    return value == MORE ? 1 : -1;
  }

  /** The ticks a step of the pulse adds, negative to shorten it. */
  public int ticks() {
    return IntervalSteps.ticks(value);
  }

  /** The channel of a connection, by its color, or none on the edge. */
  public Optional<Integer> channel() {
    return value == ModuleScreenValues.EDGE ? Optional.empty() : Optional.of(value - 1);
  }

  private static boolean fits(Kind kind, int value) {
    return switch (kind) {
      case KEY, STRENGTH -> value == LESS || value == MORE;
      case PULSE -> value >= 0 && value < IntervalSteps.COUNT;
      case INPUT, OUTPUT ->
          value >= ModuleScreenValues.EDGE && value < ModuleScreenValues.CONNECTIONS;
      case CHOOSE -> value >= 0;
    };
  }
}
