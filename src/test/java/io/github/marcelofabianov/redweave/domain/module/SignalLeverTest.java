package io.github.marcelofabianov.redweave.domain.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import org.junit.jupiter.api.Test;

/**
 * The rule of the Signal Lever: off and at 15 when placed, on and off by the player, and the
 * strength it gives only while on, from 1 to 15 (answers 552, 576 and 579).
 */
class SignalLeverTest {
  private static final int SEVEN = 7;
  private static final int TOO_STRONG = 20;
  private static final int NEGATIVE = -3;

  @Test
  void aPlacedLeverIsOffAtTheStrongestSignal() {
    SignalLever placed = SignalLever.placed();

    assertFalse(placed.on());
    assertEquals(Signal.MAX, placed.strength());
    assertEquals(Signal.OFF, placed.output());
  }

  @Test
  void theToggleTurnsItOnAndOffAgain() {
    SignalLever on = SignalLever.placed().toggle();

    assertTrue(on.on());
    assertEquals(Signal.MAX, on.output());
    assertFalse(on.toggle().on());
  }

  @Test
  void whileOnItGivesItsStrength() {
    SignalLever on = SignalLever.placed().withStrength(SEVEN).toggle();

    assertEquals(new Signal(SEVEN), on.output());
  }

  @Test
  void theStrengthStaysFromOneToFifteen() {
    SignalLever lever = SignalLever.placed();

    assertEquals(Signal.MAX, lever.withStrength(TOO_STRONG).strength());
    assertEquals(new Signal(SignalLever.WEAKEST), lever.withStrength(0).strength());
    assertEquals(new Signal(SignalLever.WEAKEST), lever.withStrength(NEGATIVE).strength());
  }

  @Test
  void theStrengthKeepsTheState() {
    SignalLever on = SignalLever.placed().toggle().withStrength(SEVEN);

    assertTrue(on.on());
  }

  @Test
  void aLeverNeverHoldsANoSignalStrength() {
    assertThrows(IllegalArgumentException.class, () -> new SignalLever(true, Signal.OFF));
    assertThrows(NullPointerException.class, () -> new SignalLever(true, null));
  }
}
