package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.NodePosition;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * A player places a Signal Node. It joins their current network with one input on the face it
 * sticks to, on the first free channel, so it works without opening a screen (answer 25).
 */
public final class PlaceNode {
  private final IdGenerator ids;
  private final CurrentNetworkFinder current;
  private final NetworkFinder finder;
  private final NetworkSaver saver;

  public PlaceNode(
      IdGenerator ids, CurrentNetworkFinder current, NetworkFinder finder, NetworkSaver saver) {
    this.ids = ids;
    this.current = current;
    this.finder = finder;
    this.saver = saver;
  }

  public PlacedNode place(PlayerId player, NodePosition position) {
    NodeId node = new NodeId(ids.newId());
    Optional<NetworkId> currentNetwork = current.currentOf(player);
    if (currentNetwork.isEmpty()) {
      return new PlacedNode(node, Outcome.NO_CURRENT_NETWORK);
    }
    Optional<Network> network = finder.find(currentNetwork.get());
    if (network.isEmpty()) {
      return new PlacedNode(node, Outcome.UNKNOWN_NETWORK);
    }
    Outcome outcome = JoinNetwork.join(network.get(), player, node, position);
    if (outcome == Outcome.ACCEPTED) {
      saver.save(network.get());
    }
    return new PlacedNode(node, outcome);
  }
}
