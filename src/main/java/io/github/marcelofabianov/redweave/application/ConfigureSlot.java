package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The Node Screen changes the direction or the face of a slot. The status and the name stay as the
 * node has them, because the screen sends only what it changes (answers 227 and 228).
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
        network ->
            network.configureSlot(
                player, node, index, keepingStatusAndName(network, node, index, slot)),
        saver);
  }

  private static ChannelSlot keepingStatusAndName(
      Network network, NodeId node, int index, ChannelSlot slot) {
    Optional<ChannelSlot> current =
        network
            .node(node)
            .filter(found -> index >= 0 && index < found.slots().size())
            .flatMap(found -> found.slots().get(index));
    return current.map(kept -> slot.enabled(kept.active()).named(kept.name())).orElse(slot);
  }
}
