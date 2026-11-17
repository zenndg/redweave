package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.domain.module.RandomizerMode;
import java.util.Optional;

/**
 * What a button of the Signal Randomizer Screen asks the server: start or stop, a mode, a step of
 * `Interval`, a connection of `Output`, or the position in the network choice (answers 398, 400 and
 * 487).
 */
public record RandomizerAction(Kind kind, int value) {
  /** The value of a connection on the edge; a channel is its color plus one. */
  public static final int EDGE = ModuleScreenValues.EDGE;

  private static final String INVALID = "Invalid value %d for %s";

  public enum Kind {
    TOGGLE,
    MODE,
    INTERVAL,
    OUTPUT,
    CHOOSE,
    DELAY
  }

  public RandomizerAction {
    if (!fits(kind, value)) {
      throw new IllegalArgumentException(String.format(INVALID, value, kind));
    }
  }

  public static RandomizerAction toggle() {
    return new RandomizerAction(Kind.TOGGLE, 0);
  }

  public static RandomizerAction mode(RandomizerMode mode) {
    return new RandomizerAction(Kind.MODE, mode.ordinal());
  }

  /** A step of `Interval`: shorter or longer, by 0.1 s or, with Shift, 1 s (answer 398). */
  public static RandomizerAction step(boolean longer, boolean shift) {
    return new RandomizerAction(Kind.INTERVAL, IntervalSteps.indexOf(longer, shift));
  }

  public static RandomizerAction choose(int position) {
    return new RandomizerAction(Kind.CHOOSE, position);
  }

  /** A step of `Delay`, one second shorter or longer (answer 830). */
  public static RandomizerAction delay(boolean longer) {
    return new RandomizerAction(Kind.DELAY, DelaySteps.of(longer));
  }

  /** The code the game sends with the click. */
  public int button() {
    return ModuleScreenCodes.button(kind.ordinal(), value);
  }

  /** The action of a code, or none for a code no button sends. */
  public static Optional<RandomizerAction> of(int button) {
    int kind = ModuleScreenCodes.kindOf(button);
    int value = ModuleScreenCodes.valueOf(button);
    if (button < 0 || kind >= Kind.values().length || !fits(Kind.values()[kind], value)) {
      return Optional.empty();
    }
    return Optional.of(new RandomizerAction(Kind.values()[kind], value));
  }

  public RandomizerMode randomizerMode() {
    return RandomizerMode.values()[value];
  }

  /** The ticks a step of `Interval` adds, negative to shorten it. */
  public int ticks() {
    return IntervalSteps.ticks(value);
  }

  /** The channel of a connection, by its color, or none on the edge. */
  public Optional<Integer> channel() {
    return value == ModuleScreenValues.EDGE ? Optional.empty() : Optional.of(value - 1);
  }

  private static boolean fits(Kind kind, int value) {
    return switch (kind) {
      case TOGGLE -> value == 0;
      case MODE -> value >= 0 && value < RandomizerMode.values().length;
      case INTERVAL -> value >= 0 && value < IntervalSteps.COUNT;
      case OUTPUT -> value >= ModuleScreenValues.EDGE && value < ModuleScreenValues.CONNECTIONS;
      case CHOOSE -> value >= 0;
      case DELAY -> DelaySteps.fits(value);
    };
  }
}
