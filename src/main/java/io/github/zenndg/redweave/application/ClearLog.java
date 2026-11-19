package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.Outcome;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The owner clears the log of a network on the Controller Screen, for everyone who opens it (answer
 * 264). Only the owner changes the network, with no exception (answers 22 and 28).
 */
public final class ClearLog {
  private final NetworkFinder finder;
  private final SignalLogs logs;

  public ClearLog(NetworkFinder finder, SignalLogs logs) {
    this.finder = finder;
    this.logs = logs;
  }

  public Outcome clear(PlayerId player, NetworkId id) {
    Optional<Network> network = finder.find(id);
    if (network.isEmpty()) {
      return Outcome.UNKNOWN_NETWORK;
    }
    if (!network.get().isOwner(player)) {
      return Outcome.NOT_OWNER;
    }
    logs.clear(id);
    return Outcome.ACCEPTED;
  }
}
