package io.github.zenndg.redweave.config;

import io.github.zenndg.redweave.domain.module.TimerLimits;

/** The limits of the Signal Timer, read from the server config every time (answer 384). */
public final class NeoForgeTimerLimits implements TimerLimits {
  @Override
  public int minIntervalTicks() {
    return RedweaveConfig.MIN_INTERVAL_TICKS.get();
  }

  @Override
  public int maxIntervalTicks() {
    return RedweaveConfig.MAX_INTERVAL_TICKS.get();
  }
}
