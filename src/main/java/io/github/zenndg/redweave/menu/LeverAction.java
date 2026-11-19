package io.github.zenndg.redweave.menu;

import java.util.Optional;

/**
 * What a button of the Signal Lever Screen asks the server: a step of `Strength`, a connection of
 * `Output`, or the position in the network choice (answers 414 and 582). The screen has no button
 * to turn the lever on: the hand and the `Toggle` do it (answer 582).
 */
public record LeverAction(Kind kind, int value) {
  /** The value of a connection on the edge; a channel is its color plus one. */
  public static final int EDGE = ModuleScreenValues.EDGE;

  private static final int WEAKER = 0;
  private static final int STRONGER = 1;
  private static final String INVALID = "Invalid value %d for %s";

  public enum Kind {
    STRENGTH,
    OUTPUT,
    CHOOSE,
    DELAY
  }

  public LeverAction {
    if (!fits(kind, value)) {
      throw new IllegalArgumentException(String.format(INVALID, value, kind));
    }
  }

  /** A step of `Strength`: `−` one weaker and `+` one stronger (answer 582). */
  public static LeverAction step(boolean stronger) {
    return new LeverAction(Kind.STRENGTH, stronger ? STRONGER : WEAKER);
  }

  public static LeverAction output(Optional<Integer> channel) {
    return new LeverAction(Kind.OUTPUT, channel.map(index -> index + 1).orElse(EDGE));
  }

  public static LeverAction choose(int position) {
    return new LeverAction(Kind.CHOOSE, position);
  }

  /** A step of `Delay`, one second shorter or longer (answer 830). */
  public static LeverAction delay(boolean longer) {
    return new LeverAction(Kind.DELAY, DelaySteps.of(longer));
  }

  /** The code the game sends with the click. */
  public int button() {
    return ModuleScreenCodes.button(kind.ordinal(), value);
  }

  /** The action of a code, or none for a code no button sends. */
  public static Optional<LeverAction> of(int button) {
    int kind = ModuleScreenCodes.kindOf(button);
    int value = ModuleScreenCodes.valueOf(button);
    if (button < 0 || kind >= Kind.values().length || !fits(Kind.values()[kind], value)) {
      return Optional.empty();
    }
    return Optional.of(new LeverAction(Kind.values()[kind], value));
  }

  /** The strength a step adds: one more or one less. */
  public int step() {
    return value == STRONGER ? 1 : -1;
  }

  /** The channel of a connection, by its color, or none on the edge. */
  public Optional<Integer> channel() {
    return value == ModuleScreenValues.EDGE ? Optional.empty() : Optional.of(value - 1);
  }

  private static boolean fits(Kind kind, int value) {
    return switch (kind) {
      case STRENGTH -> value == WEAKER || value == STRONGER;
      case OUTPUT -> value >= ModuleScreenValues.EDGE && value < ModuleScreenValues.CONNECTIONS;
      case CHOOSE -> value >= 0;
      case DELAY -> DelaySteps.fits(value);
    };
  }
}
