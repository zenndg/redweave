package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.domain.module.KeypadCode;
import java.util.Optional;

/**
 * What a button of the Signal Keypad Screen of the owner asks the server: a connection of `Input`
 * (only a channel) or `Output`, the position in the network choice, a step of `Strength` or of the
 * open time, a new code, or a card to revoke (answers 661, 663, 669, 904 and 905).
 */
public record KeypadAction(Kind kind, int value) {
  /** The value of a connection on the edge; a channel is its color plus one. */
  public static final int EDGE = ModuleScreenValues.EDGE;

  private static final int LESS = 0;
  private static final int MORE = 1;
  private static final String INVALID = "Invalid value %d for %s";

  public enum Kind {
    INPUT,
    OUTPUT,
    CHOOSE,
    STRENGTH,
    OPEN,
    CODE,
    REVOKE
  }

  public KeypadAction {
    if (!fits(kind, value)) {
      throw new IllegalArgumentException(String.format(INVALID, value, kind));
    }
  }

  public static KeypadAction choose(int position) {
    return new KeypadAction(Kind.CHOOSE, position);
  }

  /** A step of `Strength`: `−` one weaker and `+` one stronger (answer 663). */
  public static KeypadAction strength(boolean stronger) {
    return new KeypadAction(Kind.STRENGTH, stronger ? MORE : LESS);
  }

  /** A step of the open time: one second shorter or longer (answer 663). */
  public static KeypadAction open(boolean longer) {
    return new KeypadAction(Kind.OPEN, longer ? MORE : LESS);
  }

  /** A new code, typed twice on the screen (answer 905). */
  public static KeypadAction code(KeypadCode code) {
    return new KeypadAction(Kind.CODE, KeypadCodes.encode(code));
  }

  /** The card at a position of the list, to revoke (answer 669). */
  public static KeypadAction revoke(int position) {
    return new KeypadAction(Kind.REVOKE, position);
  }

  /** What a step adds: one more or one less. */
  public int step() {
    return value == MORE ? 1 : -1;
  }

  /** The channel of a connection, by its color, or none on the edge. */
  public Optional<Integer> channel() {
    return value == ModuleScreenValues.EDGE ? Optional.empty() : Optional.of(value - 1);
  }

  public int button() {
    return ModuleScreenCodes.button(kind.ordinal(), value);
  }

  /** The action of a code, or none for a code no button sends. */
  public static Optional<KeypadAction> of(int button) {
    int kind = ModuleScreenCodes.kindOf(button);
    int value = ModuleScreenCodes.valueOf(button);
    if (button < 0 || kind >= Kind.values().length || !fits(Kind.values()[kind], value)) {
      return Optional.empty();
    }
    return Optional.of(new KeypadAction(Kind.values()[kind], value));
  }

  private static boolean fits(Kind kind, int value) {
    return switch (kind) {
      case INPUT -> value > ModuleScreenValues.EDGE && value < ModuleScreenValues.CONNECTIONS;
      case OUTPUT -> value >= ModuleScreenValues.EDGE && value < ModuleScreenValues.CONNECTIONS;
      case CHOOSE, REVOKE -> value >= 0;
      case STRENGTH, OPEN -> value == LESS || value == MORE;
      case CODE -> KeypadCodes.decode(value).isPresent();
    };
  }
}
