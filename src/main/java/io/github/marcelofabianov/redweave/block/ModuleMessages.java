package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.RedweaveMod;

/** The keys of what the action bar shows about the Signal Modules (answer 421). */
public final class ModuleMessages {
  private static final String PREFIX = "message." + RedweaveMod.MOD_ID + ".module.";
  private static final String TIMER = "message." + RedweaveMod.MOD_ID + ".signal_timer.";

  public static final String USE_TO_OPEN = PREFIX + "use_to_open";
  public static final String PICKED_UP = PREFIX + "picked_up";
  public static final String NOT_OWNER = PREFIX + "not_owner";
  public static final String OUTPUT = PREFIX + "output";
  public static final String NOT_COPIED = PREFIX + "not_copied";
  public static final String TIMER_RUNNING = TIMER + "running";
  public static final String TIMER_STOPPED = TIMER + "stopped";

  private ModuleMessages() {}
}
