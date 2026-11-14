package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;

/** The owner turns a network off and back on at a Network Terminal (answers 132 and 135). */
public final class TurnNetwork {
  private final NetworkFinder finder;
  private final NetworkSaver saver;

  public TurnNetwork(NetworkFinder finder, NetworkSaver saver) {
    this.finder = finder;
    this.saver = saver;
  }

  public Outcome off(PlayerId player, NetworkId id) {
    return SavedChange.apply(
        finder.find(id),
        Outcome.UNKNOWN_NETWORK,
        network -> network.terminal().turnOff(player),
        saver);
  }

  public Outcome on(PlayerId player, NetworkId id) {
    return SavedChange.apply(
        finder.find(id),
        Outcome.UNKNOWN_NETWORK,
        network -> network.terminal().turnOn(player),
        saver);
  }
}
