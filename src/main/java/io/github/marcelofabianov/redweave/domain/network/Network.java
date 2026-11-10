package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The aggregate: every change to the name, the channels and the nodes goes through it, and it keeps
 * the rules of _docs/funcionalidades/rede-e-canais.md. It is mutable, and its queries only hand out
 * immutable values (answer 33).
 */
public final class Network {
  private static final String INVALID_DEFAULT_NAME = "The default name of a network must be valid";

  private final NetworkId id;
  private final PlayerId owner;
  private final NetworkLimits limits;
  private final Map<ChannelColor, Channel> channels = new EnumMap<>(ChannelColor.class);
  private final Map<NodeId, Node> nodes = new LinkedHashMap<>();
  private final Map<SlotRef, Signal> readings = new HashMap<>();
  private final Emissions emissions = new Emissions();
  private String name;
  private ChannelColor color;
  private Optional<BlockLocation> controller;

  private Network(
      NetworkId id,
      PlayerId owner,
      String name,
      ChannelColor color,
      BlockLocation controller,
      NetworkLimits limits) {
    this.id = id;
    this.owner = owner;
    this.name = name;
    this.color = color;
    this.controller = Optional.of(controller);
    this.limits = limits;
    for (ChannelColor channelColor : ChannelColor.values()) {
      channels.put(channelColor, Channel.unnamed(channelColor));
    }
  }

  /**
   * A network is created when its owner places a Network Controller, so it starts online. The
   * caller builds the default name (answer 35), so an invalid one is a programming error.
   */
  public static Network create(
      NetworkId id,
      PlayerId owner,
      String name,
      ChannelColor color,
      BlockLocation controller,
      NetworkLimits limits) {
    String valid =
        Names.valid(name, limits.networkNameLength())
            .orElseThrow(() -> new IllegalArgumentException(INVALID_DEFAULT_NAME));
    return new Network(id, owner, valid, color, controller, limits);
  }

