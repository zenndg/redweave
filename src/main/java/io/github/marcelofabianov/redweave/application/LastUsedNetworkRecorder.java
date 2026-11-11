package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;

/** Keeps the network a player last put a node in, for the next choice (answer 157). */
@FunctionalInterface
public interface LastUsedNetworkRecorder {
  void record(PlayerId player, NetworkId network);
}
