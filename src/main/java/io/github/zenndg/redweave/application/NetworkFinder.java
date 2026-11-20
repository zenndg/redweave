package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.NetworkId;
import java.util.Optional;

/** Finds a network saved in the world. */
@FunctionalInterface
public interface NetworkFinder {
  Optional<Network> find(NetworkId id);
}
