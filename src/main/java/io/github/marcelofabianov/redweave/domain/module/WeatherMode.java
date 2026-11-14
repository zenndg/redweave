package io.github.marcelofabianov.redweave.domain.module;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * What a Weather Detector watches: rain, which also counts a storm, only a storm, or a clear sky
 * (answer 459). The key is part of the save (answer 467, rule 15).
 */
public enum WeatherMode {
  RAIN,
  THUNDER,
  CLEAR;

  public String key() {
    return name().toLowerCase(Locale.ROOT);
  }

  public static Optional<WeatherMode> fromKey(String key) {
    return Arrays.stream(values()).filter(mode -> mode.key().equals(key)).findFirst();
  }

  /** The mode after this one, for the Toggle: rain, thunder, clear and rain again (462). */
  WeatherMode next() {
    return values()[(ordinal() + 1) % values().length];
  }

  boolean matches(Weather weather) {
    return switch (this) {
      case RAIN -> weather == Weather.RAIN || weather == Weather.THUNDER;
      case THUNDER -> weather == Weather.THUNDER;
      case CLEAR -> weather == Weather.CLEAR;
    };
  }
}
