package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.RedweaveMod;

/**
 * The keys of the texts of the two Signal Keypad Screens and of its label (answers 662, 663, 665,
 * 905 and 908). `Strength` uses the key of the lever, and `Input` and `Output` the ones of the Node
 * Screen.
 */
public final class KeypadScreenTexts {
  private static final String PREFIX = "screen." + RedweaveMod.MOD_ID + ".signal_keypad.";

  public static final String LOCKED = PREFIX + "locked";
  public static final String UNLOCKED = PREFIX + "unlocked";
  public static final String CODE = PREFIX + "code";
  public static final String CHANGE = PREFIX + "change";
  public static final String OPEN_TIME = PREFIX + "open_time";
  public static final String CARDS = PREFIX + "cards";
  public static final String NO_CARDS = PREFIX + "no_cards";
  public static final String CLEAR = PREFIX + "clear";
  public static final String ENTER = PREFIX + "enter";
  public static final String GRANTED = PREFIX + "granted";
  public static final String WRONG = PREFIX + "wrong";
  public static final String WAIT = PREFIX + "wait";
  public static final String NEW_CODE = PREFIX + "new_code";
  public static final String REPEAT_CODE = PREFIX + "repeat_code";
  public static final String MISMATCH = PREFIX + "mismatch";
  public static final String NO_CODE = PREFIX + "no_code";

  /** What the screen shows of a code: never its digits (answer 660). */
  public static final String HIDDEN = "••••";

  private KeypadScreenTexts() {}

  public static String of(boolean open) {
    return open ? UNLOCKED : LOCKED;
  }
}
