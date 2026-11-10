package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;

/**
 * A Signal Node left the world without its owner, as when its block is gone or an explosion took
 * it. There is nobody to ask, so it leaves its network anyway (answer 74).
 */
public final class LoseNode {
  private final NodeNetworkFinder finder;
  private final NetworkSaver saver;

  public LoseNode(NodeNetworkFinder finder, NetworkSaver saver) {
    this.finder = finder;
    this.saver = saver;
  }

  public Outcome lose(NodeId node) {
    return SavedChange.apply(
        finder.findByNode(node), Outcome.UNKNOWN_NODE, network -> network.loseNode(node), saver);
  }
}
