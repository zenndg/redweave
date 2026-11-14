package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Channel;
import io.github.marcelofabianov.redweave.domain.network.NodeState;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.List;
import java.util.Optional;

/**
 * What the screen and the label of a Signal Module show of its network: the state seen from the
 * plate, the name and color, and the channels with their names. `gone` tells that the network was
 * deleted, so the module lets it go (answers 385 and 400).
 */
public record ModuleNetworkView(
    NodeState state,
    Optional<String> name,
    Optional<ChannelColor> color,
    List<Channel> channels,
    boolean gone) {
  static final ModuleNetworkView NONE =
      new ModuleNetworkView(
          NodeState.NO_NETWORK, Optional.empty(), Optional.empty(), List.of(), false);

  public ModuleNetworkView {
    channels = List.copyOf(channels);
  }
}
