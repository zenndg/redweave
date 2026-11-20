package io.github.zenndg.redweave.domain.signal;

import static io.github.zenndg.redweave.domain.signal.TestDelayLimits.STARTING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** The output delay of a module: whole seconds, from 0 to the longest of the config (830, 837). */
class OutputDelayTest {
  private static final int SECOND = OutputDelay.STEP_TICKS;
  private static final int THREE = 3;
  private static final int THREE_SECONDS = THREE * SECOND;
  private static final int FIFTEEN_SECONDS = 15 * SECOND;
  private static final int HALF_SECOND = SECOND / 2;
  private static final int NEGATIVE = -1;
  private static final int UP = 1;
  private static final int DOWN = -1;
  private static final TestDelayLimits SHORTER = new TestDelayLimits(THREE_SECONDS);
  private static final TestDelayLimits OFF = new TestDelayLimits(0);
  private static final TestDelayLimits UNEVEN = new TestDelayLimits(THREE_SECONDS + HALF_SECOND);

  @Test
  void noDelayIsZeroTicks() {
    assertEquals(0, OutputDelay.NONE.ticks());
    assertEquals(0, OutputDelay.NONE.seconds());
  }

  @Test
  void aDelayKeepsWholeSeconds() {
    OutputDelay delay = new OutputDelay(THREE_SECONDS);

    assertEquals(THREE_SECONDS, delay.ticks());
    assertEquals(THREE, delay.seconds());
  }

  @Test
  void aNegativeDelayIsRefused() {
    assertThrows(IllegalArgumentException.class, () -> new OutputDelay(NEGATIVE));
  }

  @Test
  void aDelayOutOfWholeSecondsIsRefused() {
    assertThrows(IllegalArgumentException.class, () -> new OutputDelay(HALF_SECOND));
  }

  @Test
  void aStepUpAddsOneSecond() {
    OutputDelay next = OutputDelay.NONE.stepped(UP, STARTING);

    assertEquals(SECOND, next.ticks());
  }

  @Test
  void aStepDownBelowZeroStaysAtZero() {
    assertEquals(OutputDelay.NONE, OutputDelay.NONE.stepped(DOWN, STARTING));
  }

  @Test
  void aStepUpPastTheLongestStaysAtTheLongest() {
    OutputDelay longest = new OutputDelay(FIFTEEN_SECONDS);

    assertEquals(longest, longest.stepped(UP, STARTING));
  }

  @Test
  void withinClampsToTheLimitsAndWholeSeconds() {
    assertEquals(new OutputDelay(THREE_SECONDS), OutputDelay.within(FIFTEEN_SECONDS, SHORTER));
    assertEquals(OutputDelay.NONE, OutputDelay.within(NEGATIVE, STARTING));
    assertEquals(new OutputDelay(SECOND), OutputDelay.within(SECOND + HALF_SECOND, STARTING));
  }

  @Test
  void anUnevenLongestOfTheConfigGoesDownToAWholeSecond() {
    assertEquals(new OutputDelay(THREE_SECONDS), OutputDelay.within(FIFTEEN_SECONDS, UNEVEN));
  }

  @Test
  void aLongestOfZeroTurnsTheDelayOff() {
    assertEquals(OutputDelay.NONE, OutputDelay.within(FIFTEEN_SECONDS, OFF));
    assertEquals(OutputDelay.NONE, OutputDelay.NONE.stepped(UP, OFF));
  }

  @Test
  void aNegativeLongestOfTheConfigCountsAsZero() {
    assertEquals(OutputDelay.NONE, OutputDelay.within(SECOND, new TestDelayLimits(NEGATIVE)));
  }

  @Test
  void aSavedDelayAboveTheLimitIsFittedDown() {
    OutputDelay saved = new OutputDelay(FIFTEEN_SECONDS);

    assertFalse(saved.fits(SHORTER));
    assertTrue(saved.fits(STARTING));
    assertEquals(new OutputDelay(THREE_SECONDS), saved.fittedTo(SHORTER));
  }
}
