package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.Outcome;
import io.github.zenndg.redweave.domain.network.PlayerId;

/** The status of a slot on the Node Screen turned to Disabled, keeping its configuration (228). */
public final class DisableSlot {
  private final NodeNetworkFinder finder;
  private final NetworkSaver saver;

  public DisableSlot(NodeNetworkFinder finder, NetworkSaver saver) {
    this.finder = finder;
    this.saver = saver;
  }

  public Outcome disable(PlayerId player, NodeId node, int index) {
    return SavedChange.apply(
        finder.findByNode(node),
        Outcome.UNKNOWN_NODE,
        network -> network.disableSlot(player, node, index),
        saver);
  }
}
