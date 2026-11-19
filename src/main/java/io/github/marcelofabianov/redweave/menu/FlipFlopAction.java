package io.github.marcelofabianov.redweave.menu;

import java.util.Optional;

/**
 * What a button of the Signal Flip-Flop Screen asks the server: a connection of `Input` or
 * `Output`, as the edge or a channel, the position in the network choice, or a step of `Strength`
 * or `Delay` (answers 830, 880 and 884).
 */
public record FlipFlopAction(Kind kind, int value) {
  /** The value of a connection on the edge; a channel is its color plus one. */
  public static final int EDGE = ModuleScreenValues.EDGE;

  private static final int DOWN = 0;
  private static final int UP = 1;
  private static final String INVALID = "Invalid value %d for %s";

  public enum Kind {
    INPUT,
    OUTPUT,
    CHOOSE,
    STRENGTH,
    DELAY
  }

  public FlipFlopAction {
    if (!fits(kind, value)) {
      throw new IllegalArgumentException(String.format(INVALID, value, kind));
    }
  }

  public static FlipFlopAction choose(int position) {
    return new FlipFlopAction(Kind.CHOOSE, position);
  }

  /** A step of `Strength`, one stronger or one weaker (answer 880). */
  public static FlipFlopAction strength(boolean up) {
    return new FlipFlopAction(Kind.STRENGTH, up ? UP : DOWN);
  }

  /** A step of `Delay`, one second shorter or longer (answer 830). */
  public static FlipFlopAction delay(boolean longer) {
    return new FlipFlopAction(Kind.DELAY, DelaySteps.of(longer));
  }

  /** What a step of `Strength` adds: one more or one less. */
  public int step() {
    return value == UP ? 1 : -1;
  }

  /** The code the game sends with the click. */
  public int button() {
    return ModuleScreenCodes.button(kind.ordinal(), value);
  }

  /** The action of a code, or none for a code no button sends. */
  public static Optional<FlipFlopAction> of(int button) {
    int kind = ModuleScreenCodes.kindOf(button);
    int value = ModuleScreenCodes.valueOf(button);
    if (button < 0 || kind >= Kind.values().length || !fits(Kind.values()[kind], value)) {
      return Optional.empty();
    }
    return Optional.of(new FlipFlopAction(Kind.values()[kind], value));
  }

  /** The channel of a connection, by its color, or none on the edge. */
  public Optional<Integer> channel() {
    return value == ModuleScreenValues.EDGE ? Optional.empty() : Optional.of(value - 1);
  }

  private static boolean fits(Kind kind, int value) {
    return switch (kind) {
      case INPUT, OUTPUT ->
          value >= ModuleScreenValues.EDGE && value < ModuleScreenValues.CONNECTIONS;
      case CHOOSE -> value >= 0;
      case STRENGTH -> value == DOWN || value == UP;
      case DELAY -> DelaySteps.fits(value);
    };
  }
}
