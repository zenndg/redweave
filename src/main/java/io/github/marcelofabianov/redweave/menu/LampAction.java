package io.github.marcelofabianov.redweave.menu;

import java.util.Optional;

/**
 * What a button of the Signal Lamp Screen asks the server: a step of `Strength`, the mode of the
 * control, a channel of the intensity or of the control, or none, or the position in the network
 * choice (answers 650, 651 and 656).
 */
public record LampAction(Kind kind, int value) {
  /** The value of no channel; a channel is its color plus one. */
  public static final int NONE = ModuleScreenValues.EDGE;

  private static final int LESS = 0;
  private static final int MORE = 1;
  private static final String INVALID = "Invalid value %d for %s";

  public enum Kind {
    STRENGTH,
    MODE,
    INTENSITY,
    CONTROL,
    CHOOSE,
    DELAY
  }

  public LampAction {
    if (!fits(kind, value)) {
      throw new IllegalArgumentException(String.format(INVALID, value, kind));
    }
  }

  /** A step of `Strength`: `−` one weaker and `+` one stronger. */
  public static LampAction strength(boolean stronger) {
    return new LampAction(Kind.STRENGTH, stronger ? MORE : LESS);
  }

  /** The mode of the control, by its position: `Hold` or `Pulse` (answer 656). */
  public static LampAction mode(int position) {
    return new LampAction(Kind.MODE, position);
  }

  /** A channel of the intensity or of the control, or none. */
  public static LampAction connect(Kind kind, Optional<Integer> channel) {
    return new LampAction(kind, channel.map(index -> index + 1).orElse(NONE));
  }

  public static LampAction choose(int position) {
    return new LampAction(Kind.CHOOSE, position);
  }

  /** A step of `Delay`, one second shorter or longer (answer 830). */
  public static LampAction delay(boolean longer) {
    return new LampAction(Kind.DELAY, DelaySteps.of(longer));
  }

  /** The code the game sends with the click. */
  public int button() {
    return ModuleScreenCodes.button(kind.ordinal(), value);
  }

  /** The action of a code, or none for a code no button sends. */
  public static Optional<LampAction> of(int button) {
    int kind = ModuleScreenCodes.kindOf(button);
    int value = ModuleScreenCodes.valueOf(button);
    if (button < 0 || kind >= Kind.values().length || !fits(Kind.values()[kind], value)) {
      return Optional.empty();
    }
    return Optional.of(new LampAction(Kind.values()[kind], value));
  }

  /** The strength a step of `Strength` adds: one more or one less. */
  public int step() {
    return value == MORE ? 1 : -1;
  }

  /** The channel of a connection, by its color, or none. */
  public Optional<Integer> channel() {
    return value == NONE ? Optional.empty() : Optional.of(value - 1);
  }

  private static boolean fits(Kind kind, int value) {
    return switch (kind) {
      case STRENGTH -> value == LESS || value == MORE;
      case MODE -> value >= 0 && value < LampScreenLayout.MODES;
      case INTENSITY, CONTROL -> value >= NONE && value < ModuleScreenValues.CONNECTIONS;
      case CHOOSE -> value >= 0;
      case DELAY -> DelaySteps.fits(value);
    };
  }
}
