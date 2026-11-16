package io.github.marcelofabianov.redweave.config;

/**
 * The numbers of the Signal Item Sensor, read from the server config every time: the most items it
 * may expect and how often it reads its container (answers 693 and 695, rule 3).
 */
public final class NeoForgeItemSensorLimits {
  private NeoForgeItemSensorLimits() {}

  public static int maxCount() {
    return RedweaveConfig.ITEM_SENSOR_MAX_COUNT.get();
  }

  public static int checkTicks() {
    return RedweaveConfig.ITEM_SENSOR_CHECK_TICKS.get();
  }
}
