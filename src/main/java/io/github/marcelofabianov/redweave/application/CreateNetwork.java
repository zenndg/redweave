package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.DefaultNames;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NetworkLimits;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/** A player places a Network Controller: a new network is born, owned by them (answer 129). */
public final class CreateNetwork {
  private final IdGenerator ids;
  private final OwnerNetworkFinder owned;
  private final NetworkSaver saver;
  private final NetworkLimits limits;

  public CreateNetwork(
      IdGenerator ids, OwnerNetworkFinder owned, NetworkSaver saver, NetworkLimits limits) {
    this.ids = ids;
    this.owned = owned;
    this.saver = saver;
    this.limits = limits;
  }

  public NetworkId create(PlayerId owner, BlockLocation controller) {
    NetworkId id = new NetworkId(ids.newId());
    String name = DefaultNames.of(id.value());
    Network network = Network.create(id, owner, name, firstFreeColor(owner), controller, limits);
    saver.save(network);
    return id;
  }

  /** The first color no other network of the owner uses, or white with all 16 in use (40). */
  private ChannelColor firstFreeColor(PlayerId owner) {
    Set<ChannelColor> used =
        owned.ownedBy(owner).stream().map(Network::color).collect(Collectors.toSet());
    return Arrays.stream(ChannelColor.values())
        .filter(color -> !used.contains(color))
        .findFirst()
        .orElse(ChannelColor.WHITE);
  }
}
