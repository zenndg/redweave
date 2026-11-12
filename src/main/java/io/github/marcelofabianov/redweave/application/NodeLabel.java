package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.NetworkState;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.List;
import java.util.Optional;

/**
 * What the floating label over a node shows with the Configurator in hand: the node, its network
 * and the state of it, up to 4 enabled channels and how many more (answers 268 and 270). Without a
 * network, the network is empty and there are no channels.
 */
public record NodeLabel(
    String node, Optional<NetworkPart> network, List<Channel> channels, int more) {
  public NodeLabel {
    channels = List.copyOf(channels);
  }

  /** The network of the node: its name and state, as `Offline` (answer 270). */
  public record NetworkPart(String name, NetworkState state) {}

  /** An enabled channel: its color, the name the owner gave it, the direction and the signal. */
  public record Channel(
      ChannelColor color, Optional<String> name, Direction direction, Signal signal) {}
}
