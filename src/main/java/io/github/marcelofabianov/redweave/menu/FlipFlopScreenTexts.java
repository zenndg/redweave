package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.RedweaveMod;

/**
 * The keys of the texts of the Signal Flip-Flop Screen and of its label, in common code for the
 * data generation (answer 890). `Strength` uses the key of the Signal Lever Screen, `Delay` the
 * shared one and `Input` and `Output` the keys of the Node Screen.
 */
public final class FlipFlopScreenTexts {
  private static final String PREFIX = "screen." + RedweaveMod.MOD_ID + ".signal_flip_flop.";

  public static final String ON = PREFIX + "on";
  public static final String OFF = PREFIX + "off";

  private FlipFlopScreenTexts() {}

  /** `On` or `Off`, the state kept by the flip-flop (answer 890). */
  public static String of(boolean on) {
    return on ? ON : OFF;
  }
}
