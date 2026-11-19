package io.github.zenndg.redweave.domain.module;

import static io.github.zenndg.redweave.domain.module.TestTimerLimits.STARTING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * The cycle of the Signal Timer: every interval, on for the pulse, with the pulse always shorter
 * than the interval, and the interval within the limits of the config (answers 379, 384 and 409).
 */
class TimerCycleTest {
  private static final int ONE_SECOND = 20;
  private static final int HALF_SECOND = 10;
  private static final int SHORTEST_PULSE = 2;
  private static final int SHORTEST_INTERVAL = 4;
  private static final int TWO_SECONDS = 40;
  private static final int FIVE_MINUTES = 6000;
  private static final int TEN_MINUTES = 12000;
  private static final int ONE_TICK = 1;
  private static final int THREE_TICKS = 3;
  private static final int RAISED_MINIMUM = 100;
  private static final int LOWERED_MAXIMUM = 200;
  private static final TimerLimits RAISED = new TestTimerLimits(RAISED_MINIMUM, FIVE_MINUTES);
  private static final TimerLimits LOWERED =
      new TestTimerLimits(SHORTEST_INTERVAL, LOWERED_MAXIMUM);

  @Test
  void aNewTimerTicksEverySecondWithAHalfSecondPulse() {
    assertEquals(new TimerCycle(ONE_SECOND, HALF_SECOND), TimerCycle.initial(STARTING));
  }

  @Test
  void aNewTimerFitsLimitsThatDoNotAllowOneSecond() {
    assertEquals(RAISED_MINIMUM, TimerCycle.initial(RAISED).intervalTicks());
  }

  @Test
  void theShortestCycleIsAFourTickIntervalWithATwoTickPulse() {
    TimerCycle shortest = new TimerCycle(SHORTEST_INTERVAL, SHORTEST_PULSE);

    assertEquals(SHORTEST_PULSE, shortest.pulseTicks());
  }

  @Test
  void rejectsAPulseShorterThanTwoTicks() {
    assertThrows(IllegalArgumentException.class, () -> new TimerCycle(ONE_SECOND, ONE_TICK));
  }

  @Test
  void rejectsAPulseThatLeavesLessThanTwoTicksOff() {
    assertThrows(
        IllegalArgumentException.class, () -> new TimerCycle(ONE_SECOND, ONE_SECOND - ONE_TICK));
  }

  @Test
  void aNewIntervalWithinTheLimitsIsKept() {
    TimerCycle cycle = TimerCycle.initial(STARTING).withInterval(TWO_SECONDS, STARTING);

    assertEquals(new TimerCycle(TWO_SECONDS, HALF_SECOND), cycle);
  }

  @Test
  void aNewIntervalIsClampedToTheLimits() {
    TimerCycle cycle = TimerCycle.initial(STARTING);

    assertEquals(SHORTEST_INTERVAL, cycle.withInterval(ONE_TICK, STARTING).intervalTicks());
    assertEquals(FIVE_MINUTES, cycle.withInterval(TEN_MINUTES, STARTING).intervalTicks());
  }

  @Test
  void aShorterIntervalShortensThePulseToFit() {
    TimerCycle cycle = TimerCycle.initial(STARTING).withInterval(SHORTEST_INTERVAL, STARTING);

    assertEquals(new TimerCycle(SHORTEST_INTERVAL, SHORTEST_PULSE), cycle);
  }

  @Test
  void aNewPulseIsClampedBetweenTwoTicksAndTheIntervalLessTwo() {
    TimerCycle cycle = TimerCycle.initial(STARTING);

    assertEquals(SHORTEST_PULSE, cycle.withPulse(ONE_TICK).pulseTicks());
    assertEquals(ONE_SECOND - SHORTEST_PULSE, cycle.withPulse(ONE_SECOND).pulseTicks());
    assertEquals(THREE_TICKS, cycle.withPulse(THREE_TICKS).pulseTicks());
  }

  @Test
  void aSavedCycleOutsideNewLimitsIsFittedToThem() {
    TimerCycle saved = new TimerCycle(ONE_SECOND, HALF_SECOND);

    assertFalse(saved.fits(RAISED));
    assertEquals(RAISED_MINIMUM, saved.fittedTo(RAISED).intervalTicks());
  }

  @Test
  void aSavedCycleAboveANewMaximumIsShortenedWithItsPulse() {
    TimerCycle saved = new TimerCycle(FIVE_MINUTES, FIVE_MINUTES - SHORTEST_PULSE);

    TimerCycle fitted = saved.fittedTo(LOWERED);

    assertFalse(saved.fits(LOWERED));
    assertEquals(new TimerCycle(LOWERED_MAXIMUM, LOWERED_MAXIMUM - SHORTEST_PULSE), fitted);
  }

  @Test
  void aCycleWithinTheLimitsFitsThem() {
    assertTrue(TimerCycle.initial(STARTING).fits(STARTING));
  }

  @Test
  void limitsBelowTheShortestCycleStillGiveAValidCycle() {
    TimerLimits tooLow = new TestTimerLimits(ONE_TICK, ONE_TICK);

    assertEquals(SHORTEST_INTERVAL, TimerCycle.initial(tooLow).intervalTicks());
  }
}
