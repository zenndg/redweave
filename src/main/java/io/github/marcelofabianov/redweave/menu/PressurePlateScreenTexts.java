package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.module.PlateMode;

/**
 * The keys of the texts of the Signal Pressure Plate Screen and of its label, in common code for
 * the data generation (answers 609 and 621). `On`, `Off` and `Strength` are those of the Signal
 * Lever, feminine in Portuguese as "placa"; `Output` uses the key of the Node Screen.
 */
public final class PressurePlateScreenTexts {
  private static final String PREFIX = "screen." + RedweaveMod.MOD_ID + ".signal_pressure_plate.";
  private static final String STATE = PREFIX + "state.";
  private static final String ON_PREFIX = STATE + "on_";
  private static final String OFF_PREFIX = STATE + "off_";

  public static final String MODE = PREFIX + "mode";
  public static final String PLAYERS = PREFIX + "players";
  public static final String PASSIVE = PREFIX + "passive";
  public static final String HOSTILE = PREFIX + "hostile";
  public static final String ITEMS = PREFIX + "items";

  private PressurePlateScreenTexts() {}

  public static String of(PlateMode mode) {
    return switch (mode) {
      case PLAYERS -> PLAYERS;
      case PASSIVE -> PASSIVE;
      case HOSTILE -> HOSTILE;
      case ITEMS -> ITEMS;
    };
  }

  /** The state and the mode of the label, as `On · Players` (answer 609). */
  public static String stateOf(boolean on, PlateMode mode) {
    return (on ? ON_PREFIX : OFF_PREFIX) + mode.key();
  }
}
