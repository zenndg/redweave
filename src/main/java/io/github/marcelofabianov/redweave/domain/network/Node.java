package io.github.marcelofabianov.redweave.domain.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A node of a network. It is a value: a change gives a new node, which the network keeps in place
 * of the old one (answer 33). An empty slot is a free slot, and an empty name is the default one
 * (answer 206).
 */
public record Node(
    NodeId id, Optional<String> name, NodePosition position, List<Optional<ChannelSlot>> slots) {
  private static final String MISSING = "A node needs an ID, a name, even if empty, and a position";

  public Node {
    Objects.requireNonNull(id, MISSING);
    Objects.requireNonNull(name, MISSING);
    Objects.requireNonNull(position, MISSING);
    slots = List.copyOf(slots);
  }

  static Node withFreeSlots(NodeId id, NodePosition position, int slotCount) {
    return new Node(
        id, Optional.empty(), position, Collections.nCopies(slotCount, Optional.empty()));
  }

  /** The name the screens show: its own, or the start of its ID (answers 142 and 206). */
  public String displayName() {
    return name.orElseGet(() -> DefaultNames.of(id.value()));
  }

  boolean hasSlot(int index) {
    return index >= 0 && index < slots.size();
  }

  /** The slot at an index, or none when it is free or outside the node. */
  Optional<ChannelSlot> slotAt(int index) {
    return hasSlot(index) ? slots.get(index) : Optional.empty();
  }

  Node withName(Optional<String> newName) {
    return new Node(id, newName, position, slots);
  }

  Node withSlot(int index, Optional<ChannelSlot> slot) {
    List<Optional<ChannelSlot>> changed = new ArrayList<>(slots);
    changed.set(index, slot);
    return new Node(id, name, position, changed);
  }
}
