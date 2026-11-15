package io.github.marcelofabianov.redweave.menu;

import java.util.Optional;

/**
 * What a button of the Signal Button Screen asks the server: a step of `Strength`, a step of the on
 * time, a connection of `Output`, or the position in the network choice (answers 414, 556 and 582).
 * The screen has no button to press it: the hand and the `Toggle` do it (answer 588).
 */
public record ButtonAction(Kind kind, int value) {
  /** The value of a connection on the edge; a channel is its color plus one. */
  public static final int EDGE = ModuleScreenValues.EDGE;

  private static final int WEAKER = 0;
  private static final int STRONGER = 1;
  private static final String INVALID = "Invalid value %d for %s";

  public enum Kind {
    STRENGTH,
    TIME,
    OUTPUT,
    CHOOSE
  }

  public ButtonAction {
    if (!fits(kind, value)) {
      throw new IllegalArgumentException(String.format(INVALID, value, kind));
    }
  }

  /** A step of `Strength`: `−` one weaker and `+` one stronger. */
  public static ButtonAction strength(boolean stronger) {
    return new ButtonAction(Kind.STRENGTH, stronger ? STRONGER : WEAKER);
  }

  /** A step of the on time: shorter or longer, by 0.1 s or, with Shift, 1 s (answer 398). */
  public static ButtonAction time(boolean longer, boolean shift) {
    return new ButtonAction(Kind.TIME, IntervalSteps.indexOf(longer, shift));
  }

  public static ButtonAction output(Optional<Integer> channel) {
    return new ButtonAction(Kind.OUTPUT, channel.map(index -> index + 1).orElse(EDGE));
  }

  public static ButtonAction choose(int position) {
    return new ButtonAction(Kind.CHOOSE, position);
  }

  /** The code the game sends with the click. */
  public int button() {
    return ModuleScreenCodes.button(kind.ordinal(), value);
  }

  /** The action of a code, or none for a code no button sends. */
  public static Optional<ButtonAction> of(int button) {
    int kind = ModuleScreenCodes.kindOf(button);
    int value = ModuleScreenCodes.valueOf(button);
    if (button < 0 || kind >= Kind.values().length || !fits(Kind.values()[kind], value)) {
      return Optional.empty();
    }
    return Optional.of(new ButtonAction(Kind.values()[kind], value));
  }

  /** The strength a step adds: one more or one less. */
  public int step() {
    return value == STRONGER ? 1 : -1;
  }

  /** The ticks a step of the on time adds, negative to shorten it. */
  public int ticks() {
    return IntervalSteps.ticks(value);
  }

  /** The channel of a connection, by its color, or none on the edge. */
  public Optional<Integer> channel() {
    return value == ModuleScreenValues.EDGE ? Optional.empty() : Optional.of(value - 1);
  }

  private static boolean fits(Kind kind, int value) {
    return switch (kind) {
      case STRENGTH -> value == WEAKER || value == STRONGER;
      case TIME -> value >= 0 && value < IntervalSteps.COUNT;
      case OUTPUT -> value >= ModuleScreenValues.EDGE && value < ModuleScreenValues.CONNECTIONS;
      case CHOOSE -> value >= 0;
    };
  }
}
