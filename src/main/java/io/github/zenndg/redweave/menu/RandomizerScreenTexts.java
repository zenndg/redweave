package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.domain.module.RandomizerMode;
import io.github.zenndg.redweave.domain.module.RandomizerState;

/**
 * The keys of the texts of the Signal Randomizer Screen and of its label, in common code for the
 * data generation (answers 476, 484 and 486). `Interval`, `Running` and `Stopped` use the keys of
 * the Signal Timer Screen, and `Output` the one of the Node Screen.
 */
public final class RandomizerScreenTexts {
  private static final String PREFIX = "screen." + RedweaveMod.MOD_ID + ".signal_randomizer.";

  public static final String MODE = PREFIX + "mode";
  public static final String ANALOG = PREFIX + "analog";
  public static final String BINARY = PREFIX + "binary";

  private RandomizerScreenTexts() {}

  public static String of(RandomizerMode mode) {
    return switch (mode) {
      case ANALOG -> ANALOG;
      case BINARY -> BINARY;
    };
  }

  public static String of(RandomizerState state) {
    return switch (state) {
      case RUNNING -> TimerScreenTexts.RUNNING;
      case STOPPED -> TimerScreenTexts.STOPPED;
    };
  }
}
