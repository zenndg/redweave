package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;

/** A player opens a Network Controller. For its owner, it becomes the current network (25). */
public final class OpenNetwork {
  private final NetworkFinder finder;
  private final CurrentNetworkChooser current;

  public OpenNetwork(NetworkFinder finder, CurrentNetworkChooser current) {
    this.finder = finder;
    this.current = current;
  }

  public Outcome open(PlayerId player, NetworkId id) {
    Optional<Network> network = finder.find(id);
    if (network.isEmpty()) {
      return Outcome.UNKNOWN_NETWORK;
    }
    if (!network.get().isOwner(player)) {
      return Outcome.NOT_OWNER;
    }
    current.choose(player, id);
    return Outcome.ACCEPTED;
  }
}
