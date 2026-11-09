package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Network;

/** Saves a network in the world after a change. */
@FunctionalInterface
public interface NetworkSaver {
  void save(Network network);
}
