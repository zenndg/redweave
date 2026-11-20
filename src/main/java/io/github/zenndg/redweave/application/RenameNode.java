package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.Outcome;
import io.github.zenndg.redweave.domain.network.PlayerId;

/**
 * The Node Screen renames the node, in the one network of the node, or gives it back the default
 * name with a blank one (answers 144 and 204). A node with no network cannot be configured (139).
 */
public final class RenameNode {
  private final NodeNetworkFinder finder;
  private final NetworkSaver saver;

  public RenameNode(NodeNetworkFinder finder, NetworkSaver saver) {
    this.finder = finder;
    this.saver = saver;
  }

  public Outcome rename(PlayerId player, NodeId node, String newName) {
    return SavedChange.apply(
        finder.findByNode(node),
        Outcome.UNKNOWN_NODE,
        network -> network.nodeSettings().rename(player, node, newName),
        saver);
  }
}
