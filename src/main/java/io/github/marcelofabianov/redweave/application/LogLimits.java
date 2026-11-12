package io.github.marcelofabianov.redweave.application;

/** How many lines the log of each network keeps, from the config, {@code log.size} (147). */
public interface LogLimits {
  int logSize();
}
