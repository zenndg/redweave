package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.NetworkId;

/** Takes a network out of the saves for good, as the Delete of a Network Terminal (166). */
@FunctionalInterface
public interface NetworkRemover {
  void remove(NetworkId id);
}
