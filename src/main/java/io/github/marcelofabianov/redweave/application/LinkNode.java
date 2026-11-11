package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.NodePosition;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * Moves a node to another network, from the Node Screen in #87 (answers 141 and 194). The node
 * leaves the old one, so it has one network at most, and the player must own both, so nobody takes
 * a node of another (answer 42).
 */
public final class LinkNode {
  private final NetworkFinder finder;
  private final NodeNetworkFinder nodeFinder;
  private final NetworkSaver saver;

  public LinkNode(NetworkFinder finder, NodeNetworkFinder nodeFinder, NetworkSaver saver) {
    this.finder = finder;
    this.nodeFinder = nodeFinder;
    this.saver = saver;
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
    old.ifPresent(network -> {
      network.detachNode(player, node);
      saver.save(network);
    });
    Outcome joined = JoinNetwork.join(target.get(), player, node, position);
    saver.save(target.get());
    return joined;
  }

  /** Checks everything before changing anything, so a refusal leaves both networks as they were. */
  private static Outcome mayMove(PlayerId player, Optional<Network> old, Network target) {
    if (old.filter(network -> network.id().equals(target.id())).isPresent()) {
      return Outcome.ALREADY_ATTACHED;
    }
    boolean ownsOld = old.map(network -> network.isOwner(player)).orElse(true);
    return ownsOld && target.isOwner(player) ? Outcome.ACCEPTED : Outcome.NOT_OWNER;
  }
}
