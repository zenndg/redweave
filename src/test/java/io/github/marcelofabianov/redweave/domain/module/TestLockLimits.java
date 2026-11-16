package io.github.marcelofabianov.redweave.domain.module;

/** Fixed lock limits for the tests, with the starting values of _docs/limites.md (640). */
public record TestLockLimits(int minIntervalTicks, int maxIntervalTicks) implements LockLimits {
  private static final int MIN_PULSE = 2;
  private static final int MAX_PULSE = 200;

  public static final TestLockLimits STARTING = new TestLockLimits(MIN_PULSE, MAX_PULSE);
}
