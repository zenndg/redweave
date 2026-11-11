package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;

/** The network a player last put a node in, highlighted on the network choice (answer 157). */
@FunctionalInterface
public interface LastUsedNetworkFinder {
  Optional<NetworkId> lastUsedOf(PlayerId player);
}
