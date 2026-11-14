package io.github.marcelofabianov.redweave.domain.module;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Objects;

/**
 * The rule of the Weather Detector: 15 while the weather of its dimension matches its mode, and 0
 * otherwise, always 0 where there is no weather (answers 459, 460 and 464). It keeps only its mode;
 * the weather is read again every tick and never saved (answers 467 and 468).
 */
public record WeatherDetector(WeatherMode mode) {
  public WeatherDetector {
    Objects.requireNonNull(mode);
  }

  /** A new detector watches the rain and emits on its first tick (answer 463). */
  public static WeatherDetector placed() {
    return new WeatherDetector(WeatherMode.RAIN);
  }

  /** The next mode, as the Toggle of the Configurator asks (answer 462). */
  public WeatherDetector toggled() {
    return new WeatherDetector(mode.next());
  }

  public Signal output(Weather weather) {
    return mode.matches(weather) ? Signal.MAX : Signal.OFF;
  }

  public DetectorState state(Weather weather) {
    if (weather == Weather.NONE) {
      return DetectorState.NO_WEATHER;
    }
    return output(weather).isOn() ? DetectorState.ON : DetectorState.OFF;
  }
}
