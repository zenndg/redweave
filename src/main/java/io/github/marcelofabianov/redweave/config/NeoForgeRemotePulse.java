package io.github.marcelofabianov.redweave.config;

import java.util.function.IntSupplier;

/** How long the button pulse of a Signal Remote lasts, from the server config, read every time. */
public final class NeoForgeRemotePulse implements IntSupplier {
  @Override
  public int getAsInt() {
    return RedweaveConfig.PULSE_TICKS.get();
  }
}
