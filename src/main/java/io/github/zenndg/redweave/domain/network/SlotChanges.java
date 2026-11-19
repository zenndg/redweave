package io.github.zenndg.redweave.domain.network;

import io.github.zenndg.redweave.domain.signal.ChannelColor;
import java.util.Optional;

/**
 * What enabling, disabling and setting up do to one slot of a node (answers 226, 228 and 237). A
 * slot never enabled becomes an input on the face the node sticks to; a disabled one keeps its
 * direction and face. None means the change does not apply.
 */
final class SlotChanges {
  private SlotChanges() {}

  static Optional<ChannelSlot> enable(Node node, int index) {
    return Optional.of(node.slotAt(index).orElseGet(() -> firstInput(node, index)).enabled(true));
  }

  static Optional<ChannelSlot> disable(Node node, int index) {
    return node.slotAt(index).map(slot -> slot.enabled(false));
  }

  /** A new direction and face on a slot that stays disabled (answer 237). */
  static Optional<ChannelSlot> setUp(int index, Direction direction, Face face) {
    ChannelColor channel = ChannelColor.values()[index];
    return Optional.of(new ChannelSlot(direction, face, channel, false));
  }

  private static ChannelSlot firstInput(Node node, int index) {
    return new ChannelSlot(Direction.INPUT, node.position().face(), ChannelColor.values()[index]);
  }
}
