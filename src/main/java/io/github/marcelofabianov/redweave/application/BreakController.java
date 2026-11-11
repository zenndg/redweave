package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;

/**
 * A player breaks a Network Controller. Only the owner does, and the network is unplaced until the
 * item is placed again (answers 24 and 30).
 */
public final class BreakController {
  private final NetworkFinder finder;
  private final NetworkSaver saver;

  public BreakController(NetworkFinder finder, NetworkSaver saver) {
    this.finder = finder;
    this.saver = saver;
  }

  public Outcome breakBy(PlayerId player, NetworkId id) {
    return SavedChange.apply(
        finder.find(id),
        Outcome.UNKNOWN_NETWORK,
        network -> network.removeController(player),
        saver);
  }
}
