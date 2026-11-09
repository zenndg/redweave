package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NetworkLimits;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/** A player places a Network Controller: a new network is born, and it becomes their current. */
public final class CreateNetwork {
  private static final String NAME_PREFIX = "Network ";
  private static final int ID_CHARACTERS = 4;

  private final IdGenerator ids;
  private final OwnerNetworkFinder owned;
  private final NetworkSaver saver;
  private final CurrentNetworkChooser current;
  private final NetworkLimits limits;

  public CreateNetwork(
      IdGenerator ids,
      OwnerNetworkFinder owned,
      NetworkSaver saver,
      CurrentNetworkChooser current,
      NetworkLimits limits) {
    this.ids = ids;
    this.owned = owned;
    this.saver = saver;
    this.current = current;
    this.limits = limits;
  }

  public NetworkId create(PlayerId owner, BlockLocation controller) {
    NetworkId id = new NetworkId(ids.newId());
    Network network =
        Network.create(id, owner, defaultName(id), firstFreeColor(owner), controller, limits);
    saver.save(network);
    current.choose(owner, id);
    return id;
  }

  /** "Network" and the first characters of the ID, in English, the base (answer 35). */
  private static String defaultName(NetworkId id) {
    return NAME_PREFIX + id.value().toString().substring(0, ID_CHARACTERS);
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
