package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.Node;
import io.github.zenndg.redweave.domain.network.NodeId;
import java.util.Optional;

/**
 * What a node should emit now, for its part to delay: the strongest output of its network and its
 * delay (answers 71 and 860). A node with no network, that does not count or with no enabled output
 * emits nothing, so its part cuts the output at once (answer 862).
 */
public final class EmitNode {
  private final NodeNetworkFinder finder;

  public EmitNode(NodeNetworkFinder finder) {
    this.finder = finder;
  }

  public Emission of(NodeId nodeId) {
    Optional<Network> network = finder.findByNode(nodeId);
    Optional<Node> node = network.flatMap(found -> found.node(nodeId));
    if (node.isEmpty()) {
      return Emission.NONE;
    }
    if (!emits(network.get(), node.get())) {
      return new Emission(Optional.empty(), node.get().delay());
    }
    return new Emission(Optional.of(network.get().emission(nodeId)), node.get().delay());
  }

  /** Whether the node does not count or has no enabled output, without working out a signal. */
  public boolean isSilent(NodeId nodeId) {
    Optional<Network> network = finder.findByNode(nodeId);
    return network
        .flatMap(found -> found.node(nodeId))
        .map(node -> !emits(network.get(), node))
        .orElse(true);
  }

  private static boolean emits(Network network, Node node) {
    return network.counts(node.id()) && node.emits();
  }
}
