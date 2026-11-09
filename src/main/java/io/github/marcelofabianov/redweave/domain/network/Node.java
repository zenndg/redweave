package io.github.marcelofabianov.redweave.domain.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A node of a network. It is a value: a change gives a new node, which the network keeps in place
 * of the old one (answer 33). An empty slot is a free slot.
 */
public record Node(NodeId id, NodePosition position, List<Optional<ChannelSlot>> slots) {
  private static final String MISSING = "A node needs an ID, a position and its slots";

  public Node {
    Objects.requireNonNull(id, MISSING);
    Objects.requireNonNull(position, MISSING);
    slots = List.copyOf(slots);
  }

  static Node withFreeSlots(NodeId id, NodePosition position, int slotCount) {
    return new Node(id, position, Collections.nCopies(slotCount, Optional.empty()));
  }

  boolean hasSlot(int index) {
    return index >= 0 && index < slots.size();
  }

  Node withSlot(int index, Optional<ChannelSlot> slot) {
    List<Optional<ChannelSlot>> changed = new ArrayList<>(slots);
    changed.set(index, slot);
    return new Node(id, position, changed);
  }
}
