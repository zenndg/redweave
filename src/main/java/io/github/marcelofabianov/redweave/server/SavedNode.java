package io.github.marcelofabianov.redweave.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.network.Node;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.NodePosition;
import io.github.marcelofabianov.redweave.domain.network.SlotReading;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntFunction;

/**
 * A node as it is in the saves: the name only when its owner gave one (answer 206) and the slots
 * ever enabled, each in the place of its channel (answer 224).
 */
record SavedNode(NodeId id, Optional<String> name, NodePosition position, List<SavedSlot> slots) {
  private static final String ID = "id";
  private static final String BLOCK = "block";
  private static final String FACE = "face";
  private static final String SLOTS = "slots";
  private static final String SLOT_PART = "slot";
  private static final String NAME_PART = "node name";
  private static final String READING_PART = "reading";
  private static final String REPEATED = "channel repeated on the node";
  private static final String NOT_INPUT = "reading of a slot that is not an input";

  static final Codec<SavedNode> CODEC =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  SaveCodecs.NODE_ID.fieldOf(ID).forGetter(SavedNode::id),
                  Lenient.optionalField(SaveCodecs.NAME, SaveCodecs.SAVED_NAME, NAME_PART)
                      .forGetter(SavedNode::name),
                  SaveCodecs.BLOCK.fieldOf(BLOCK).forGetter(node -> node.position().block()),
                  SaveCodecs.FACE.fieldOf(FACE).forGetter(node -> node.position().face()),
                  Lenient.list(SavedSlot.CODEC, SLOT_PART)
                      .fieldOf(SLOTS)
                      .forGetter(SavedNode::slots))
              .apply(instance, SavedNode::checked));

  SavedNode {
    slots = List.copyOf(slots);
  }

  /** A node of the domain with the reading of each of its slots, to write it. */
  static SavedNode of(Node node, IntFunction<Signal> readingOf) {
    List<SavedSlot> used = new ArrayList<>();
    for (int index = 0; index < node.slots().size(); index++) {
      int at = index;
      node.slots().get(index).ifPresent(slot -> used.add(new SavedSlot(slot, readingOf.apply(at))));
    }
    return new SavedNode(node.id(), node.name(), node.position(), used);
  }

  /** Drops a repeated channel and the readings out of an enabled input (answer 92). */
  private static SavedNode checked(
      NodeId id, Optional<String> name, BlockLocation block, Face face, List<SavedSlot> slots) {
    Set<Integer> taken = new HashSet<>();
    List<SavedSlot> kept = new ArrayList<>();
    for (SavedSlot slot : slots) {
      if (!taken.add(slot.index())) {
        Lenient.dropped(SLOT_PART, REPEATED);
        continue;
      }
      kept.add(withoutStrayReading(slot));
    }
    return new SavedNode(id, name, new NodePosition(block, face), kept);
  }

  private static SavedSlot withoutStrayReading(SavedSlot slot) {
    if (slot.slot().reads() || !slot.reading().isOn()) {
      return slot;
    }
    Lenient.dropped(READING_PART, NOT_INPUT);
    return new SavedSlot(slot.slot(), Signal.OFF);
  }

  Node node() {
    List<Optional<ChannelSlot>> all =
        new ArrayList<>(Collections.nCopies(Node.SLOT_COUNT, Optional.empty()));
    slots.forEach(saved -> all.set(saved.index(), Optional.of(saved.slot())));
    return new Node(id, name, position, all);
  }

  /** The readings that are on, the only ones that go back to the network (answer 89). */
  List<SlotReading> readings() {
    return slots.stream()
        .filter(saved -> saved.reading().isOn())
        .map(saved -> new SlotReading(id, saved.index(), saved.reading()))
        .toList();
  }
}
