package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.module.AlarmListeners;

/**
 * The keys of the texts of the Signal Alarm Screen and of its label (answers 645 to 649). `Input`
 * uses the key of the Node Screen.
 */
public final class AlarmScreenTexts {
  private static final String PREFIX = "screen." + RedweaveMod.MOD_ID + ".signal_alarm.";

  public static final String QUIET = PREFIX + "quiet";
  public static final String RINGING = PREFIX + "ringing";
  public static final String LISTENERS = PREFIX + "listeners";
  public static final String REPEATS = PREFIX + "repeats";
  public static final String INTERVAL = PREFIX + "interval";
  public static final String DURATION = PREFIX + "duration";
  public static final String OWNER = PREFIX + "owner";
  public static final String NETWORK = PREFIX + "network";
  public static final String RANGE = PREFIX + "range";

  private AlarmScreenTexts() {}

  public static String of(boolean ringing) {
    return ringing ? RINGING : QUIET;
  }

  /** The text of the listeners the owner chose (answer 645). */
  public static String of(AlarmListeners listeners) {
    return switch (listeners) {
      case OWNER -> OWNER;
      case NETWORK -> NETWORK;
      case RANGE -> RANGE;
    };
  }
}
