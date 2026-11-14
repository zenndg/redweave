package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.RedweaveMod;

/**
 * The keys of the texts of the Signal Timer Screen, in common code for the data generation. The
 * shared parts, as the network choice and the way back, use the keys of the Node Screen.
 */
public final class TimerScreenTexts {
  private static final String PREFIX = "screen." + RedweaveMod.MOD_ID + ".signal_timer.";

  public static final String INTERVAL = PREFIX + "interval";
  public static final String PULSE = PREFIX + "pulse";
  public static final String OUTPUT = PREFIX + "output";
  public static final String PAUSE = PREFIX + "pause";
  public static final String EDGE = PREFIX + "edge";
  public static final String CHANNEL = PREFIX + "channel";
  public static final String RUNNING = PREFIX + "running";
  public static final String STOPPED = PREFIX + "stopped";
  public static final String PAUSED = PREFIX + "paused";
  public static final String SECONDS = PREFIX + "seconds";
  public static final String TICKS = PREFIX + "ticks";

  private TimerScreenTexts() {}
}
