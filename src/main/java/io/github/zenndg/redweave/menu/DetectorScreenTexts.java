package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.domain.module.DetectorState;
import io.github.zenndg.redweave.domain.module.WeatherMode;

/**
 * The keys of the texts of the Weather Detector Screen and of its label, in common code for the
 * data generation (answers 459, 465 and 469). `Output` uses the key of the Node Screen.
 */
public final class DetectorScreenTexts {
  private static final String PREFIX = "screen." + RedweaveMod.MOD_ID + ".weather_detector.";

  public static final String MODE = PREFIX + "mode";
  public static final String RAIN = PREFIX + "rain";
  public static final String THUNDER = PREFIX + "thunder";
  public static final String CLEAR = PREFIX + "clear";
  public static final String ON = PREFIX + "on";
  public static final String OFF = PREFIX + "off";
  public static final String NO_WEATHER = PREFIX + "no_weather";

  private DetectorScreenTexts() {}

  public static String of(WeatherMode mode) {
    return switch (mode) {
      case RAIN -> RAIN;
      case THUNDER -> THUNDER;
      case CLEAR -> CLEAR;
    };
  }

  public static String of(DetectorState state) {
    return switch (state) {
      case ON -> ON;
      case OFF -> OFF;
      case NO_WEATHER -> NO_WEATHER;
    };
  }
}
