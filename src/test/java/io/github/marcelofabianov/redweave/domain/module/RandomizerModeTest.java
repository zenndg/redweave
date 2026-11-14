package io.github.marcelofabianov.redweave.domain.module;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

/** The modes of the Signal Randomizer and their keys in the save (answers 476 and 481). */
class RandomizerModeTest {
  private static final String ANALOG = "analog";
  private static final String BINARY = "binary";
  private static final String UNKNOWN = "dice";

  @Test
  void theKeysAreTheOnesOfTheSave() {
    assertEquals(ANALOG, RandomizerMode.ANALOG.key());
    assertEquals(BINARY, RandomizerMode.BINARY.key());
  }

  @ParameterizedTest
  @EnumSource(RandomizerMode.class)
  void aKeyReadsBackItsMode(RandomizerMode mode) {
    assertEquals(Optional.of(mode), RandomizerMode.fromKey(mode.key()));
  }

  @Test
  void anUnknownKeyReadsNoMode() {
    assertEquals(Optional.empty(), RandomizerMode.fromKey(UNKNOWN));
  }
}
