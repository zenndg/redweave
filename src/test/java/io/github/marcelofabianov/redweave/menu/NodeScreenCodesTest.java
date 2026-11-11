package io.github.marcelofabianov.redweave.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * The Node Screen talks to the server in numbers: the data slots of the menu and the button clicks
 * of the game. Every slot and every action must come back exactly as it went.
 */
class NodeScreenCodesTest {
  private static final int LAST_SLOT = 8;
  private static final int NEGATIVE_USE = -4;

  /** Past the 4 bits of a slot index, as a player with many networks (answer 145). */
  private static final int LAST_CHOICE = 99;

  /** Used, input, face 7, white. */
  private static final int SEVENTH_FACE = 0b11101;

  private static final ChannelSlot RED_OUTPUT =
      new ChannelSlot(Direction.OUTPUT, Face.EAST, ChannelColor.RED);

  /** The data slots of the game go to the client as 16-bit numbers. */
  private static final int SHORT_LIMIT = Short.MAX_VALUE;

  @Test
  void everySlotComesBackAsItWent() {
    for (Direction direction : Direction.values()) {
      for (Face face : Face.values()) {
        for (ChannelColor channel : ChannelColor.values()) {
          Optional<ChannelSlot> slot = Optional.of(new ChannelSlot(direction, face, channel));
          int code = NodeScreenCodes.slot(slot);
          assertEquals(slot, NodeScreenCodes.slotOf(code));
          assertEquals(true, code > 0 && code <= SHORT_LIMIT);
        }
      }
    }
  }

  @Test
  void aFreeSlotIsZero() {
    assertEquals(0, NodeScreenCodes.slot(Optional.empty()));
    assertEquals(Optional.empty(), NodeScreenCodes.slotOf(0));
  }

  @Test
  void everyActionComesBackAsItWent() {
    NodeAction configure = NodeAction.configure(LAST_SLOT, RED_OUTPUT);
    NodeAction use = NodeAction.enable(LAST_SLOT);
    NodeAction clear = NodeAction.disable(0);
    NodeAction choose = NodeAction.choose(LAST_CHOICE);

    assertEquals(
        Optional.of(configure), NodeScreenCodes.actionOf(NodeScreenCodes.button(configure)));
    assertEquals(Optional.of(use), NodeScreenCodes.actionOf(NodeScreenCodes.button(use)));
    assertEquals(Optional.of(clear), NodeScreenCodes.actionOf(NodeScreenCodes.button(clear)));
    assertEquals(Optional.of(choose), NodeScreenCodes.actionOf(NodeScreenCodes.button(choose)));
  }

  /**
   * A negative button id sent by a modified client is refused, not trusted (rule 16). A choice past
   * the list is decoded, and the menu refuses it, because only it knows the list.
   */
  @Test
  void aNegativeButtonIsRefused() {
    assertEquals(Optional.empty(), NodeScreenCodes.actionOf(-1));
    assertEquals(Optional.empty(), NodeScreenCodes.actionOf(Integer.MIN_VALUE));
    assertEquals(Optional.empty(), NodeScreenCodes.actionOf(NEGATIVE_USE));
  }

  /** Only configure carries a slot, so a use or a clear with slot bits is refused. */
  @Test
  void anActionWithBitsItDoesNotUseIsRefused() {
    int useWithASlot = NodeScreenCodes.button(NodeAction.configure(LAST_SLOT, RED_OUTPUT)) - 1;

    assertEquals(Optional.empty(), NodeScreenCodes.actionOf(useWithASlot));
  }

  /** Only 6 faces exist, so a seventh sent by a client is refused. */
  @Test
  void aSlotWithAFaceThatDoesNotExistIsRefused() {
    assertEquals(Optional.empty(), NodeScreenCodes.slotOf(SEVENTH_FACE));
  }
}
