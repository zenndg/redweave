package io.github.marcelofabianov.redweave.domain.cable;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * Cables join only with the same color, so cables of different colors run side by side, and only
 * with the same owner, so nobody feeds the cable of another player (answers 497, 501 and 528).
 */
class InsulationTest {
  private static final PlayerId OWNER = new PlayerId(new UUID(0, 1));
  private static final PlayerId STRANGER = new PlayerId(new UUID(0, 2));
  private static final Insulation RED = new Insulation(ChannelColor.RED, OWNER);

  @Test
  void theSameColorAndOwnerJoin() {
    assertTrue(RED.joins(new Insulation(ChannelColor.RED, OWNER)));
  }

  @Test
  void anotherColorDoesNotJoin() {
    assertFalse(RED.joins(new Insulation(ChannelColor.BLUE, OWNER)));
  }

  @Test
  void anotherOwnerDoesNotJoin() {
    assertFalse(RED.joins(new Insulation(ChannelColor.RED, STRANGER)));
  }

  @Test
  void anInsulationNeedsItsColorAndOwner() {
    assertThrows(NullPointerException.class, () -> new Insulation(null, OWNER));
    assertThrows(NullPointerException.class, () -> new Insulation(ChannelColor.RED, null));
  }
}
