package io.github.marcelofabianov.redweave.domain.module;

import static io.github.marcelofabianov.redweave.domain.module.TestButtonLimits.STARTING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import org.junit.jupiter.api.Test;

/**
 * The rule of the Signal Button: off, at 15 and with 1 s of on time when placed; a press turns it
 * on for its on time, a second press does nothing, and it turns off on its own (answers 552, 556,
 * 590 and 591).
 */
class SignalButtonTest {
  private static final int SEVEN = 7;
  private static final int TOO_STRONG = 20;
  private static final int ONE_SECOND = 20;
  private static final int SHORT = 4;
  private static final int LONGER = 40;
  private static final int TEN_MINUTES = 12000;
  private static final int RAISED_MINIMUM = 100;
  private static final ButtonLimits RAISED = new TestButtonLimits(RAISED_MINIMUM, TEN_MINUTES);

  @Test
  void aPlacedButtonIsOffAtTheStrongestSignalForOneSecond() {
    SignalButton placed = SignalButton.placed(STARTING);

    assertFalse(placed.on());
    assertEquals(Signal.MAX, placed.strength());
    assertEquals(new ButtonTime(ONE_SECOND), placed.time());
    assertEquals(0, placed.remaining());
    assertEquals(Signal.OFF, placed.output());
  }

  @Test
  void aPressTurnsItOnForItsWholeOnTime() {
    SignalButton pressed = SignalButton.placed(STARTING).press();

    assertTrue(pressed.on());
    assertEquals(ONE_SECOND, pressed.remaining());
    assertEquals(Signal.MAX, pressed.output());
  }

  @Test
  void aPressWhileOnDoesNotStartTheTimeOver() {
    SignalButton ticked = SignalButton.placed(STARTING).press().tick();

    assertSame(ticked, ticked.press());
  }

  @Test
  void itTurnsOffOnItsOwnAfterItsOnTime() {
    SignalButton button = SignalButton.placed(STARTING).withTime(SHORT, STARTING).press();
    for (int tick = 1; tick < SHORT; tick++) {
      button = button.tick();
      assertTrue(button.on());
    }

    assertFalse(button.tick().on());
    assertEquals(0, button.tick().remaining());
  }

  @Test
  void anIdleButtonStaysOff() {
    SignalButton placed = SignalButton.placed(STARTING);

    assertSame(placed, placed.tick());
  }

  @Test
  void theStrengthStaysFromOneToFifteen() {
    SignalButton button = SignalButton.placed(STARTING);

    assertEquals(Signal.MAX, button.withStrength(TOO_STRONG).strength());
    assertEquals(new Signal(SignalButton.WEAKEST), button.withStrength(0).strength());
    assertEquals(new Signal(SEVEN), button.withStrength(SEVEN).press().output());
  }

  @Test
  void anotherOnTimeKeepsThePressUnderWay() {
    SignalButton pressed = SignalButton.placed(STARTING).press().withTime(LONGER, STARTING);

    assertEquals(new ButtonTime(LONGER), pressed.time());
    assertEquals(ONE_SECOND, pressed.remaining());
  }

  @Test
  void aSavedOnTimeFitsTheLimitsOfNow() {
    SignalButton saved = SignalButton.placed(STARTING);

    assertTrue(saved.fits(STARTING));
    assertFalse(saved.fits(RAISED));
    assertEquals(new ButtonTime(RAISED_MINIMUM), saved.fittedTo(RAISED).time());
  }

  @Test
  void anInvalidButtonIsRefused() {
    ButtonTime time = new ButtonTime(ONE_SECOND);

    assertThrows(IllegalArgumentException.class, () -> new SignalButton(true, Signal.OFF, time, 1));
    assertThrows(IllegalArgumentException.class, () -> new SignalButton(true, Signal.MAX, time, 0));
    assertThrows(
        IllegalArgumentException.class, () -> new SignalButton(false, Signal.MAX, time, 1));
    assertThrows(NullPointerException.class, () -> new SignalButton(false, null, time, 0));
    assertThrows(NullPointerException.class, () -> new SignalButton(false, Signal.MAX, null, 0));
  }
}
