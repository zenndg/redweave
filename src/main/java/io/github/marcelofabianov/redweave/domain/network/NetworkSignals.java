package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Inside the network: what each channel, slot and node is worth now, read from the parts of the
 * network without changing them. Off or unplaced, every channel is off (answers 132 and 146).
 */
final class NetworkSignals {
  private final Nodes nodes;
  private final Readings readings;
  private final RemoteInputs remotes;
  private final ModuleInputs modules;
  private final Channels channels;
  private final NetworkStatus status;
  private final NetworkLimits limits;

  NetworkSignals(
      Nodes nodes,
      Readings readings,
      RemoteInputs remotes,
      ModuleInputs modules,
      Channels channels,
      NetworkStatus status,
      NetworkLimits limits) {
    this.nodes = nodes;
    this.readings = readings;
    this.remotes = remotes;
    this.modules = modules;
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
    return reaches(node.position().block());
  }

  /** Whether a place is in the range of the controller, in its dimension (answers 23 and 354). */
  boolean reaches(BlockLocation where) {
    return status
        .controller()
        .map(location -> new Range(limits.range()).reaches(location, where))
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
  /** A registered module, with the rules of a node, by the place of its plate (answer 809). */
  NodeState placeState(Optional<BlockLocation> place, boolean loaded) {
    if (place.isEmpty() || status.state() == NetworkState.UNPLACED) {
      return NodeState.NO_NETWORK;
    }
    if (status.state() == NetworkState.OFFLINE) {
      return NodeState.OFFLINE;
    }
    if (!reaches(place.get())) {
      return NodeState.OUT_OF_RANGE;
    }
    return loaded ? NodeState.ONLINE : NodeState.UNLOADED;
  }

  /** What a module writes this tick, or none (answer 814). */
  Optional<Signal> moduleSignal(ModuleId module) {
    return modules.of(module);
  }

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
    return channelValues(input -> true);
  }

  /** The channels with only the tablets that pass, as without one tablet (answer 370). */
  Map<ChannelColor, Signal> channelValues(Predicate<RemoteInput> counts) {
    Map<ChannelColor, Signal> values = channels.allOff();
    if (!isOnline()) {
      return values;
    }
    for (Node node : nodes.all()) {
      if (inRange(node)) {
        readings.addInputsOf(node, values);
      }
    }
    remotes.addTo(nodes, values, counts);
    modules.addTo(values, this::reaches);
    return values;
  }

  /** The Signal Remotes that count now, for the Controller Screen; none when not online (364). */
  int remotesOn() {
    return isOnline() ? remotes.count(nodes) : 0;
  }

  /** The Signal Modules that count now, for the Controller Screen; none when not online (424). */
  int modulesOn() {
    return isOnline() ? modules.count(this::reaches) : 0;
  }

  /** Whether a node counts: the network is online and the node is in range (answer 132). */
  boolean counts(NodeId nodeId) {
    Node node = nodes.at(nodeId);
    return node != null && isOnline() && inRange(node);
  }
}
