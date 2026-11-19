package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.RedweaveMod;

/**
 * The keys of the texts of the Signal Inverter Screen and of its label, in common code for the data
 * generation (answers 448 and 451). `Input` and `Output` use the keys of the Node Screen.
 */
public final class InverterScreenTexts {
  private static final String PREFIX = "screen." + RedweaveMod.MOD_ID + ".signal_inverter.";

  public static final String ON = PREFIX + "on";
  public static final String OFF = PREFIX + "off";
  public static final String BURNT_OUT = PREFIX + "burnt_out";

  private InverterScreenTexts() {}
}
