package io.github.zenndg.redweave.config;

import io.github.zenndg.redweave.domain.module.InverterLimits;

/** The burnout of the Signal Inverter, read from the server config every time (answer 449). */
public final class NeoForgeInverterLimits implements InverterLimits {
  @Override
  public int burnoutToggles() {
    return RedweaveConfig.BURNOUT_TOGGLES.get();
  }

  @Override
  public int burnoutWindowTicks() {
    return RedweaveConfig.BURNOUT_WINDOW_TICKS.get();
  }

  @Override
  public int burnoutTicks() {
    return RedweaveConfig.BURNOUT_TICKS.get();
  }
}
