package io.github.marcelofabianov.redweave.config;

import io.github.marcelofabianov.redweave.domain.module.ButtonLimits;

/** The limits of the on time of the Signal Button, read from the server config every time (592). */
public final class NeoForgeButtonLimits implements ButtonLimits {
  @Override
  public int minIntervalTicks() {
    return RedweaveConfig.BUTTON_MIN_INTERVAL_TICKS.get();
  }

  @Override
  public int maxIntervalTicks() {
    return RedweaveConfig.BUTTON_MAX_INTERVAL_TICKS.get();
  }
}
