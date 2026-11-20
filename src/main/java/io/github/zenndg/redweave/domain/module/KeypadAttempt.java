package io.github.zenndg.redweave.domain.module;

/** The wrong tries of one player on a keypad and the tick of the world of the last (898, 910). */
public record KeypadAttempt(int count, long lastTick) {
  private static final String INVALID = "A try needs a count of 1+ and a tick of 0+";

  public KeypadAttempt {
    if (count < 1 || lastTick < 0) {
      throw new IllegalArgumentException(INVALID);
    }
  }
}
