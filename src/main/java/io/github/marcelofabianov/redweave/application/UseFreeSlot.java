package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.Node;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;

/**
 * "Click to use" on a free slot of the Node Screen: an input on the face the node sticks to, on the
 * first free channel, as a node that was just placed (answers 25 and 83).
 */
public final class UseFreeSlot {
  private final NodeNetworkFinder finder;
  private final NetworkSaver saver;

  public UseFreeSlot(NodeNetworkFinder finder, NetworkSaver saver) {
    this.finder = finder;
    this.saver = saver;
  }

  public Outcome use(PlayerId player, NodeId node, int index) {
    return SavedChange.apply(
        finder.findByNode(node),
        Outcome.UNKNOWN_NODE,
        network -> use(network, player, node, index),
        saver);
  }

  private static Outcome use(Network network, PlayerId player, NodeId nodeId, int index) {
    Node node = network.node(nodeId).orElseThrow();
    if (index < 0 || index >= node.slots().size() || node.slots().get(index).isPresent()) {
      return Outcome.INVALID_SLOT;
    }
    ChannelColor channel = network.firstFreeChannel().orElse(ChannelColor.WHITE);
    ChannelSlot input = new ChannelSlot(Direction.INPUT, node.position().face(), channel);
    return network.configureSlot(player, nodeId, index, input);
  }
}
