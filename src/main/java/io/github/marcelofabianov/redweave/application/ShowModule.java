package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NetworkState;
import io.github.marcelofabianov.redweave.domain.network.NodeState;
import java.util.List;
import java.util.Optional;

/**
 * The network of a Signal Module as its plate sees it, with the same states and texts as a node: no
 * network, unplaced, offline, out of range or online (answers 385 and 402).
 */
public final class ShowModule {
  private final NetworkFinder finder;

  public ShowModule(NetworkFinder finder) {
    this.finder = finder;
  }

  public ModuleNetworkView show(SignalModule module, BlockLocation plate) {
    if (module.network().isEmpty()) {
      return ModuleNetworkView.NONE;
    }
    NetworkId id = module.network().get();
    Optional<Network> found = finder.find(id);
    if (found.isEmpty()) {
      return new ModuleNetworkView(
          NodeState.NO_NETWORK, Optional.empty(), Optional.empty(), List.of(), true);
    }
    Network network = found.get();
    return new ModuleNetworkView(
        stateOf(network, plate),
        Optional.of(network.name()),
        Optional.of(network.color()),
        network.channels(),
        false);
  }

  private static NodeState stateOf(Network network, BlockLocation plate) {
    if (network.state() == NetworkState.UNPLACED) {
      return NodeState.NO_NETWORK;
    }
    if (network.state() == NetworkState.OFFLINE) {
      return NodeState.OFFLINE;
    }
    return network.modules().reaches(plate) ? NodeState.ONLINE : NodeState.OUT_OF_RANGE;
  }
}
