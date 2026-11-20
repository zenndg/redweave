package io.github.zenndg.redweave.menu;

import java.util.Optional;

/**
 * What a button of the Signal Inverter Screen asks the server: a connection of `Input` or `Output`,
 * as the edge or a channel, the position in the network choice, or a step of `Strength` or `Delay`
 * (answers 400, 451, 830 and 834).
 */
public record InverterAction(Kind kind, int value) {
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

  public InverterAction {
    if (!fits(kind, value)) {
      throw new IllegalArgumentException(String.format(INVALID, value, kind));
    }
  }

  public static InverterAction choose(int position) {
    return new InverterAction(Kind.CHOOSE, position);
  }

  /** A step of `Strength`, one stronger or one weaker (answer 834). */
  public static InverterAction strength(boolean up) {
    return new InverterAction(Kind.STRENGTH, up ? UP : DOWN);
  }

  /** A step of `Delay`, one second longer or shorter (answer 830). */
  public static InverterAction delay(boolean up) {
    return new InverterAction(Kind.DELAY, up ? UP : DOWN);
  }

  /** What a step adds: one more or one less. */
  public int step() {
    return value == UP ? 1 : -1;
  }

  /** The code the game sends with the click. */
  public int button() {
    return ModuleScreenCodes.button(kind.ordinal(), value);
  }

  /** The action of a code, or none for a code no button sends. */
  public static Optional<InverterAction> of(int button) {
    int kind = ModuleScreenCodes.kindOf(button);
    int value = ModuleScreenCodes.valueOf(button);
    if (button < 0 || kind >= Kind.values().length || !fits(Kind.values()[kind], value)) {
      return Optional.empty();
    }
    return Optional.of(new InverterAction(Kind.values()[kind], value));
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
      case STRENGTH, DELAY -> value == DOWN || value == UP;
    };
  }
}
