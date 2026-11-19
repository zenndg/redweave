package io.github.zenndg.redweave.domain.network;

import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.domain.signal.OutputDelay;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A node of a network. It is a value: a change gives a new node, which the network keeps in place
 * of the old one (answer 33). It has one slot per channel color, the slot of a color always on that
 * channel; an empty slot was never enabled (answer 224). An empty name is the default one (206).
 * Its output delay is of the whole node, not of a slot (answers 859 and 861).
 */
public record Node(
    NodeId id,
    Optional<String> name,
    NodePosition position,
    List<Optional<ChannelSlot>> slots,
    OutputDelay delay) {
  private static final String MISSING = "A node needs an ID, a name, a position and a delay";
  private static final String NOT_BY_COLOR = "A node has one slot per color, in color order";

  /** One slot per channel color (answer 224). */
  public static final int SLOT_COUNT = ChannelColor.values().length;

  public Node {
    Objects.requireNonNull(id, MISSING);
    Objects.requireNonNull(name, MISSING);
    Objects.requireNonNull(position, MISSING);
    Objects.requireNonNull(delay, MISSING);
    slots = List.copyOf(slots);
    if (!isByColor(slots)) {
      throw new IllegalArgumentException(NOT_BY_COLOR);
    }
  }

  /** A node with no output delay, as a new one (answer 870). */
  public Node(
      NodeId id, Optional<String> name, NodePosition position, List<Optional<ChannelSlot>> slots) {
    this(id, name, position, slots, OutputDelay.NONE);
  }

  private static boolean isByColor(List<Optional<ChannelSlot>> slots) {
    if (slots.size() != SLOT_COUNT) {
      return false;
    }
    for (int index = 0; index < SLOT_COUNT; index++) {
      int at = index;
      if (slots.get(index).filter(slot -> slot.channel().ordinal() != at).isPresent()) {
        return false;
      }
    }
    return true;
  }

  /** The enabled channels, in the order of the colors, for the corners of the plate (229). */
  public List<ChannelColor> enabledChannels() {
    return slots.stream()
        .flatMap(Optional::stream)
        .filter(ChannelSlot::active)
        .map(ChannelSlot::channel)
        .toList();
  }

  /** Whether it reads, emits, both or neither, by its enabled slots only (answer 337). */
  public NodeKind kind() {
    List<Direction> directions =
        slots.stream()
            .flatMap(Optional::stream)
            .filter(ChannelSlot::active)
            .map(ChannelSlot::direction)
            .toList();
    return NodeKind.of(directions.contains(Direction.INPUT), directions.contains(Direction.OUTPUT));
  }

  /** Whether any of its slots emits: enabled and an output (answer 862). */
  public boolean emits() {
    return slots.stream().flatMap(Optional::stream).anyMatch(ChannelSlot::emits);
  }

  static Node withFreeSlots(NodeId id, NodePosition position) {
    return new Node(
        id, Optional.empty(), position, Collections.nCopies(SLOT_COUNT, Optional.empty()));
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
    return new Node(id, newName, position, slots, delay);
  }

  Node withSlot(int index, Optional<ChannelSlot> slot) {
    List<Optional<ChannelSlot>> changed = new ArrayList<>(slots);
    changed.set(index, slot);
    return new Node(id, name, position, changed, delay);
  }

  Node withDelay(OutputDelay next) {
    return new Node(id, name, position, slots, next);
  }
}
