package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;

/** Makes a network the current one of a player (answer 25). */
@FunctionalInterface
public interface CurrentNetworkChooser {
  void choose(PlayerId player, NetworkId network);
}
