package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.NodeId;
import java.util.Optional;

/** Finds the one network a node belongs to, if any (rede-e-canais.md). */
@FunctionalInterface
public interface NodeNetworkFinder {
  Optional<Network> findByNode(NodeId node);
}
