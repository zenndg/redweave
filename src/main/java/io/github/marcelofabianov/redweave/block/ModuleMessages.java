package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.RedweaveMod;

/** The keys of what the action bar shows about the Signal Modules (answer 421). */
public final class ModuleMessages {
  private static final String PREFIX = "message." + RedweaveMod.MOD_ID + ".module.";
  private static final String TIMER = "message." + RedweaveMod.MOD_ID + ".signal_timer.";
  private static final String INVERTER = "message." + RedweaveMod.MOD_ID + ".signal_inverter.";
  private static final String DETECTOR = "message." + RedweaveMod.MOD_ID + ".weather_detector.";
  private static final String RANDOMIZER = "message." + RedweaveMod.MOD_ID + ".signal_randomizer.";

  public static final String USE_TO_OPEN = PREFIX + "use_to_open";
  public static final String PICKED_UP = PREFIX + "picked_up";
  public static final String NOT_OWNER = PREFIX + "not_owner";
  public static final String OUTPUT = PREFIX + "output";
  public static final String NOT_COPIED = PREFIX + "not_copied";
  public static final String TIMER_RUNNING = TIMER + "running";
  public static final String TIMER_STOPPED = TIMER + "stopped";
  public static final String INVERTER_NOTHING_TO_TOGGLE = INVERTER + "nothing_to_toggle";
  public static final String DETECTOR_MODE = DETECTOR + "mode";
  public static final String RANDOMIZER_RUNNING = RANDOMIZER + "running";
  public static final String RANDOMIZER_STOPPED = RANDOMIZER + "stopped";

  private ModuleMessages() {}
}
