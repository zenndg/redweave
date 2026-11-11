package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;

/** The Node Screen frees one slot of the node, in the one network of the node. */
public final class ClearSlot {
  private final NodeNetworkFinder finder;
  private final NetworkSaver saver;

  public ClearSlot(NodeNetworkFinder finder, NetworkSaver saver) {
    this.finder = finder;
    this.saver = saver;
  }

  public Outcome clear(PlayerId player, NodeId node, int index) {
    return SavedChange.apply(
        finder.findByNode(node),
        Outcome.UNKNOWN_NODE,
        network -> network.clearSlot(player, node, index),
        saver);
  }
}
