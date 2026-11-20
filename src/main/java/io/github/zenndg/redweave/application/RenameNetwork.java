package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.Outcome;
import io.github.zenndg.redweave.domain.network.PlayerId;

/** The Controller Screen renames the network, for its owner only. */
public final class RenameNetwork {
  private final NetworkFinder finder;
  private final NetworkSaver saver;

  public RenameNetwork(NetworkFinder finder, NetworkSaver saver) {
    this.finder = finder;
    this.saver = saver;
  }

  public Outcome rename(PlayerId player, NetworkId id, String name) {
    return SavedChange.apply(
        finder.find(id), Outcome.UNKNOWN_NETWORK, network -> network.rename(player, name), saver);
  }
}
