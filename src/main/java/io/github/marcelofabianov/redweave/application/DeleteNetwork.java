package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The owner deletes an unplaced network at a Network Terminal: it leaves the saves and its log
 * goes, and its nodes stay in the world with no network (answers 147 and 166).
 */
public final class DeleteNetwork {
  private final NetworkFinder finder;
  private final NetworkRemover remover;
  private final SignalLogs logs;

  public DeleteNetwork(NetworkFinder finder, NetworkRemover remover, SignalLogs logs) {
    this.finder = finder;
    this.remover = remover;
    this.logs = logs;
  }

  public Outcome delete(PlayerId player, NetworkId id) {
    Optional<Network> network = finder.find(id);
    if (network.isEmpty()) {
      return Outcome.UNKNOWN_NETWORK;
    }
    Outcome outcome = network.get().terminal().deletion(player);
    if (outcome == Outcome.ACCEPTED) {
      remover.remove(id);
      logs.forget(id);
    }
    return outcome;
  }
}
