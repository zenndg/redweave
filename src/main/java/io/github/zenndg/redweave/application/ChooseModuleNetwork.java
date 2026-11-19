package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.Outcome;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The owner of a Signal Module chooses its network, only among his online networks, so no module
 * writes into the network of another player (answers 378, 397 and rule 17).
 */
public final class ChooseModuleNetwork {
  private final NetworkFinder finder;

  public ChooseModuleNetwork(NetworkFinder finder) {
    this.finder = finder;
  }

  public Outcome choose(PlayerId requester, SignalModule module, NetworkId id) {
    if (!module.isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    Optional<Network> found = finder.find(id);
    if (found.isEmpty()) {
      return Outcome.UNKNOWN_NETWORK;
    }
    if (!found.get().isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    if (!found.get().isOnline()) {
      return Outcome.NETWORK_NOT_ONLINE;
    }
    return module.choose(requester, id);
  }
}
