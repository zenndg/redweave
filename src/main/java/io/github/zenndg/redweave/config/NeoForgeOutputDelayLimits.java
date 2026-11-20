package io.github.zenndg.redweave.config;

import io.github.zenndg.redweave.domain.signal.OutputDelayLimits;

/** The longest output delay of the modules, read from the server config every time (837). */
public final class NeoForgeOutputDelayLimits implements OutputDelayLimits {
  @Override
  public int maxOutputDelayTicks() {
    return RedweaveConfig.MAX_OUTPUT_DELAY_TICKS.get();
  }
}
