package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;

/** The owner names a slot on the Node Screen; blank takes the name away (answer 227). */
public final class RenameSlot {
  private final NodeNetworkFinder finder;
  private final NetworkSaver saver;

  public RenameSlot(NodeNetworkFinder finder, NetworkSaver saver) {
    this.finder = finder;
    this.saver = saver;
  }

  public Outcome rename(PlayerId player, NodeId node, int index, String name) {
    return SavedChange.apply(
        finder.findByNode(node),
        Outcome.UNKNOWN_NODE,
        network -> network.renameSlot(player, node, index, name),
        saver);
  }
}
