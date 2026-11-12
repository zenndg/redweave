package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.SignalChange;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Optional;

/**
 * A Signal Node reads the redstone of its block into an input. A new value leaves the network
 * waiting for the next tick, is saved, so it holds while the node is unloaded (answer 37), and is a
 * line of the log of the Controller Screen (answer 147).
 */
public final class ReadInput {
  private static final ChannelColor[] COLORS = ChannelColor.values();

  private final NodeNetworkFinder finder;
  private final NetworkSaver saver;
  private final SignalLogs logs;
  private final ServerClock clock;

  public ReadInput(
      NodeNetworkFinder finder, NetworkSaver saver, SignalLogs logs, ServerClock clock) {
    this.finder = finder;
    this.saver = saver;
    this.logs = logs;
    this.clock = clock;
  }

  public Outcome read(NodeId node, int index, Signal signal) {
    Optional<Network> network = finder.findByNode(node);
    if (network.isEmpty()) {
      return Outcome.UNKNOWN_NODE;
    }
    if (network.get().readInput(node, index, signal)) {
      saver.save(network.get());
      logs.record(network.get().id(), lineOf(network.get(), node, index, signal));
    }
    return Outcome.ACCEPTED;
  }

  /** The names of the moment, so renaming later leaves the line as it was (answer 245). */
  private SignalChange lineOf(Network network, NodeId node, int index, Signal signal) {
    ChannelColor color = COLORS[index];
    return new SignalChange(
        clock.now(),
        network.node(node).orElseThrow().displayName(),
        color,
        network.channel(color).name(),
        Direction.INPUT,
        signal);
  }
}
