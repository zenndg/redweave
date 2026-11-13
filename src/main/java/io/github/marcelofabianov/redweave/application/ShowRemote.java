package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.Node;
import io.github.marcelofabianov.redweave.domain.network.RemoteInput;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Optional;

/**
 * The link of a Signal Remote as it is now. Empty is `Link lost`: the network is gone, or the slot
 * is no longer an enabled input (answer 287). The channel follows the slot (answer 287).
 */
public final class ShowRemote {
  private final NetworkFinder finder;

  public ShowRemote(NetworkFinder finder) {
    this.finder = finder;
  }

  public Optional<RemoteView> show(NetworkId id, RemoteInput tablet) {
    return finder
        .find(id)
        .filter(network -> network.remotes().accepts(tablet.node(), tablet.index()))
        .map(network -> viewOf(network, tablet));
  }

  private static RemoteView viewOf(Network network, RemoteInput tablet) {
    Node node = network.node(tablet.node()).orElseThrow();
    ChannelColor channel = node.slots().get(tablet.index()).orElseThrow().channel();
    return new RemoteView(
        network.name(),
        network.color(),
        node.displayName(),
        channel,
        network.channel(channel).name(),
        !network.isOnline(),
        network.remotes().othersHold(tablet));
  }
}
