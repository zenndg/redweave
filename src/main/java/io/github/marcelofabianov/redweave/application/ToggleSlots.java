package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.Node;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The Toggle mode of the Configurator: a click enables every configured channel of a node, and
 * Shift + click disables them, through the slot operations of the network, so the owner is checked.
 * A channel never configured stays as it is (answer 241).
 */
public final class ToggleSlots {
  private static final int NONE = 0;

  private final NodeNetworkFinder finder;
  private final NetworkSaver saver;

  public ToggleSlots(NodeNetworkFinder finder, NetworkSaver saver) {
    this.finder = finder;
    this.saver = saver;
  }

  public ToggleResult toggle(PlayerId player, NodeId nodeId, boolean enable) {
    Optional<Network> network = finder.findByNode(nodeId);
    Optional<Node> node = network.flatMap(found -> found.node(nodeId));
    if (node.isEmpty()) {
      return new ToggleResult(Outcome.UNKNOWN_NODE, NONE);
    }
    if (!network.get().isOwner(player)) {
      return new ToggleResult(Outcome.NOT_OWNER, NONE);
    }
    int switched = 0;
    for (int index = 0; index < node.get().slots().size(); index++) {
      if (node.get().slots().get(index).isPresent()) {
        Outcome outcome =
            enable
                ? network.get().enableSlot(player, nodeId, index)
                : network.get().disableSlot(player, nodeId, index);
        switched += outcome == Outcome.ACCEPTED ? 1 : 0;
      }
    }
    saver.save(network.get());
    return new ToggleResult(Outcome.ACCEPTED, switched);
  }
}
