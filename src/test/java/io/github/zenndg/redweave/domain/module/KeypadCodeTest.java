package io.github.zenndg.redweave.domain.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** The code of a Signal Keypad: 1 to 6 digits, chosen by its owner (answer 659). */
class KeypadCodeTest {
  private static final String ONE_DIGIT = "7";
  private static final String SIX_DIGITS = "123456";
  private static final String SEVEN_DIGITS = "1234567";
  private static final String LETTERS = "12a4";
  private static final String EMPTY = "";

  @Test
  void oneToSixDigitsAreACode() {
    assertEquals(ONE_DIGIT, new KeypadCode(ONE_DIGIT).digits());
    assertEquals(SIX_DIGITS, new KeypadCode(SIX_DIGITS).digits());
  }

  @Test
  void noDigitsMoreThanSixOrLettersAreRefused() {
    assertThrows(IllegalArgumentException.class, () -> new KeypadCode(EMPTY));
    assertThrows(IllegalArgumentException.class, () -> new KeypadCode(SEVEN_DIGITS));
    assertThrows(IllegalArgumentException.class, () -> new KeypadCode(LETTERS));
    assertThrows(NullPointerException.class, () -> new KeypadCode(null));
  }

  @Test
  void itNeverShowsItsDigits() {
    assertEquals(KeypadCode.HIDDEN, new KeypadCode(SIX_DIGITS).toString());
  }
}
