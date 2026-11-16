package io.github.marcelofabianov.redweave.config;

import io.github.marcelofabianov.redweave.domain.module.LockLimits;

/** The limits of the `Toggle` pulse of the Signal Lock, read from the server config (640). */
public final class NeoForgeLockLimits implements LockLimits {
  @Override
  public int minIntervalTicks() {
    return RedweaveConfig.LOCK_MIN_PULSE_TICKS.get();
  }

  @Override
  public int maxIntervalTicks() {
    return RedweaveConfig.LOCK_MAX_PULSE_TICKS.get();
  }
}
