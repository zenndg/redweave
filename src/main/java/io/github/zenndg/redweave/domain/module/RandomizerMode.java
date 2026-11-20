package io.github.zenndg.redweave.domain.module;

import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * What the Signal Randomizer draws: any strength from 0 to 15, each as likely, or off or 15, half
 * and half (answer 476). The key is part of the save (answer 481, rule 15).
 */
public enum RandomizerMode {
  ANALOG,
  BINARY;

  private static final int STRENGTHS = Signal.MAX.strength() + 1;
  private static final int SIDES = 2;
  private static final int ON_SIDE = 1;

  public String key() {
    return name().toLowerCase(Locale.ROOT);
  }

  public static Optional<RandomizerMode> fromKey(String key) {
    return Arrays.stream(values()).filter(mode -> mode.key().equals(key)).findFirst();
  }

  Signal draw(Draws draws) {
    return switch (this) {
      case ANALOG -> new Signal(draws.next(STRENGTHS));
      case BINARY -> draws.next(SIDES) == ON_SIDE ? Signal.MAX : Signal.OFF;
    };
  }
}
