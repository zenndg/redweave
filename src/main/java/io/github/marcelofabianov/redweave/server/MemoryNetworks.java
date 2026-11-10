package io.github.marcelofabianov.redweave.server;

import io.github.marcelofabianov.redweave.application.AllNetworksFinder;
import io.github.marcelofabianov.redweave.application.NetworkFinder;
import io.github.marcelofabianov.redweave.application.NetworkSaver;
import io.github.marcelofabianov.redweave.application.NodeNetworkFinder;
import io.github.marcelofabianov.redweave.application.OwnerNetworkFinder;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The networks of a running server, kept in memory until issue #16 saves them in the world, so they
 * are gone after a restart (answers 48 and 53).
 */
final class MemoryNetworks
    implements AllNetworksFinder,
        NetworkFinder,
        NetworkSaver,
        NodeNetworkFinder,
        OwnerNetworkFinder {
  private final Map<NetworkId, Network> networks = new LinkedHashMap<>();

  @Override
  public List<Network> all() {
    return List.copyOf(networks.values());
  }

  @Override
  public Optional<Network> find(NetworkId id) {
    return Optional.ofNullable(networks.get(id));
  }

  @Override
  public void save(Network network) {
    networks.put(network.id(), network);
  }

  @Override
  public Optional<Network> findByNode(NodeId node) {
    return networks.values().stream().filter(network -> network.node(node).isPresent()).findFirst();
  }

  @Override
  public List<Network> ownedBy(PlayerId owner) {
    return networks.values().stream().filter(network -> network.isOwner(owner)).toList();
  }
}
