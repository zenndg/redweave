package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.Node;
import io.github.marcelofabianov.redweave.domain.network.NodeCopy;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.Pasted;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The Configurator pastes a copy on a node: every slot of the node is replaced, through the slot
 * operations of the network, so the owner is checked and the readings are dropped (answer 106). The
 * node stays in its own network.
 */
public final class PasteNode {
  private final NodeNetworkFinder finder;
  private final NetworkSaver saver;

  public PasteNode(NodeNetworkFinder finder, NetworkSaver saver) {
    this.finder = finder;
    this.saver = saver;
  }

  public PasteResult paste(PlayerId player, NodeId nodeId, NodeCopy copy) {
    Optional<Network> network = finder.findByNode(nodeId);
    Optional<Node> node = network.flatMap(found -> found.node(nodeId));
    if (node.isEmpty()) {
      return PasteResult.refused(Outcome.UNKNOWN_NODE);
    }
    if (!network.get().isOwner(player)) {
      return PasteResult.refused(Outcome.NOT_OWNER);
    }
    Pasted pasted = copy.onto(node.get());
    for (int index = 0; index < pasted.slots().size(); index++) {
      int at = index;
      pasted
          .slots()
          .get(index)
          .ifPresentOrElse(
              slot -> network.get().configureSlot(player, nodeId, at, slot),
              () -> network.get().clearSlot(player, nodeId, at));
    }
    saver.save(network.get());
    return new PasteResult(Outcome.ACCEPTED, pasted.inUse());
  }
}
