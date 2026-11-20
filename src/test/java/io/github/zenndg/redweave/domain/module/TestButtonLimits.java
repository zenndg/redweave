package io.github.zenndg.redweave.domain.module;

/** Fixed button limits for the tests, with the starting values of _docs/limites.md (592). */
public record TestButtonLimits(int minIntervalTicks, int maxIntervalTicks) implements ButtonLimits {
  private static final int MIN_TIME = 2;
  private static final int MAX_TIME = 6000;

  public static final TestButtonLimits STARTING = new TestButtonLimits(MIN_TIME, MAX_TIME);
}
