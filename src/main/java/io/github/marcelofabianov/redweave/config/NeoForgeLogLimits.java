package io.github.marcelofabianov.redweave.config;

import io.github.marcelofabianov.redweave.application.LogLimits;

/** The size of the log of each network, read from the server config when a log starts (147). */
public final class NeoForgeLogLimits implements LogLimits {
  @Override
  public int logSize() {
    return RedweaveConfig.LOG_SIZE.get();
  }
}
