package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import java.util.Optional;

/** Finds a network saved in the world. */
@FunctionalInterface
public interface NetworkFinder {
  Optional<Network> find(NetworkId id);
}
