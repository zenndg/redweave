package io.github.marcelofabianov.redweave.domain.network;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The nodes of a network, in the order they were attached, and the rules of each node: a name kept
 * whole, a blank name back to the default (answers 144 and 206) and a slot within the node. The
 * network checks the owner and what a change does to the signals.
 */
final class Nodes {
  private final Map<NodeId, Node> byId = new LinkedHashMap<>();

  /** A node that moves in keeps the name it had, even past the limit of today (93 and 200). */
  Outcome attach(NodeId id, NodePosition position, Optional<String> name) {
    if (byId.containsKey(id)) {
      return Outcome.ALREADY_ATTACHED;
    }
    if (name.filter(given -> !Names.isKept(given)).isPresent()) {
      return Outcome.INVALID_NAME;
    }
    byId.put(id, Node.withFreeSlots(id, position).withName(name));
    return Outcome.ACCEPTED;
  }

  Outcome rename(NodeId id, String newName, int maxLength) {
    Node node = byId.get(id);
    if (node == null) {
      return Outcome.UNKNOWN_NODE;
    }
    if (newName.isBlank()) {
      byId.put(id, node.withName(Optional.empty()));
      return Outcome.ACCEPTED;
    }
    Optional<String> valid = Names.valid(newName, maxLength);
    if (valid.isEmpty()) {
      return Outcome.INVALID_NAME;
    }
    byId.put(id, node.withName(valid));
    return Outcome.ACCEPTED;
  }

  Outcome changeSlot(NodeId id, int index, Optional<ChannelSlot> slot) {
    Node node = byId.get(id);
    if (node == null) {
      return Outcome.UNKNOWN_NODE;
    }
    if (!node.hasSlot(index)
        || slot.filter(given -> given.channel().ordinal() != index).isPresent()) {
      return Outcome.INVALID_SLOT;
    }
    byId.put(id, node.withSlot(index, slot));
    return Outcome.ACCEPTED;
  }

  /** Whether the node is in the network and has a slot at the index. */
  Outcome slotOf(NodeId id, int index) {
    Node node = byId.get(id);
    if (node == null) {
      return Outcome.UNKNOWN_NODE;
    }
    return node.hasSlot(index) ? Outcome.ACCEPTED : Outcome.INVALID_SLOT;
  }

  boolean remove(NodeId id) {
    return byId.remove(id) != null;
  }

  /** Puts back a node from the saves, already checked by the restore. */
  void restore(Node node) {
    byId.put(node.id(), node);
  }

  boolean isInput(NodeId id, int index) {
    Node node = byId.get(id);
    return node != null && node.slotAt(index).filter(ChannelSlot::reads).isPresent();
  }

  /** The node, or null when it is not in the network, like Map.get, for the paths of the tick. */
  Node at(NodeId id) {
    return byId.get(id);
  }

  Collection<Node> all() {
    return byId.values();
  }
}