  /** Renames the network, for its owner only. */
  public Outcome rename(PlayerId requester, String newName) {
    if (!isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    Optional<String> valid = Names.valid(newName, limits.networkNameLength());
    if (valid.isEmpty()) {
      return Outcome.INVALID_NAME;
    }
    name = valid.get();
    return Outcome.ACCEPTED;
  }

  /** Changes the color of the network, for its owner only. */
  public Outcome recolor(PlayerId requester, ChannelColor newColor) {
    if (!isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    color = newColor;
    return Outcome.ACCEPTED;
  }

  /**
   * Names a channel, for the owner only. A blank name goes back to the name of the color, because
   * the name of a channel is optional (answers 26 and 34).
   */
  public Outcome renameChannel(PlayerId requester, ChannelColor channelColor, String newName) {
    if (!isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    if (newName.isBlank()) {
      channels.put(channelColor, Channel.unnamed(channelColor));
      return Outcome.ACCEPTED;
    }
    Optional<String> valid = Names.valid(newName, limits.channelNameLength());
    if (valid.isEmpty()) {
      return Outcome.INVALID_NAME;
    }
    channels.put(channelColor, new Channel(channelColor, valid));
    return Outcome.ACCEPTED;
  }

  /** Attaches a node with every slot free, for the owner only. */
  public Outcome attachNode(PlayerId requester, NodeId nodeId, NodePosition position) {
    if (!isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    if (nodes.containsKey(nodeId)) {
      return Outcome.ALREADY_ATTACHED;
    }
    nodes.put(nodeId, Node.withFreeSlots(nodeId, position, limits.slotsPerNode()));
    emissions.markPending();
    return Outcome.ACCEPTED;
  }

  /** Detaches a node, for the owner only. */
  public Outcome detachNode(PlayerId requester, NodeId nodeId) {
    if (!isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    if (nodes.remove(nodeId) == null) {
      return Outcome.UNKNOWN_NODE;
    }
    readings.keySet().removeIf(slot -> slot.node().equals(nodeId));
    emissions.markPending();
    return Outcome.ACCEPTED;
  }

  /** Configures one slot of a node, for the owner only. */
  public Outcome configureSlot(PlayerId requester, NodeId nodeId, int index, ChannelSlot slot) {
    return changeSlot(requester, nodeId, index, Optional.of(slot));
  }

  /** Frees one slot of a node, for the owner only. */
  public Outcome clearSlot(PlayerId requester, NodeId nodeId, int index) {
    return changeSlot(requester, nodeId, index, Optional.empty());
  }

  private Outcome changeSlot(
      PlayerId requester, NodeId nodeId, int index, Optional<ChannelSlot> slot) {
    if (!isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    Node node = nodes.get(nodeId);
    if (node == null) {
      return Outcome.UNKNOWN_NODE;
    }
    if (!node.hasSlot(index)) {
      return Outcome.INVALID_SLOT;
    }
    nodes.put(nodeId, node.withSlot(index, slot));
    readings.remove(new SlotRef(nodeId, index));
    emissions.markPending();
    return Outcome.ACCEPTED;
  }

  /** The owner placed the controller again: the network comes back online there. */
  public Outcome placeController(PlayerId requester, BlockLocation location) {
    if (!isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    if (controller.isPresent()) {
      return Outcome.ALREADY_PLACED;
    }
    controller = Optional.of(location);
    emissions.markPending();
    return Outcome.ACCEPTED;
  }

  /** The owner broke the controller: the network goes offline until it is placed again. */
  public Outcome removeController(PlayerId requester) {
    if (!isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    loseController();
    return Outcome.ACCEPTED;
  }

  /**
   * The controller left the world without its owner breaking it, as in creative, by a command or by
   * another mod. There is nobody to ask, so the network just goes offline (answer 49).
   */
  public void loseController() {
    controller = Optional.empty();
    emissions.markPending();
  }

  /** Only the owner changes the network, with no exception for operators (answers 22 and 28). */
  public boolean isOwner(PlayerId player) {
    return owner.equals(player);
  }

  public NetworkId id() {
    return id;
  }

  public PlayerId owner() {
    return owner;
  }

  public String name() {
    return name;
  }

  public ChannelColor color() {
    return color;
  }

  public Optional<BlockLocation> controller() {
    return controller;
  }

  public boolean isOnline() {
    return controller.isPresent();
  }

  /** The 16 channels, in the order of the colors. */
  public List<Channel> channels() {
    return List.copyOf(channels.values());
  }

  public Channel channel(ChannelColor channelColor) {
    return channels.get(channelColor);
  }

  /** The nodes, in the order they were attached. */
  public List<Node> nodes() {
    return List.copyOf(nodes.values());
  }

  public Optional<Node> node(NodeId nodeId) {
    return Optional.ofNullable(nodes.get(nodeId));
  }

  /**
   * The first channel, in the order of the colors, with no slot in the network. A node placed by
   * its owner starts on it (answer 25).
   */
  public Optional<ChannelColor> firstFreeChannel() {
    Set<ChannelColor> used =
        nodes.values().stream()
            .flatMap(node -> node.slots().stream())
            .flatMap(Optional::stream)
            .map(ChannelSlot::channel)
            .collect(Collectors.toSet());
    return channels.keySet().stream().filter(channel -> !used.contains(channel)).findFirst();
  }

  /**
   * Keeps the last reading of an input slot, which stays while the chunk of the node is unloaded
   * and goes to the saves (answers 13 and 37). The server reads it, so there is no requester. A
   * slot that is not an input is ignored. Tells whether the reading changed.
   */
  public boolean readInput(NodeId nodeId, int index, Signal signal) {
    if (!isInput(nodeId, index)) {
      return false;
    }
    Signal previous = readings.getOrDefault(new SlotRef(nodeId, index), Signal.OFF);
    readings.put(new SlotRef(nodeId, index), signal);
    if (previous.equals(signal)) {
      return false;
    }
    emissions.markPending();
    return true;
  }

  /**
   * The strongest of the inputs that count, and off with none. An offline network has every channel
   * off, and a node out of range does not count (answers 23 and 24).
   */
  public Signal channelValue(ChannelColor channelColor) {
    return channelValues().get(channelColor);
  }

  private boolean inRange(Node node) {
    return controller
        .map(location -> new Range(limits.range()).reaches(location, node.position().block()))
        .orElse(false);
  }

  /**
   * What an output slot emits into its block: its channel, or off when the node is out of range or
   * the network is offline. A slot that is not an output emits off.
   */
  public Signal output(NodeId nodeId, int index) {
    Node node = nodes.get(nodeId);
    if (node == null || !node.hasSlot(index) || !inRange(node)) {
      return Signal.OFF;
    }
    return node.slots()
        .get(index)
        .filter(slot -> slot.direction() == Direction.OUTPUT)
        .map(slot -> channelValue(slot.channel()))
        .orElse(Signal.OFF);
  }

  /**
   * The state of a node for the screens. The caller tells whether its chunk is loaded, because the
   * domain does not know the world. Out of range wins, because it is what stops the node counting.
   */
  public NodeState nodeState(NodeId nodeId, boolean loaded) {
    Node node = nodes.get(nodeId);
    if (node == null || !isOnline()) {
      return NodeState.NO_NETWORK;
    }
    if (!inRange(node)) {
      return NodeState.OUT_OF_RANGE;
    }
    return loaded ? NodeState.ONLINE : NodeState.UNLOADED;
  }

  private boolean isInput(NodeId nodeId, int index) {
    Node node = nodes.get(nodeId);
    return node != null
        && node.hasSlot(index)
        && node.slots().get(index).filter(slot -> slot.direction() == Direction.INPUT).isPresent();
  }

  /** Whether a signal of the network changed since the last evaluation (answer 38). */
  public boolean isPending() {
    return emissions.isPending();
  }

  /** The 16 channels in one pass over the nodes, for the simulator (_docs/limites.md, load). */
  public Map<ChannelColor, Signal> channelValues() {
    Map<ChannelColor, Signal> values = new EnumMap<>(ChannelColor.class);
    channels.keySet().forEach(channelColor -> values.put(channelColor, Signal.OFF));
    if (!isOnline()) {
      return values;
    }
    for (Node node : nodes.values()) {
      if (inRange(node)) {
        addInputs(node, values);
      }
    }
    return values;
  }

  private void addInputs(Node node, Map<ChannelColor, Signal> values) {
    for (int index = 0; index < node.slots().size(); index++) {
      Optional<ChannelSlot> slot = node.slots().get(index);
      if (slot.isPresent() && slot.get().direction() == Direction.INPUT) {
        Signal read = readings.getOrDefault(new SlotRef(node.id(), index), Signal.OFF);
        values.merge(slot.get().channel(), read, Signal::strongest);
      }
    }
  }

  /** Whether a node counts: the network is online and the node is in range. */
  public boolean counts(NodeId nodeId) {
    Node node = nodes.get(nodeId);
    return node != null && inRange(node);
  }

  /**
   * Called by the simulator with what every face should emit now. Gives back only the faces that
   * changed, and the network stops waiting for an evaluation.
   */
  public List<OutputChange> settle(Map<OutputFace, Signal> wanted) {
    return emissions.settle(wanted);
  }
}
