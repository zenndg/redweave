package io.github.marcelofabianov.redweave.config;

import io.github.marcelofabianov.redweave.domain.signal.DelayLineLimits;

/** The most changes a delay line holds, read from the server config every time (answer 847). */
public final class NeoForgeDelayLineLimits implements DelayLineLimits {
  @Override
  public int maxDelayChanges() {
    return RedweaveConfig.MAX_DELAY_CHANGES.get();
  }
}
