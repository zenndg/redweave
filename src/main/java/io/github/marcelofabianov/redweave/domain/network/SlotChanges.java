package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Optional;

/**
 * What enabling, disabling and naming do to one slot of a node (answers 226 to 228). A slot never
 * enabled becomes an input on the face the node sticks to; a disabled one keeps its direction, face
 * and name. None means the change does not apply.
 */
final class SlotChanges {
  private SlotChanges() {}

  static Optional<ChannelSlot> enable(Node node, int index) {
    return Optional.of(node.slotAt(index).orElseGet(() -> firstInput(node, index)).enabled(true));
  }

  static Optional<ChannelSlot> disable(Node node, int index) {
    return node.slotAt(index).map(slot -> slot.enabled(false));
  }

  /** A new direction and face on a slot that stays disabled, keeping its name (answer 237). */
  static Optional<ChannelSlot> setUp(Node node, int index, Direction direction, Face face) {
    Optional<String> name = node.slotAt(index).flatMap(ChannelSlot::name);
    ChannelColor channel = ChannelColor.values()[index];
    return Optional.of(new ChannelSlot(direction, face, channel, false, name));
  }

  /** A name on a slot never enabled keeps it disabled, waiting for the owner. */
  static Optional<ChannelSlot> name(Node node, int index, Optional<String> name) {
    ChannelSlot slot = node.slotAt(index).orElseGet(() -> firstInput(node, index).enabled(false));
    return Optional.of(slot.named(name));
  }

  /**
   * The name a slot gets: none for a blank text, the text for a valid one, and nothing at all, so a
   * refusal, for a name past the limit.
   */
  static Optional<Optional<String>> validName(String name, int maxLength) {
    if (name.isBlank()) {
      return Optional.of(Optional.empty());
    }
    return Names.valid(name, maxLength).map(Optional::of);
  }

  private static ChannelSlot firstInput(Node node, int index) {
    return new ChannelSlot(Direction.INPUT, node.position().face(), ChannelColor.values()[index]);
  }
}
