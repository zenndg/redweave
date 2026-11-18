package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.OutputDelayLimits;

/**
 * The owner of the network of a node steps its output delay on the Node Screen, one second at a
 * time, within the config (answers 830, 859 and 864). A node with no network cannot be configured
 * (answer 139), and a refusal is not saved.
 */
public final class ChangeNodeDelay {
  private final NodeNetworkFinder finder;
  private final NetworkSaver saver;

  public ChangeNodeDelay(NodeNetworkFinder finder, NetworkSaver saver) {
    this.finder = finder;
    this.saver = saver;
  }

  public Outcome step(PlayerId player, NodeId node, int seconds, OutputDelayLimits limits) {
    return SavedChange.apply(
        finder.findByNode(node),
        Outcome.UNKNOWN_NODE,
        network -> network.nodeSettings().stepDelay(player, node, seconds, limits),
        saver);
  }
}
