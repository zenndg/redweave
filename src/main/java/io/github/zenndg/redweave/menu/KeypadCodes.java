package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.domain.module.KeypadCode;
import java.util.Optional;

/**
 * A code of the Signal Keypad as the number a click carries: its length times a million plus its
 * digits, so leading zeros are kept. The server checks every number it gets (rule 16).
 */
public final class KeypadCodes {
  private static final int MILLION = 1_000_000;
  private static final String PADDED = "%%0%dd";

  private KeypadCodes() {}

  public static int encode(KeypadCode code) {
    return code.digits().length() * MILLION + Integer.parseInt(code.digits());
  }

  /** The code of a number, or none for a number no screen sends. */
  public static Optional<KeypadCode> decode(int value) {
    int length = value / MILLION;
    int number = value % MILLION;
    if (value < 0 || length < 1 || length > KeypadCode.MAX_DIGITS) {
      return Optional.empty();
    }
    String digits = String.format(String.format(PADDED, length), number);
    return digits.length() == length ? Optional.of(new KeypadCode(digits)) : Optional.empty();
  }
}
