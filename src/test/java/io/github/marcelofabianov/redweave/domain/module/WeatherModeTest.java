package io.github.marcelofabianov.redweave.domain.module;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/** The modes of the Weather Detector and their keys in the save (answers 459 and 467). */
class WeatherModeTest {
  private static final String RAIN = "rain";
  private static final String THUNDER = "thunder";
  private static final String CLEAR = "clear";
  private static final String UNKNOWN = "snow";

  @Test
  void theKeysAreTheOnesOfTheSave() {
    assertEquals(RAIN, WeatherMode.RAIN.key());
    assertEquals(THUNDER, WeatherMode.THUNDER.key());
    assertEquals(CLEAR, WeatherMode.CLEAR.key());
  }

  @ParameterizedTest
  @EnumSource(WeatherMode.class)
  void aKeyReadsBackItsMode(WeatherMode mode) {
    assertEquals(Optional.of(mode), WeatherMode.fromKey(mode.key()));
  }

  @Test
  void anUnknownKeyReadsNoMode() {
    assertEquals(Optional.empty(), WeatherMode.fromKey(UNKNOWN));
  }
}
