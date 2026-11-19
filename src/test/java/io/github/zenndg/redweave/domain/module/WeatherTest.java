package io.github.zenndg.redweave.domain.module;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** The weather the detector reads from its dimension (answers 458 and 464). */
class WeatherTest {
  @Test
  void aDimensionWithNoWeatherHasNone() {
    assertEquals(Weather.NONE, Weather.of(false, true, true));
  }

  @Test
  void rainWithThunderIsAStorm() {
    assertEquals(Weather.THUNDER, Weather.of(true, true, true));
  }

  @Test
  void rainAloneIsRain() {
    assertEquals(Weather.RAIN, Weather.of(true, true, false));
  }

  /** The game shows no storm without rain, so thunder alone reads as a clear sky. */
  @Test
  void noRainIsClearEvenWithThunder() {
    assertEquals(Weather.CLEAR, Weather.of(true, false, true));
    assertEquals(Weather.CLEAR, Weather.of(true, false, false));
  }
}
