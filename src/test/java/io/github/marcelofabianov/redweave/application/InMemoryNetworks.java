package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Every port about the networks, kept in memory, as the saved world would keep them. */
final class InMemoryNetworks
    implements AllNetworksFinder,
        NetworkFinder,
        NetworkSaver,
        NetworkRemover,
        NodeNetworkFinder,
        OwnerNetworkFinder {
  private final Map<NetworkId, Network> networks = new LinkedHashMap<>();
  private int saves;

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
    saves++;
  }

  @Override
  public void remove(NetworkId id) {
    networks.remove(id);
  }

  @Override
  public Optional<Network> findByNode(NodeId node) {
    return networks.values().stream().filter(network -> network.node(node).isPresent()).findFirst();
  }

  @Override
  public List<Network> ownedBy(PlayerId owner) {
    return networks.values().stream().filter(network -> network.isOwner(owner)).toList();
  }

  /** How many times a use case saved, which is what it hands to the world. */
  int saves() {
    return saves;
  }
}
