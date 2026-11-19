package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.domain.module.WeatherMode;
import java.util.Optional;

/**
 * What a button of the Weather Detector Screen asks the server: a mode, a connection of `Output`,
 * as the edge or a channel, or the position in the network choice (answers 400 and 470).
 */
public record DetectorAction(Kind kind, int value) {
  /** The value of a connection on the edge; a channel is its color plus one. */
  public static final int EDGE = ModuleScreenValues.EDGE;

  private static final String INVALID = "Invalid value %d for %s";

  public enum Kind {
    MODE,
    OUTPUT,
    CHOOSE,
    DELAY
  }

  public DetectorAction {
    if (!fits(kind, value)) {
      throw new IllegalArgumentException(String.format(INVALID, value, kind));
    }
  }

  public static DetectorAction mode(WeatherMode mode) {
    return new DetectorAction(Kind.MODE, mode.ordinal());
  }

  public static DetectorAction choose(int position) {
    return new DetectorAction(Kind.CHOOSE, position);
  }

  /** A step of `Delay`, one second shorter or longer (answer 830). */
  public static DetectorAction delay(boolean longer) {
    return new DetectorAction(Kind.DELAY, DelaySteps.of(longer));
  }

  /** The code the game sends with the click. */
  public int button() {
    return ModuleScreenCodes.button(kind.ordinal(), value);
  }

  /** The action of a code, or none for a code no button sends. */
  public static Optional<DetectorAction> of(int button) {
    int kind = ModuleScreenCodes.kindOf(button);
    int value = ModuleScreenCodes.valueOf(button);
    if (button < 0 || kind >= Kind.values().length || !fits(Kind.values()[kind], value)) {
      return Optional.empty();
    }
    return Optional.of(new DetectorAction(Kind.values()[kind], value));
  }

  /** The mode a `MODE` action picks. */
  public WeatherMode weatherMode() {
    return WeatherMode.values()[value];
  }

  /** The channel of a connection, by its color, or none on the edge. */
  public Optional<Integer> channel() {
    return value == ModuleScreenValues.EDGE ? Optional.empty() : Optional.of(value - 1);
  }

  private static boolean fits(Kind kind, int value) {
    return switch (kind) {
      case MODE -> value >= 0 && value < WeatherMode.values().length;
      case OUTPUT -> value >= ModuleScreenValues.EDGE && value < ModuleScreenValues.CONNECTIONS;
      case CHOOSE -> value >= 0;
      case DELAY -> DelaySteps.fits(value);
    };
  }
}
