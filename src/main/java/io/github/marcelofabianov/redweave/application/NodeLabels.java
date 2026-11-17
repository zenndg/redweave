package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.DefaultNames;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.Node;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import java.util.Optional;

/**
 * Builds the label of a node from its network, or from nothing when it has none (answers 274 and
 * 275), with the color of its first enabled slot (772). It only reads the network, and the world
 * decides which nodes are near the player (268).
 */
public final class NodeLabels {
  private NodeLabels() {}

  public static NodeLabel of(NodeId id, Optional<Network> network) {
    Optional<Node> node = network.flatMap(found -> found.node(id));
    if (network.isEmpty() || node.isEmpty()) {
      return new NodeLabel(DefaultNames.of(id.value()), Optional.empty(), Optional.empty());
    }
    return new NodeLabel(
        node.get().displayName(),
        Optional.of(new NodeLabel.NetworkPart(network.get().name(), network.get().color())),
        node.get().enabledChannels().stream().findFirst());
  }
}
