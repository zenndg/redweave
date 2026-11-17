package io.github.marcelofabianov.redweave.domain.module;

/** A fixed longest output delay for the tests, the starting value of _docs/limites.md (837). */
record TestDelayLimits(int maxOutputDelayTicks) implements OutputDelayLimits {
  private static final int FIFTEEN_SECONDS = 300;

  static final TestDelayLimits STARTING = new TestDelayLimits(FIFTEEN_SECONDS);
}
