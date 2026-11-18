package io.github.marcelofabianov.redweave.client.screen;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * The slot of the channel open on the Node Screen. With no channel open, as on the network choice,
 * there is no slot to set up: the release of the mouse there once crashed the game (#179).
 */
class OpenChannelTest {
  private static final int NO_CHANNEL = -1;
  private static final int PAST_THE_LAST = ChannelColor.values().length;
  private static final ChannelColor DOOR = ChannelColor.RED;
  private static final ChannelSlot OUTPUT = new ChannelSlot(Direction.OUTPUT, Face.UP, DOOR);
  private static final ChannelSlot NEVER_ENABLED =
      new ChannelSlot(Direction.INPUT, Face.EAST, DOOR, false);

  @Test
  void noChannelOpenHasNoSlotToSetUp() {
    assertEquals(Optional.empty(), OpenChannel.slot(Optional.empty(), NO_CHANNEL, Face.EAST));
    assertEquals(Optional.empty(), OpenChannel.slot(Optional.empty(), PAST_THE_LAST, Face.EAST));
  }

  /** A stray slot read for no channel is not one of the node either (#179). */
  @Test
  void noChannelOpenIgnoresAStraySlot() {
    assertEquals(Optional.empty(), OpenChannel.slot(Optional.of(OUTPUT), NO_CHANNEL, Face.EAST));
  }

  @Test
  void anOpenChannelShowsItsSlot() {
    assertEquals(
        Optional.of(OUTPUT), OpenChannel.slot(Optional.of(OUTPUT), DOOR.ordinal(), Face.EAST));
  }

  /** A channel never enabled shows an input on the face the node sticks to (answer 228). */
  @Test
  void anOpenChannelNeverEnabledShowsAnInputOnTheStuckFace() {
    assertEquals(
        Optional.of(NEVER_ENABLED), OpenChannel.slot(Optional.empty(), DOOR.ordinal(), Face.EAST));
  }
}
