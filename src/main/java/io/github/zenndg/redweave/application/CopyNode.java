package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.NodeCopy;
import io.github.zenndg.redweave.domain.network.NodeId;
import java.util.Optional;

/**
 * The Configurator copies the slots of a node (answer 105). Anyone may copy, as anyone may see the
 * Node Screen, and a node without a network has nothing to copy.
 */
public final class CopyNode {
  private final NodeNetworkFinder finder;

  public CopyNode(NodeNetworkFinder finder) {
    this.finder = finder;
  }

  public Optional<NodeCopy> copy(NodeId node) {
    return finder.findByNode(node).flatMap(network -> network.node(node)).map(NodeCopy::of);
  }
}
