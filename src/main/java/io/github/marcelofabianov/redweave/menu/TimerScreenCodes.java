package io.github.marcelofabianov.redweave.menu;

import java.util.Optional;

/**
 * The buttons of the Signal Timer Screen as the number the game sends with a menu click, coded as
 * every module screen (ModuleScreenCodes). The server checks every code (rule 16).
 */
public final class TimerScreenCodes {
  private TimerScreenCodes() {}

  public static int button(TimerAction action) {
    return ModuleScreenCodes.button(action.kind().ordinal(), action.value());
  }

  /** The action of a code, or none for a code no button sends. */
  public static Optional<TimerAction> actionOf(int button) {
    int kind = ModuleScreenCodes.kindOf(button);
    int value = ModuleScreenCodes.valueOf(button);
    if (button < 0 || kind >= TimerAction.Kind.values().length) {
      return Optional.empty();
    }
    TimerAction.Kind found = TimerAction.Kind.values()[kind];
    return TimerAction.fits(found, value)
        ? Optional.of(new TimerAction(found, value))
        : Optional.empty();
  }
}
