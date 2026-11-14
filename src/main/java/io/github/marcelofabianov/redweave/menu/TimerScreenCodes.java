package io.github.marcelofabianov.redweave.menu;

import java.util.Optional;

/**
 * The buttons of the Signal Timer Screen as the number the game sends with a menu click: the kind
 * in the 3 low bits and the value above them. The server checks every code (rule 16).
 */
public final class TimerScreenCodes {
  private static final int KIND_BITS = 3;
  private static final int KIND_MASK = (1 << KIND_BITS) - 1;

  private TimerScreenCodes() {}

  public static int button(TimerAction action) {
    return action.kind().ordinal() | action.value() << KIND_BITS;
  }

  /** The action of a code, or none for a code no button sends. */
  public static Optional<TimerAction> actionOf(int button) {
    int kind = button & KIND_MASK;
    int value = button >> KIND_BITS;
    if (button < 0 || kind >= TimerAction.Kind.values().length) {
      return Optional.empty();
    }
    TimerAction.Kind found = TimerAction.Kind.values()[kind];
    return TimerAction.fits(found, value)
        ? Optional.of(new TimerAction(found, value))
        : Optional.empty();
  }
}
