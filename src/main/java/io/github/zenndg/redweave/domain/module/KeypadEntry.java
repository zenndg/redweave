package io.github.zenndg.redweave.domain.module;

import java.util.Objects;

/**
 * What a try on a Signal Keypad gave: the keypad after it, the misses of the player that count, the
 * ticks still to wait, and whether the owner is warned (answers 663 to 666 and 914).
 */
public record KeypadEntry(
    Kind kind, SignalKeypad keypad, int misses, long waitTicks, boolean warnsOwner) {
  public enum Kind {
    GRANTED,
    WRONG,
    WAIT,
    NO_POWER,
    NO_CODE,
    NOT_LINKED
  }

  public KeypadEntry {
    Objects.requireNonNull(kind);
    Objects.requireNonNull(keypad);
  }

  /** A try that changed nothing, as with no power, no code or a card not linked. */
  static KeypadEntry refused(Kind kind, SignalKeypad keypad) {
    return new KeypadEntry(kind, keypad, 0, 0, false);
  }
}
