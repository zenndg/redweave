package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.RedweaveMod;

/**
 * The keys of the texts of the Signal Button Screen and of its label (answers 556 and 595). `On`
 * and `Off` have their own keys, apart from the lever, because Portuguese gives them the gender of
 * the item; `Strength` uses the key of the lever, and `Output` the one of the Node Screen.
 */
public final class ButtonScreenTexts {
  private static final String PREFIX = "screen." + RedweaveMod.MOD_ID + ".signal_button.";

  public static final String ON = PREFIX + "on";
  public static final String OFF = PREFIX + "off";
  public static final String ON_TIME = PREFIX + "on_time";

  private ButtonScreenTexts() {}

  public static String of(boolean on) {
    return on ? ON : OFF;
  }
}
