package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.Outcome;
import io.github.zenndg.redweave.domain.network.PlayerId;

/**
 * The status of a slot on the Node Screen turned to Enabled: the first time, an input on the face
 * the node sticks to; after that, the slot as it was (answers 226 and 228).
 */
public final class EnableSlot {
  private final NodeNetworkFinder finder;
  private final NetworkSaver saver;

  public EnableSlot(NodeNetworkFinder finder, NetworkSaver saver) {
    this.finder = finder;
    this.saver = saver;
  }

  public Outcome enable(PlayerId player, NodeId node, int index) {
    return SavedChange.apply(
        finder.findByNode(node),
        Outcome.UNKNOWN_NODE,
        network -> network.enableSlot(player, node, index),
        saver);
  }
}
