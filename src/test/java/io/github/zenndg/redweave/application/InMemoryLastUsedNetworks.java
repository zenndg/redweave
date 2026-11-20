package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** The network each player last put a node in, kept in memory (answer 157). */
final class InMemoryLastUsedNetworks implements LastUsedNetworkFinder, LastUsedNetworkRecorder {
  private final Map<PlayerId, NetworkId> lastUsed = new HashMap<>();

  @Override
  public Optional<NetworkId> lastUsedOf(PlayerId player) {
    return Optional.ofNullable(lastUsed.get(player));
  }

  @Override
  public void record(PlayerId player, NetworkId network) {
    lastUsed.put(player, network);
  }
}
