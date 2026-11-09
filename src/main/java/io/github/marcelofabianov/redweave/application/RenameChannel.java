package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;

/** The Signals tab names a channel, for the owner only (answer 26). */
public final class RenameChannel {
  private final NetworkFinder finder;
  private final NetworkSaver saver;

  public RenameChannel(NetworkFinder finder, NetworkSaver saver) {
    this.finder = finder;
    this.saver = saver;
  }

  public Outcome rename(PlayerId player, NetworkId id, ChannelColor channel, String name) {
    return SavedChange.apply(
        finder.find(id),
        Outcome.UNKNOWN_NETWORK,
        network -> network.renameChannel(player, channel, name),
        saver);
  }
}
