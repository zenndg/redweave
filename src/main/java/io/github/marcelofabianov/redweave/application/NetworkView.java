package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NetworkState;
import io.github.marcelofabianov.redweave.domain.network.NodeState;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.List;
import java.util.Objects;

/**
 * What the Controller Screen shows of a network, for one viewer: the header, the node list and the
 * 16 channels, from which the footer counts, and the Signal Remotes that count now (telas.md and
 * answer 364).
 */
public record NetworkView(
    String name,
    ChannelColor color,
    PlayerId owner,
    NetworkState state,
    boolean viewerIsOwner,
    List<NodeRow> nodes,
    List<ChannelRow> channels,
    int remotesOn,
    int modulesOn) {
  private static final String MISSING = "A network view needs a name, color, owner and state";

  public NetworkView {
    Objects.requireNonNull(name, MISSING);
    Objects.requireNonNull(color, MISSING);
    Objects.requireNonNull(owner, MISSING);
    Objects.requireNonNull(state, MISSING);
    nodes = List.copyOf(nodes);
    channels = List.copyOf(channels);
  }

  /** Channels with at least one slot, as in "3 of 16 channels in use". */
  public int channelsInUse() {
    return (int) channels.stream().filter(ChannelRow::inUse).count();
  }

  /** How many nodes are in a state, for the Unloaded and Out of range counts of the footer. */
  public int nodesIn(NodeState state) {
    return (int) nodes.stream().filter(row -> row.state() == state).count();
  }
}
