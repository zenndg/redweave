package io.github.marcelofabianov.redweave.server;

import io.github.marcelofabianov.redweave.application.CurrentNetworkChooser;
import io.github.marcelofabianov.redweave.application.CurrentNetworkFinder;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/** The current network of each player on a running server, in memory until issue #16 (53). */
final class MemoryCurrentNetworks implements CurrentNetworkFinder, CurrentNetworkChooser {
  private final Map<PlayerId, NetworkId> current = new HashMap<>();

  @Override
  public Optional<NetworkId> currentOf(PlayerId player) {
    return Optional.ofNullable(current.get(player));
  }

  @Override
  public void choose(PlayerId player, NetworkId network) {
    current.put(player, network);
  }
}
