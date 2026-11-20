package io.github.zenndg.redweave.domain.module;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A code of the Signal Keypad, as typed: 1 to 6 digits (answer 659). It never shows its digits, not
 * even in a log (answer 660); only its hash is kept (answer 897).
 */
public record KeypadCode(String digits) {
  /** The most digits of a code (answer 659). */
  public static final int MAX_DIGITS = 6;

  /** What a code shows of itself. */
  public static final String HIDDEN = "••••";

  private static final Pattern DIGITS = Pattern.compile("[0-9]{1," + MAX_DIGITS + "}");
  private static final String INVALID = "A code has 1 to " + MAX_DIGITS + " digits";

  public KeypadCode {
    Objects.requireNonNull(digits, INVALID);
    if (!DIGITS.matcher(digits).matches()) {
      throw new IllegalArgumentException(INVALID);
    }
  }

  @Override
  public String toString() {
    return HIDDEN;
  }
}
