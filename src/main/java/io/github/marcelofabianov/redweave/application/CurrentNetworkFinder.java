package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;

/** The current network of a player: the last one they created or opened as owner (answer 25). */
@FunctionalInterface
public interface CurrentNetworkFinder {
  Optional<NetworkId> currentOf(PlayerId player);
}
