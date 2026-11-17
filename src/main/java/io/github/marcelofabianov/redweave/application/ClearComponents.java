package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;

/** The owner takes every node out of a network at a Network Terminal (answers 133 and 176). */
public final class ClearNodes {
  private final NetworkFinder finder;
  private final NetworkSaver saver;

  public ClearNodes(NetworkFinder finder, NetworkSaver saver) {
    this.finder = finder;
    this.saver = saver;
  }

  public Outcome clear(PlayerId player, NetworkId id) {
    return SavedChange.apply(
        finder.find(id),
        Outcome.UNKNOWN_NETWORK,
        network -> network.terminal().clearNodes(player),
        saver);
  }
}
