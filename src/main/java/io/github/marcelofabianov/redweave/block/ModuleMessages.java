package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.RedweaveMod;

/** The keys of what the action bar shows about the Signal Modules (answer 421). */
public final class ModuleMessages {
  private static final String PREFIX = "message." + RedweaveMod.MOD_ID + ".module.";
  private static final String TIMER = "message." + RedweaveMod.MOD_ID + ".signal_timer.";
  private static final String INVERTER = "message." + RedweaveMod.MOD_ID + ".signal_inverter.";
  private static final String DETECTOR = "message." + RedweaveMod.MOD_ID + ".weather_detector.";
  private static final String RANDOMIZER = "message." + RedweaveMod.MOD_ID + ".signal_randomizer.";
  private static final String LEVER = "message." + RedweaveMod.MOD_ID + ".signal_lever.";
  private static final String BUTTON = "message." + RedweaveMod.MOD_ID + ".signal_button.";
  private static final String PLATE = "message." + RedweaveMod.MOD_ID + ".signal_pressure_plate.";
  private static final String SENSOR = "message." + RedweaveMod.MOD_ID + ".signal_item_sensor.";
  private static final String LOCK = "message." + RedweaveMod.MOD_ID + ".signal_lock.";
  private static final String ALARM = "message." + RedweaveMod.MOD_ID + ".signal_alarm.";
  private static final String FLIP_FLOP = "message." + RedweaveMod.MOD_ID + ".signal_flip_flop.";

  public static final String USE_TO_OPEN = PREFIX + "use_to_open";
  public static final String PICKED_UP = PREFIX + "picked_up";
  public static final String NOT_OWNER = PREFIX + "not_owner";
  public static final String OUTPUT = PREFIX + "output";
  public static final String WRENCH_ONLY = PREFIX + "wrench_only";
  public static final String TIMER_RUNNING = TIMER + "running";
  public static final String TIMER_STOPPED = TIMER + "stopped";
  public static final String INVERTER_NOTHING_TO_TOGGLE = INVERTER + "nothing_to_toggle";
  public static final String DETECTOR_MODE = DETECTOR + "mode";
  public static final String RANDOMIZER_RUNNING = RANDOMIZER + "running";
  public static final String RANDOMIZER_STOPPED = RANDOMIZER + "stopped";
  public static final String LEVER_ON = LEVER + "on";
  public static final String LEVER_OFF = LEVER + "off";
  public static final String BUTTON_PRESSED = BUTTON + "pressed";
  public static final String PRESSURE_PLATE_MODE = PLATE + "mode";
  public static final String PRESSURE_PLATE_PULSED = PLATE + "pulsed";
  public static final String ITEM_SENSOR_NOTHING_TO_TOGGLE = SENSOR + "nothing_to_toggle";
  public static final String LOCK_UNLOCKED = LOCK + "unlocked";
  public static final String ALARM_RING = ALARM + "ring";
  public static final String ALARM_SILENCED = ALARM + "silenced";
  public static final String FLIP_FLOP_ON = FLIP_FLOP + "on";
  public static final String FLIP_FLOP_OFF = FLIP_FLOP + "off";

  private ModuleMessages() {}
}
