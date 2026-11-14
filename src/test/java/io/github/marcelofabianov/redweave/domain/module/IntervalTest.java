package io.github.marcelofabianov.redweave.domain.module;

import static io.github.marcelofabianov.redweave.domain.module.TestTimerLimits.STARTING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * The interval of a module that ticks on its own, as the Signal Timer and the Signal Randomizer:
 * whole ticks, clamped to the limits of the config instead of refused, and never below the shortest
 * valid one (answers 384, 425, 477 and 490).
 */
class IntervalTest {
  private static final int ONE_SECOND = 20;
  private static final int SHORTEST = 4;
  private static final int FIVE_MINUTES = 6000;
  private static final int TEN_MINUTES = 12000;
  private static final int ONE_TICK = 1;
  private static final int THREE_TICKS = 3;
  private static final int RAISED_MINIMUM = 100;
  private static final int BELOW_SHORTEST = 1;
  private static final IntervalLimits RAISED = new TestTimerLimits(RAISED_MINIMUM, FIVE_MINUTES);
  private static final IntervalLimits TOO_LOW = new TestTimerLimits(BELOW_SHORTEST, BELOW_SHORTEST);

  @Test
  void theStepIsATenthOfASecond() {
    assertEquals(2, Interval.STEP_TICKS);
    assertEquals(SHORTEST, Interval.SHORTEST_TICKS);
  }

  @Test
  void anIntervalWithinTheLimitsStays() {
    assertEquals(new Interval(ONE_SECOND), Interval.within(ONE_SECOND, STARTING));
  }

  @Test
  void anIntervalOutsideTheLimitsIsClamped() {
    assertEquals(new Interval(SHORTEST), Interval.within(ONE_TICK, STARTING));
    assertEquals(new Interval(FIVE_MINUTES), Interval.within(TEN_MINUTES, STARTING));
  }

  @Test
  void theLimitsNeverGoBelowTheShortestInterval() {
    assertEquals(new Interval(SHORTEST), Interval.within(ONE_SECOND, TOO_LOW));
  }

  @Test
  void aSavedIntervalFitsTheLimitsOfNow() {
    Interval saved = new Interval(ONE_SECOND);

    assertTrue(saved.fits(STARTING));
    assertFalse(saved.fits(RAISED));
    assertEquals(new Interval(RAISED_MINIMUM), saved.fittedTo(RAISED));
  }

  @Test
  void anIntervalShorterThanTheShortestIsRefused() {
    assertThrows(IllegalArgumentException.class, () -> new Interval(THREE_TICKS));
  }
}
