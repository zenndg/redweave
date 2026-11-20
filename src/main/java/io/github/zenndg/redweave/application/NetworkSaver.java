package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.Network;

/** Saves a network in the world after a change. */
@FunctionalInterface
public interface NetworkSaver {
  void save(Network network);
}
