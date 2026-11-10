package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;

/**
 * A player breaks a Signal Node. In survival, only the owner of its network does, and the node
 * leaves the network. A node without a network changes nothing, so anyone breaks it (74).
 */
public final class BreakNode {
  private final NodeNetworkFinder finder;
  private final NetworkSaver saver;

  public BreakNode(NodeNetworkFinder finder, NetworkSaver saver) {
    this.finder = finder;
    this.saver = saver;
  }

  public Outcome breakBy(PlayerId player, NodeId node) {
    return SavedChange.apply(
        finder.findByNode(node),
        Outcome.ACCEPTED,
        network -> network.detachNode(player, node),
        saver);
  }
}
