package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Optional;

/**
 * The slot of the channel open on the Node Screen, apart from the drawing so a test reaches it.
 * With no channel open, as on the network choice, there is no slot: the release of the mouse there
 * once crashed the game (#179).
 */
final class OpenChannel {
  private OpenChannel() {}

  /** The slot of the open channel, or none while the index is no channel of the node. */
  static Optional<ChannelSlot> slot(Optional<ChannelSlot> slot, int index, Face stuck) {
    if (index < 0 || index >= ChannelColor.values().length) {
      return Optional.empty();
    }
    return Optional.of(setUpOf(slot, index, stuck));
  }

  /**
   * The slot as the panel shows it: its own, or, never enabled, an input on the face the node
   * sticks to, as enabling it would make (answer 228).
   */
  static ChannelSlot setUpOf(Optional<ChannelSlot> slot, int index, Face stuck) {
    return slot.orElseGet(
        () -> new ChannelSlot(Direction.INPUT, stuck, ChannelColor.values()[index], false));
  }
}
