package io.github.zenndg.redweave.domain.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * The rule of the Weather Detector: each mode against each weather, with no weather where the
 * dimension has none, and the Toggle that steps the mode (answers 459, 460, 462 to 465).
 */
class WeatherDetectorTest {
  @Test
  void aPlacedDetectorWatchesTheRain() {
    assertEquals(WeatherMode.RAIN, WeatherDetector.placed().mode());
  }

  @ParameterizedTest
  @MethodSource
  void eachModeMatchesItsWeather(WeatherMode mode, Weather weather, Signal expected) {
    assertEquals(expected, new WeatherDetector(mode).output(weather));
  }

  static Stream<Arguments> eachModeMatchesItsWeather() {
    return Stream.of(
        Arguments.of(WeatherMode.RAIN, Weather.CLEAR, Signal.OFF),
        Arguments.of(WeatherMode.RAIN, Weather.RAIN, Signal.MAX),
        Arguments.of(WeatherMode.RAIN, Weather.THUNDER, Signal.MAX),
        Arguments.of(WeatherMode.THUNDER, Weather.CLEAR, Signal.OFF),
        Arguments.of(WeatherMode.THUNDER, Weather.RAIN, Signal.OFF),
        Arguments.of(WeatherMode.THUNDER, Weather.THUNDER, Signal.MAX),
        Arguments.of(WeatherMode.CLEAR, Weather.CLEAR, Signal.MAX),
        Arguments.of(WeatherMode.CLEAR, Weather.RAIN, Signal.OFF),
        Arguments.of(WeatherMode.CLEAR, Weather.THUNDER, Signal.OFF));
  }

  @ParameterizedTest
  @EnumSource(WeatherMode.class)
  void withNoWeatherEveryModeIsOff(WeatherMode mode) {
    WeatherDetector detector = new WeatherDetector(mode);

    assertEquals(Signal.OFF, detector.output(Weather.NONE));
    assertEquals(DetectorState.NO_WEATHER, detector.state(Weather.NONE));
  }

  @Test
  void theStateFollowsTheOutput() {
    WeatherDetector detector = WeatherDetector.placed();

    assertEquals(DetectorState.ON, detector.state(Weather.RAIN));
    assertEquals(DetectorState.OFF, detector.state(Weather.CLEAR));
  }

  @Test
  void theToggleStepsTheModeRound() {
    WeatherDetector thunder = WeatherDetector.placed().toggled();
    WeatherDetector clear = thunder.toggled();

    assertEquals(WeatherMode.THUNDER, thunder.mode());
    assertEquals(WeatherMode.CLEAR, clear.mode());
    assertEquals(WeatherMode.RAIN, clear.toggled().mode());
  }

  @Test
  void aDetectorNeedsAMode() {
    assertThrows(NullPointerException.class, () -> new WeatherDetector(null));
  }
}
