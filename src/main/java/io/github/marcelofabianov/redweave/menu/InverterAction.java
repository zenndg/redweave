package io.github.marcelofabianov.redweave.menu;

import java.util.Optional;

/**
 * What a button of the Signal Inverter Screen asks the server: a connection of `Input` or `Output`,
 * as the edge or a channel, or the position in the network choice (answers 400 and 451).
 */
public record InverterAction(Kind kind, int value) {
  /** The value of a connection on the edge; a channel is its color plus one. */
  public static final int EDGE = ModuleScreenValues.EDGE;

  private static final String INVALID = "Invalid value %d for %s";

  public enum Kind {
    INPUT,
    OUTPUT,
    CHOOSE
  }

  public InverterAction {
    if (!fits(kind, value)) {
      throw new IllegalArgumentException(String.format(INVALID, value, kind));
    }
  }

  public static InverterAction choose(int position) {
    return new InverterAction(Kind.CHOOSE, position);
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
    };
  }
}
