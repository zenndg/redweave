package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The Configurator clicks a Network Controller to keep its network. Only the owner keeps it,
 * because only they could link nodes to it, and the current network does not change (104).
 */
public final class StoreNetwork {
  private final NetworkFinder finder;

  public StoreNetwork(NetworkFinder finder) {
    this.finder = finder;
  }

  public Outcome store(PlayerId player, NetworkId id) {
    Optional<Network> network = finder.find(id);
    if (network.isEmpty()) {
      return Outcome.UNKNOWN_NETWORK;
    }
    return network.get().isOwner(player) ? Outcome.ACCEPTED : Outcome.NOT_OWNER;
  }
}
