package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.OutputDelay;
import io.github.marcelofabianov.redweave.domain.signal.OutputDelayLimits;

/**
 * What the owner of a network sets on a node as a whole, and not on one of its slots: its name
 * (answer 144) and its output delay (answers 859 to 861). Neither changes a signal of the network.
 * It left the `Network` so the aggregate stays under 400 lines (answers 426 and 875).
 */
public final class NodeSettings {
  private final Network network;
  private final Nodes nodes;
  private final NetworkLimits limits;

  NodeSettings(Network network, Nodes nodes, NetworkLimits limits) {
    this.network = network;
    this.nodes = nodes;
    this.limits = limits;
  }

  /** Names a node, for the owner only; blank gives back the default (answer 144). */
  public Outcome rename(PlayerId requester, NodeId nodeId, String newName) {
    if (!network.isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    return nodes.rename(nodeId, newName, limits.nodeNameLength());
  }

  /** One `−` or `+` of the Node Screen, a second at a time within the config (830 and 864). */
  public Outcome stepDelay(
      PlayerId requester, NodeId nodeId, int seconds, OutputDelayLimits delays) {
    Node node = nodes.at(nodeId);
    if (node == null) {
      return network.isOwner(requester) ? Outcome.UNKNOWN_NODE : Outcome.NOT_OWNER;
    }
    return delay(requester, nodeId, node.delay().stepped(seconds, delays));
  }

  /** The delay of a paste or of a node that moves in, for the owner only (868 and 870). */
  public Outcome delay(PlayerId requester, NodeId nodeId, OutputDelay delay) {
    if (!network.isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    return nodes.delay(nodeId, delay);
  }
}
