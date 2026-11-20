package io.github.zenndg.redweave.domain.network;

import java.util.Optional;
import java.util.function.BiFunction;

/**
 * Inside the network: how a slot of a node changes. The network checks the owner first, and here
 * the slot changes, its last reading goes and the network waits for the next tick. Left the Network
 * like SlotChanges, so the aggregate keeps one responsibility per part.
 */
final class SlotEdits {
  private final Nodes nodes;
  private final Readings readings;
  private final Emissions emissions;

  SlotEdits(Nodes nodes, Readings readings, Emissions emissions) {
    this.nodes = nodes;
    this.readings = readings;
    this.emissions = emissions;
  }

  /** Changes a slot by a rule of SlotChanges, which may refuse it, as an invalid slot. */
  Outcome changeWith(
      NodeId nodeId, int index, BiFunction<Node, Integer, Optional<ChannelSlot>> change) {
    Outcome found = nodes.slotOf(nodeId, index);
    if (found != Outcome.ACCEPTED) {
      return found;
    }
    Optional<ChannelSlot> changed = change.apply(nodes.at(nodeId), index);
    return changed.isEmpty() ? Outcome.INVALID_SLOT : change(nodeId, index, changed);
  }

  /** Sets up a slot that is not enabled; an enabled one is locked (answer 237). */
  Outcome setUp(NodeId nodeId, int index, Direction way, Face face) {
    if (nodes.isEnabled(nodeId, index)) {
      return Outcome.SLOT_ENABLED;
    }
    return changeWith(nodeId, index, (node, at) -> SlotChanges.setUp(at, way, face));
  }

  /** Puts a slot, or frees it with empty, forgetting the last reading of the old one. */
  Outcome change(NodeId nodeId, int index, Optional<ChannelSlot> slot) {
    Outcome changed = nodes.changeSlot(nodeId, index, slot);
    if (changed != Outcome.ACCEPTED) {
      return changed;
    }
    readings.forget(new SlotRef(nodeId, index));
    emissions.markPending();
    return Outcome.ACCEPTED;
  }
}
