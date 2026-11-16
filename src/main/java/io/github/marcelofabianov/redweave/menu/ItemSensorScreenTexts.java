package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.RedweaveMod;

/**
 * The keys of the texts of the Signal Item Sensor Screen, in common code for the data generation
 * (answer 696). `On` and `Off` are those of the Signal Button, masculine in Portuguese as "sensor";
 * `Strength` is that of the Signal Lever and `Output` that of the Node Screen.
 */
public final class ItemSensorScreenTexts {
  private static final String PREFIX = "screen." + RedweaveMod.MOD_ID + ".signal_item_sensor.";

  public static final String ITEM = PREFIX + "item";
  public static final String COUNT = PREFIX + "count";
  public static final String NONE = PREFIX + "none";
  public static final String GHOST_HINT = PREFIX + "ghost_hint";

  private ItemSensorScreenTexts() {}
}
