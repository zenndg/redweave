package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.Node;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.NodePosition;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * Picks or switches the network of a node, from the Node Screen (answers 139, 141 and 194). The
 * node leaves the old one, so it has one network at most, keeps its name (200) and its output delay
 * (870) and joins only an online network (157). The player must own both, so nobody takes a node of
 * another (42). Who may pick for a node with no network is checked by the block, which knows who
 * placed it (172).
 */
public final class LinkNode {
  private final NetworkFinder finder;
  private final NodeNetworkFinder nodeFinder;
  private final NetworkSaver saver;
  private final LastUsedNetworkRecorder lastUsed;

  public LinkNode(
      NetworkFinder finder,
      NodeNetworkFinder nodeFinder,
      NetworkSaver saver,
      LastUsedNetworkRecorder lastUsed) {
    this.finder = finder;
    this.nodeFinder = nodeFinder;
    this.saver = saver;
    this.lastUsed = lastUsed;
  }

  public Outcome link(PlayerId player, NodeId node, NodePosition position, NetworkId targetId) {
    Optional<Network> target = finder.find(targetId);
    if (target.isEmpty()) {
      return Outcome.UNKNOWN_NETWORK;
    }
    Optional<Network> old = nodeFinder.findByNode(node);
    Outcome allowed = mayMove(player, old, target.get());
    if (allowed != Outcome.ACCEPTED) {
      return allowed;
    }
    Optional<Node> moving = old.flatMap(network -> network.node(node));
    old.ifPresent(network -> {
      network.detachNode(player, node);
      saver.save(network);
    });
    Outcome joined = target.get().attachNode(player, node, position, moving.flatMap(Node::name));
    moving.ifPresent(was -> target.get().nodeSettings().delay(player, node, was.delay()));
    saver.save(target.get());
    lastUsed.record(player, targetId);
    return joined;
  }

  /** Checks everything before changing anything, so a refusal leaves both networks as they were. */
  private static Outcome mayMove(PlayerId player, Optional<Network> old, Network target) {
    if (old.filter(network -> network.id().equals(target.id())).isPresent()) {
      return Outcome.ALREADY_ATTACHED;
    }
    boolean ownsOld = old.map(network -> network.isOwner(player)).orElse(true);
    if (!ownsOld || !target.isOwner(player)) {
      return Outcome.NOT_OWNER;
    }
    return target.isOnline() ? Outcome.ACCEPTED : Outcome.NETWORK_NOT_ONLINE;
  }
}
