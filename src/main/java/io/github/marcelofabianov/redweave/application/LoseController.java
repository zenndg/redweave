package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;

/**
 * The controller left the world without its owner breaking it, as in creative, by a command or by
 * another mod. The block is already gone, so the network goes offline anyway (answer 49).
 */
public final class LoseController {
  private final NetworkFinder finder;
  private final NetworkSaver saver;

  public LoseController(NetworkFinder finder, NetworkSaver saver) {
    this.finder = finder;
    this.saver = saver;
  }

  public Outcome lose(NetworkId id) {
    return SavedChange.apply(
        finder.find(id),
        Outcome.UNKNOWN_NETWORK,
        network -> {
          network.loseController();
          return Outcome.ACCEPTED;
        },
        saver);
  }
}
