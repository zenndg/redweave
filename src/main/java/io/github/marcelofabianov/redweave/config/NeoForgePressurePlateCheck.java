package io.github.marcelofabianov.redweave.config;

/**
 * How often a Signal Pressure Plate that is on looks again for what stands on it, read from the
 * server config every time (answer 607, rule 3).
 */
public final class NeoForgePressurePlateCheck {
  private NeoForgePressurePlateCheck() {}

  public static int ticks() {
    return RedweaveConfig.PRESSURE_PLATE_CHECK_TICKS.get();
  }
}
