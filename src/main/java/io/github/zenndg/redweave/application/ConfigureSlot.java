package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.ChannelSlot;
import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.Outcome;
import io.github.zenndg.redweave.domain.network.PlayerId;

/**
 * The Node Screen sets up the direction and the face of a slot that is not enabled. An enabled slot
 * is locked, so a change never fires redstone by accident (answer 237).
 */
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
        network -> network.setUpSlot(player, node, index, slot.direction(), slot.face()),
        saver);
  }
}
