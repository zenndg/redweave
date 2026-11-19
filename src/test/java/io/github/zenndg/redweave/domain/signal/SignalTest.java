package io.github.zenndg.redweave.domain.signal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SignalTest {
  private static final int WEAKEST = 0;
  private static final int STRONGEST = 15;
  private static final int BELOW_RANGE = -1;
  private static final int ABOVE_RANGE = 16;
  private static final int WEAKEST_ON = 1;
  private static final Signal WEAK = new Signal(3);
  private static final Signal STRONG = new Signal(12);
  private static final int FAR_BELOW = -40;
  private static final int FAR_ABOVE = 99;

  @Test
  void keepsAStrengthInsideTheRedstoneRange() {
    assertEquals(WEAKEST, new Signal(WEAKEST).strength());
    assertEquals(STRONGEST, new Signal(STRONGEST).strength());
  }

  @Test
  void rejectsAStrengthBelowZero() {
    assertThrows(IllegalArgumentException.class, () -> new Signal(BELOW_RANGE));
  }

  @Test
  void rejectsAStrengthAboveFifteen() {
    assertThrows(IllegalArgumentException.class, () -> new Signal(ABOVE_RANGE));
  }

  @Test
  void offIsTheWeakestAndMaxIsTheStrongest() {
    assertEquals(WEAKEST, Signal.OFF.strength());
    assertEquals(STRONGEST, Signal.MAX.strength());
  }

  @Test
  void isOnFromTheFirstStrengthAboveZero() {
    assertFalse(Signal.OFF.isOn());
    assertTrue(new Signal(WEAKEST_ON).isOn());
  }

  @Test
  void theStrongestOfTwoSignalsWinsInAnyOrder() {
    assertEquals(STRONG, WEAK.strongest(STRONG));
    assertEquals(STRONG, STRONG.strongest(WEAK));
  }

  @Test
  void twoEqualSignalsGiveTheSameSignal() {
    assertEquals(STRONG, STRONG.strongest(new Signal(STRONG.strength())));
  }

  @Test
  void clampingKeepsAValueInsideTheRange() {
    assertEquals(STRONG, Signal.clamped(STRONG.strength()));
  }

  @Test
  void clampingTurnsAValueBelowTheRangeIntoOff() {
    assertEquals(Signal.OFF, Signal.clamped(FAR_BELOW));
  }

  @Test
  void clampingTurnsAValueAboveTheRangeIntoMax() {
    assertEquals(Signal.MAX, Signal.clamped(FAR_ABOVE));
  }
}
