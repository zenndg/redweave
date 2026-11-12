package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Map;

/**
 * Inside the network: what each channel, slot and node is worth now, read from the parts of the
 * network without changing them. Off or unplaced, every channel is off (answers 132 and 146).
 */
final class NetworkSignals {
  private final Nodes nodes;
  private final Readings readings;
  private final Channels channels;
  private final NetworkStatus status;
  private final NetworkLimits limits;

  NetworkSignals(
      Nodes nodes,
      Readings readings,
      Channels channels,
      NetworkStatus status,
      NetworkLimits limits) {
    this.nodes = nodes;
    this.readings = readings;
    this.channels = channels;
    this.status = status;
    this.limits = limits;
  }

  private boolean isOnline() {
    return status.state() == NetworkState.ONLINE;
  }

  /**
   * The strongest of the inputs that count, and off with none. A network that is not online has
   * every channel off, and a node out of range does not count (answers 23 and 24).
   */
  Signal channelValue(ChannelColor channelColor) {
    return channelValues().get(channelColor);
  }

  private boolean inRange(Node node) {
    return status
        .controller()
        .map(location -> new Range(limits.range()).reaches(location, node.position().block()))
        .orElse(false);
  }

  /**
   * What an output slot emits into its block: its channel, or off when the node is out of range or
   * the network is not online. A slot that is not an output emits off.
   */
  Signal output(NodeId nodeId, int index) {
    Node node = nodes.at(nodeId);
    if (node == null || !inRange(node)) {
      return Signal.OFF;
    }
    return node.slotAt(index)
        .filter(ChannelSlot::emits)
        .map(slot -> channelValue(slot.channel()))
        .orElse(Signal.OFF);
  }

  /**
   * The signal of one slot, for the meter of the Node Screen: the last reading of an input and what
   * an output emits. A free slot shows 0.
   */
  Signal slotSignal(NodeId nodeId, int index) {
    Node node = nodes.at(nodeId);
    if (node == null || !node.hasSlot(index)) {
      return Signal.OFF;
    }
    if (nodes.isInput(nodeId, index)) {
      return readings.of(new SlotRef(nodeId, index));
    }
    return output(nodeId, index);
  }

  /**
   * What a node emits in the world: it strongly powers only the block it sticks to, so its
   * strongest output wins, whatever the face of each slot (answers 71 and 262).
   */
  Signal emission(NodeId nodeId) {
    Node node = nodes.at(nodeId);
    if (node == null) {
      return Signal.OFF;
    }
    Signal strongest = Signal.OFF;
    for (int index = 0; index < node.slots().size(); index++) {
      strongest = strongest.strongest(output(nodeId, index));
    }
    return strongest;
  }

  /**
   * The signal of a node, for its lens and the node list of the Controller Screen: the strongest of
   * its last readings and of what it emits, so an input receiving a signal lights it too. A node
   * that does not count is off (answers 98, 177 and 179).
   */
  Signal nodeSignal(NodeId nodeId) {
    Node node = nodes.at(nodeId);
    if (node == null || !counts(nodeId)) {
      return Signal.OFF;
    }
    Signal strongest = Signal.OFF;
    for (int index = 0; index < node.slots().size(); index++) {
      strongest = strongest.strongest(slotSignal(nodeId, index));
    }
    return strongest;
  }

  /**
   * The state of a node for the screens. The caller tells whether its chunk is loaded, because the
   * domain does not know the world. Out of range wins, because it is what stops the node counting.
   */
  NodeState nodeState(NodeId nodeId, boolean loaded) {
    Node node = nodes.at(nodeId);
    if (node == null || status.state() == NetworkState.UNPLACED) {
      return NodeState.NO_NETWORK;
    }
    if (status.state() == NetworkState.OFFLINE) {
      return NodeState.OFFLINE;
    }
    if (!inRange(node)) {
      return NodeState.OUT_OF_RANGE;
    }
    return loaded ? NodeState.ONLINE : NodeState.UNLOADED;
  }

  /** The 16 channels in one pass over the nodes, for the simulator (_docs/limites.md, load). */
  Map<ChannelColor, Signal> channelValues() {
    Map<ChannelColor, Signal> values = channels.allOff();
    if (!isOnline()) {
      return values;
    }
    for (Node node : nodes.all()) {
      if (inRange(node)) {
        readings.addInputsOf(node, values);
      }
    }
    return values;
  }

  /** Whether a node counts: the network is online and the node is in range (answer 132). */
  boolean counts(NodeId nodeId) {
    Node node = nodes.at(nodeId);
    return node != null && isOnline() && inRange(node);
  }
}
