package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.Outcome;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.domain.signal.ChannelColor;

/** The Controller Screen changes the color of the network, for its owner only. */
public final class RecolorNetwork {
  private final NetworkFinder finder;
  private final NetworkSaver saver;

  public RecolorNetwork(NetworkFinder finder, NetworkSaver saver) {
    this.finder = finder;
    this.saver = saver;
  }

  public Outcome recolor(PlayerId player, NetworkId id, ChannelColor color) {
    return SavedChange.apply(
        finder.find(id), Outcome.UNKNOWN_NETWORK, network -> network.recolor(player, color), saver);
  }
}
