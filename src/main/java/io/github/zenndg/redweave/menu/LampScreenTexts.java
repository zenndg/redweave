package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.domain.module.LampMode;

/**
 * The keys of the texts of the Signal Lamp Screen (answers 650, 651 and 656). `On`, `Off` and
 * `Strength` use the keys of the lever, feminine in Portuguese as `lâmpada`.
 */
public final class LampScreenTexts {
  private static final String PREFIX = "screen." + RedweaveMod.MOD_ID + ".signal_lamp.";

  public static final String INTENSITY = PREFIX + "intensity";
  public static final String CONTROL = PREFIX + "control";
  public static final String MODE = PREFIX + "mode";
  public static final String HOLD = PREFIX + "hold";
  public static final String PULSE = PREFIX + "pulse";
  public static final String NONE = PREFIX + "none";
  public static final String LIGHT = PREFIX + "light";

  private LampScreenTexts() {}

  public static String of(LampMode mode) {
    return mode == LampMode.HOLD ? HOLD : PULSE;
  }
}
