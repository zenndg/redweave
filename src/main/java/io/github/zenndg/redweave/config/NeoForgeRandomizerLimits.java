package io.github.zenndg.redweave.config;

import io.github.zenndg.redweave.domain.module.RandomizerLimits;

/** The limits of the Signal Randomizer, read from the server config every time (answer 477). */
public final class NeoForgeRandomizerLimits implements RandomizerLimits {
  @Override
  public int minIntervalTicks() {
    return RedweaveConfig.RANDOMIZER_MIN_INTERVAL_TICKS.get();
  }

  @Override
  public int maxIntervalTicks() {
    return RedweaveConfig.RANDOMIZER_MAX_INTERVAL_TICKS.get();
  }
}
