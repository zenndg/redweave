package io.github.zenndg.redweave.domain.module;

import static io.github.zenndg.redweave.domain.module.TestButtonLimits.STARTING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * The on time of the Signal Button: whole ticks, clamped to the limits of the config, with a floor
 * of 2 ticks instead of the 4 of a timer (answers 490, 556 and 592).
 */
class ButtonTimeTest {
  private static final int SHORTEST = 2;
  private static final int ONE_SECOND = 20;
  private static final int FIVE_MINUTES = 6000;
  private static final int TEN_MINUTES = 12000;
  private static final int RAISED_MINIMUM = 100;
  private static final ButtonLimits RAISED = new TestButtonLimits(RAISED_MINIMUM, FIVE_MINUTES);
  private static final ButtonLimits TOO_LOW = new TestButtonLimits(0, 0);

  @Test
  void theShortestOnTimeIsOneStep() {
    assertEquals(SHORTEST, ButtonTime.SHORTEST_TICKS);
    assertEquals(new ButtonTime(SHORTEST), ButtonTime.within(0, STARTING));
  }

  @Test
  void anOnTimeOutsideTheLimitsIsClamped() {
    assertEquals(new ButtonTime(ONE_SECOND), ButtonTime.within(ONE_SECOND, STARTING));
    assertEquals(new ButtonTime(FIVE_MINUTES), ButtonTime.within(TEN_MINUTES, STARTING));
  }

  @Test
  void theLimitsNeverGoBelowTheFloor() {
    assertEquals(new ButtonTime(SHORTEST), ButtonTime.within(ONE_SECOND, TOO_LOW));
  }

  @Test
  void aSavedOnTimeFitsTheLimitsOfNow() {
    ButtonTime saved = new ButtonTime(ONE_SECOND);

    assertTrue(saved.fits(STARTING));
    assertFalse(saved.fits(RAISED));
    assertEquals(new ButtonTime(RAISED_MINIMUM), saved.fittedTo(RAISED));
  }

  @Test
  void anOnTimeBelowTheFloorIsRefused() {
    assertThrows(IllegalArgumentException.class, () -> new ButtonTime(1));
  }
}
