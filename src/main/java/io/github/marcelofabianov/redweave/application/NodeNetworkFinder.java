package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import java.util.Optional;

/** Finds the one network a node belongs to, if any (rede-e-canais.md). */
@FunctionalInterface
public interface NodeNetworkFinder {
  Optional<Network> findByNode(NodeId node);
}
