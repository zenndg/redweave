package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.Outcome;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * Checks a link of a Signal Remote: only the owner links, only to an enabled input slot of an
 * online network (answers 287, 357 and 359). The item keeps the link; the network does not change.
 */
public final class LinkRemote {
  private final NetworkFinder finder;

  public LinkRemote(NetworkFinder finder) {
    this.finder = finder;
  }

  public Outcome link(PlayerId requester, NetworkId id, NodeId node, int index) {
    Optional<Network> found = finder.find(id);
    if (found.isEmpty()) {
      return Outcome.UNKNOWN_NETWORK;
    }
    Network network = found.get();
    if (!network.isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    if (!network.isOnline()) {
      return Outcome.NETWORK_NOT_ONLINE;
    }
    return network.remotes().accepts(node, index) ? Outcome.ACCEPTED : Outcome.INVALID_SLOT;
  }
}
