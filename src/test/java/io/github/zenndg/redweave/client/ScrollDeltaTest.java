package io.github.zenndg.redweave.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * The turn of the wheel that switches the Configurator mode. On macOS, Shift turns the vertical
 * wheel into a horizontal one, so the game falls back to it, as the hotbar does (answer 351).
 */
class ScrollDeltaTest {
  private static final double STILL = 0;
  private static final double DOWN = -1;
  private static final double UP = 1;
  private static final double SIDEWAYS = 1;

  @Test
  void theVerticalWheelWinsWhenItTurns() {
    assertEquals(DOWN, ScrollDelta.of(SIDEWAYS, DOWN));
  }

  @Test
  void withShiftOnMacTheHorizontalWheelCountsInverted() {
    assertEquals(DOWN, ScrollDelta.of(UP, STILL));
    assertEquals(UP, ScrollDelta.of(DOWN, STILL));
  }

  @Test
  void noTurnIsStill() {
    assertEquals(STILL, ScrollDelta.of(STILL, STILL));
  }
}
