package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;

/** The Node Screen configures one slot of the node, in the one network of the node. */
public final class ConfigureSlot {
  private final NodeNetworkFinder finder;
  private final NetworkSaver saver;

  public ConfigureSlot(NodeNetworkFinder finder, NetworkSaver saver) {
    this.finder = finder;
    this.saver = saver;
  }

  public Outcome configure(PlayerId player, NodeId node, int index, ChannelSlot slot) {
    return SavedChange.apply(
        finder.findByNode(node),
        Outcome.UNKNOWN_NODE,
        network -> network.configureSlot(player, node, index, slot),
        saver);
  }
}
