package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.DefaultNames;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.Node;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Builds the label of a node from its network, or from nothing when it has none (answer 270). It
 * only reads the network, and the world decides which nodes are near the player (answer 268).
 */
public final class NodeLabels {
  /** The same 4 as the dots on the corners of the plate (answer 229). */
  private static final int SHOWN = 4;

  private static final ChannelColor[] COLORS = ChannelColor.values();

  private NodeLabels() {}

  public static NodeLabel of(NodeId id, Optional<Network> network) {
    Optional<Node> node = network.flatMap(found -> found.node(id));
    if (network.isEmpty() || node.isEmpty()) {
      String name = DefaultNames.of(id.value());
      return new NodeLabel(name, Optional.empty(), List.of(), 0);
    }
    List<NodeLabel.Channel> channels = channelsOf(network.get(), node.get());
    int shown = Math.min(SHOWN, channels.size());
    return new NodeLabel(
        node.get().displayName(),
        Optional.of(new NodeLabel.NetworkPart(network.get().name(), network.get().state())),
        channels.subList(0, shown),
        channels.size() - shown);
  }

  private static List<NodeLabel.Channel> channelsOf(Network network, Node node) {
    List<NodeLabel.Channel> channels = new ArrayList<>();
    for (int index = 0; index < node.slots().size(); index++) {
      Optional<ChannelSlot> slot = node.slots().get(index).filter(ChannelSlot::active);
      if (slot.isPresent()) {
        ChannelColor color = COLORS[index];
        channels.add(
            new NodeLabel.Channel(
                color,
                network.channel(color).name(),
                slot.get().direction(),
                network.slotSignal(node.id(), index)));
      }
    }
    return channels;
  }
}
