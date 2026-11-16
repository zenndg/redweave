package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.RedweaveMod;

/**
 * The keys of the texts of the Signal Lock Screen and of its label (answers 636 to 640). `Locked`
 * and `Unlocked` take the gender of the item in Portuguese, `fechadura`; `Strength` uses the key of
 * the lever, and `Input` and `Output` the ones of the Node Screen.
 */
public final class LockScreenTexts {
  private static final String PREFIX = "screen." + RedweaveMod.MOD_ID + ".signal_lock.";

  public static final String LOCKED = PREFIX + "locked";
  public static final String UNLOCKED = PREFIX + "unlocked";
  public static final String KEY = PREFIX + "key";
  public static final String PULSE = PREFIX + "pulse";

  /** What a player who does not own the lock sees in place of its key (answer 637). */
  public static final String HIDDEN = "?";

  private LockScreenTexts() {}

  public static String of(boolean open) {
    return open ? UNLOCKED : LOCKED;
  }
}
