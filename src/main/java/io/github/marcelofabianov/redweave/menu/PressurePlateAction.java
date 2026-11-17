package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.domain.module.PlateMode;
import java.util.Optional;

/**
 * What a button of the Signal Pressure Plate Screen asks the server: a step of `Strength`, a mode,
 * a connection of `Output`, or the position in the network choice (answers 414, 552 and 621).
 */
public record PressurePlateAction(Kind kind, int value) {
  /** The value of a connection on the edge; a channel is its color plus one. */
  public static final int EDGE = ModuleScreenValues.EDGE;

  private static final int WEAKER = 0;
  private static final int STRONGER = 1;
  private static final String INVALID = "Invalid value %d for %s";

  public enum Kind {
    STRENGTH,
    MODE,
    OUTPUT,
    CHOOSE,
    DELAY
  }

  public PressurePlateAction {
    if (!fits(kind, value)) {
      throw new IllegalArgumentException(String.format(INVALID, value, kind));
    }
  }

  /** A step of `Strength`: `−` one weaker and `+` one stronger. */
  public static PressurePlateAction step(boolean stronger) {
    return new PressurePlateAction(Kind.STRENGTH, stronger ? STRONGER : WEAKER);
  }

  public static PressurePlateAction mode(PlateMode mode) {
    return new PressurePlateAction(Kind.MODE, mode.ordinal());
  }

  public static PressurePlateAction output(Optional<Integer> channel) {
    return new PressurePlateAction(Kind.OUTPUT, channel.map(index -> index + 1).orElse(EDGE));
  }

  public static PressurePlateAction choose(int position) {
    return new PressurePlateAction(Kind.CHOOSE, position);
  }

  /** A step of `Delay`, one second shorter or longer (answer 830). */
  public static PressurePlateAction delay(boolean longer) {
    return new PressurePlateAction(Kind.DELAY, DelaySteps.of(longer));
  }

  /** The code the game sends with the click. */
  public int button() {
    return ModuleScreenCodes.button(kind.ordinal(), value);
  }

  /** The action of a code, or none for a code no button sends. */
  public static Optional<PressurePlateAction> of(int button) {
    int kind = ModuleScreenCodes.kindOf(button);
    int value = ModuleScreenCodes.valueOf(button);
    if (button < 0 || kind >= Kind.values().length || !fits(Kind.values()[kind], value)) {
      return Optional.empty();
    }
    return Optional.of(new PressurePlateAction(Kind.values()[kind], value));
  }

  /** The strength a step adds: one more or one less. */
  public int step() {
    return value == STRONGER ? 1 : -1;
  }

  /** The mode a `MODE` action picks. */
  public PlateMode plateMode() {
    return PlateMode.values()[value];
  }

  /** The channel of a connection, by its color, or none on the edge. */
  public Optional<Integer> channel() {
    return value == ModuleScreenValues.EDGE ? Optional.empty() : Optional.of(value - 1);
  }

  private static boolean fits(Kind kind, int value) {
    return switch (kind) {
      case STRENGTH -> value == WEAKER || value == STRONGER;
      case MODE -> value >= 0 && value < PlateMode.values().length;
      case OUTPUT -> value >= ModuleScreenValues.EDGE && value < ModuleScreenValues.CONNECTIONS;
      case CHOOSE -> value >= 0;
      case DELAY -> DelaySteps.fits(value);
    };
  }
}
