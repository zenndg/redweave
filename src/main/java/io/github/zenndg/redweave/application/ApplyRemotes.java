package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.RemoteInput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Each tick, the Signal Remotes that are on reach their networks: only those within the range of
 * the controller, measured from the holder. Every network gets its whole set, an empty one too, so
 * a tablet that left the inventory goes back to off (answers 27, 354 and 355). Nothing is saved.
 */
public final class ApplyRemotes {
  private final AllNetworksFinder finder;

  public ApplyRemotes(AllNetworksFinder finder) {
    this.finder = finder;
  }

  public void apply(List<RemoteSighting> sightings) {
    Map<NetworkId, List<RemoteSighting>> byNetwork = new HashMap<>();
    sightings.forEach(sighting ->
        byNetwork.computeIfAbsent(sighting.network(), id -> new ArrayList<>()).add(sighting));
    for (Network network : finder.all()) {
      network.remotes().apply(reaching(network, byNetwork.getOrDefault(network.id(), List.of())));
    }
  }

  private static Set<RemoteInput> reaching(Network network, List<RemoteSighting> sightings) {
    Set<RemoteInput> tablets = new HashSet<>();
    for (RemoteSighting sighting : sightings) {
      if (network.remotes().reaches(sighting.holder())) {
        tablets.add(sighting.tablet());
      }
    }
    return tablets;
  }
}
