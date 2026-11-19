package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.RedweaveMod;

/**
 * The keys of the texts of the Signal Lever Screen and of its label, in common code for the data
 * generation (answer 582). `Output` uses the key of the Node Screen.
 */
public final class LeverScreenTexts {
  private static final String PREFIX = "screen." + RedweaveMod.MOD_ID + ".signal_lever.";

  public static final String ON = PREFIX + "on";
  public static final String OFF = PREFIX + "off";
  public static final String STRENGTH = PREFIX + "strength";

  private LeverScreenTexts() {}

  public static String of(boolean on) {
    return on ? ON : OFF;
  }
}
