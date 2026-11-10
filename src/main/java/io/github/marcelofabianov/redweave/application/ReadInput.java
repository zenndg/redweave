package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Optional;

/**
 * A Signal Node reads the redstone of its block into an input. A new value leaves the network
 * waiting for the next tick and is saved, so it holds while the node is unloaded (answer 37).
 */
public final class ReadInput {
  private final NodeNetworkFinder finder;
  private final NetworkSaver saver;

  public ReadInput(NodeNetworkFinder finder, NetworkSaver saver) {
    this.finder = finder;
    this.saver = saver;
  }

  public Outcome read(NodeId node, int index, Signal signal) {
    Optional<Network> network = finder.findByNode(node);
    if (network.isEmpty()) {
      return Outcome.UNKNOWN_NODE;
    }
    if (network.get().readInput(node, index, signal)) {
      saver.save(network.get());
    }
    return Outcome.ACCEPTED;
  }
}
