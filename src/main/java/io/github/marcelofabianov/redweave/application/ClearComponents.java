package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;

/**
 * The owner takes every component out of a network at a Network Terminal: the nodes and the
 * registered Signal Modules (answers 133, 176 and 807).
 */
public final class ClearComponents {
  private final NetworkFinder finder;
  private final NetworkSaver saver;

  public ClearComponents(NetworkFinder finder, NetworkSaver saver) {
    this.finder = finder;
    this.saver = saver;
  }

  public Outcome clear(PlayerId player, NetworkId id) {
    return SavedChange.apply(
        finder.find(id),
        Outcome.UNKNOWN_NETWORK,
        network -> network.terminal().clearComponents(player),
        saver);
  }
}
