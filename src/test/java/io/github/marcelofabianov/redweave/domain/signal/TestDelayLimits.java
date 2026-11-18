package io.github.marcelofabianov.redweave.domain.signal;

/**
 * Fixed delay limits for the tests, with the starting values of _docs/limites.md: the longest
 * output delay and the most changes a delay line holds (answers 837 and 847).
 */
public record TestDelayLimits(int maxOutputDelayTicks, int maxDelayChanges)
    implements OutputDelayLimits, DelayLineLimits {
  private static final int FIFTEEN_SECONDS = 300;
  private static final int CHANGES = 32;

  public static final TestDelayLimits STARTING = new TestDelayLimits(FIFTEEN_SECONDS, CHANGES);

  public TestDelayLimits(int maxOutputDelayTicks) {
    this(maxOutputDelayTicks, CHANGES);
  }
}
