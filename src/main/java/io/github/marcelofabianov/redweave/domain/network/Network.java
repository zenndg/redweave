package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
  private final Channels channels = new Channels();
  private final Nodes nodes = new Nodes();
  private final Readings readings = new Readings();
  private final Emissions emissions = new Emissions();
  private String name;
  private ChannelColor color;
  private final NetworkStatus status;
  private final NetworkSignals signals;
  private final SlotEdits slotEdits;

  private Network(
      NetworkId id,
      PlayerId owner,
      String name,
      ChannelColor color,
      NetworkStatus status,
      NetworkLimits limits) {
    this.id = id;
    this.owner = owner;
    this.name = name;
    this.color = color;
    this.status = status;
    this.signals = new NetworkSignals(nodes, readings, channels, status, limits);
    this.slotEdits = new SlotEdits(nodes, readings, emissions);
    this.limits = limits;
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
    NetworkStatus status = new NetworkStatus(Optional.of(controller), false);
    return new Network(id, owner, valid, color, status, limits);
  }

  /**
   * A network loaded from the saves, as it was, waiting for the first tick to apply every output
   * again (answers 38 and 90). A name keeps its length if the config now allows less (answer 93).
   */
  public static Network restore(NetworkSnapshot snapshot, NetworkLimits limits) {
    SnapshotRules.check(snapshot);
    Network network =
        new Network(
            snapshot.id(),
            snapshot.owner(),
            snapshot.name(),
            snapshot.color(),
            new NetworkStatus(snapshot.controller(), snapshot.offline()),
            limits);
    snapshot.channels().forEach(network.channels::restore);
    snapshot.nodes().forEach(network.nodes::restore);
    snapshot.readings().forEach(network.readings::restore);
    return network;
  }

  /** Everything that goes to the saves, without what the network last emitted (answer 38). */
  public NetworkSnapshot snapshot() {
    return new NetworkSnapshot(
        id,
        owner,
        name,
        color,
        status.controller(),
        status.isOff(),
        channels.named(),
        nodes(),
        readings.all());
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

  /** Names a channel, for the owner only, or goes back to the name of the color (answer 34). */
  public Outcome renameChannel(PlayerId requester, ChannelColor channelColor, String newName) {
    if (!isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    return channels.rename(channelColor, newName, limits.channelNameLength());
  }

  /**
   * Attaches a node with every slot free, for the owner only and while online (answer 157). An
   * empty name is the default one, and a node that moves in keeps its own (answers 200 and 206).
   */
  public Outcome attachNode(
      PlayerId requester, NodeId nodeId, NodePosition position, Optional<String> nodeName) {
    if (!isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    if (!isOnline()) {
      return Outcome.NETWORK_NOT_ONLINE;
    }
    return pendingIfAccepted(nodes.attach(nodeId, position, nodeName));
  }

  /** Names a node, for the owner only; blank gives back the default (144). It changes no signal. */
  public Outcome renameNode(PlayerId requester, NodeId nodeId, String newName) {
    if (!isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    return nodes.rename(nodeId, newName, limits.nodeNameLength());
  }

  /** Detaches a node, for the owner only. */
  public Outcome detachNode(PlayerId requester, NodeId nodeId) {
    if (!isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    return loseNode(nodeId);
  }

  /**
   * The node left the world without its owner taking it, as when its block is gone or an explosion
   * takes it. There is nobody to ask, so it leaves anyway (answer 74).
   */
  public Outcome loseNode(NodeId nodeId) {
    if (!nodes.remove(nodeId)) {
      return Outcome.UNKNOWN_NODE;
    }
    readings.forgetNode(nodeId);
    emissions.markPending();
    return Outcome.ACCEPTED;
  }

  /**
   * Takes every node out, for the owner only, in any state, at a Network Terminal: the nodes stay
   * in the world with no network, and their slots and readings go (answers 133 and 176).
   */
  public Outcome clearNodes(PlayerId requester) {
    if (!isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    nodes.all().stream().map(Node::id).toList().forEach(this::loseNode);
    return Outcome.ACCEPTED;
  }

  /**
   * Whether the owner may delete the network at a Network Terminal: only an unplaced one goes, so a
   * network with its controller in the world never does (answer 166). The saves remove it.
   */
  public Outcome deletion(PlayerId requester) {
    if (!isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    return state() == NetworkState.UNPLACED ? Outcome.ACCEPTED : Outcome.NETWORK_NOT_UNPLACED;
  }

  /** Configures one slot of a node, for the owner only. */
  public Outcome configureSlot(PlayerId requester, NodeId nodeId, int index, ChannelSlot slot) {
    return isOwner(requester)
        ? slotEdits.change(nodeId, index, Optional.of(slot))
        : Outcome.NOT_OWNER;
  }

  /** Enables the slot of a color, for the owner only (answers 226 and 228). */
  public Outcome enableSlot(PlayerId requester, NodeId nodeId, int index) {
    return isOwner(requester)
        ? slotEdits.changeWith(nodeId, index, SlotChanges::enable)
        : Outcome.NOT_OWNER;
  }

  /** Disables a slot, which keeps its configuration, for the owner only (answer 228). */
  public Outcome disableSlot(PlayerId requester, NodeId nodeId, int index) {
    return isOwner(requester)
        ? slotEdits.changeWith(nodeId, index, SlotChanges::disable)
        : Outcome.NOT_OWNER;
  }

  /** Sets up the direction and face of a slot that is not enabled, for the owner (answer 237). */
  public Outcome setUpSlot(PlayerId requester, NodeId nodeId, int index, Direction way, Face face) {
    return isOwner(requester) ? slotEdits.setUp(nodeId, index, way, face) : Outcome.NOT_OWNER;
  }

  /** Names a slot, for the owner only; blank takes the name away. It changes no signal (227). */
  public Outcome renameSlot(PlayerId requester, NodeId nodeId, int index, String newName) {
    return isOwner(requester)
        ? slotEdits.rename(nodeId, index, newName, limits.slotNameLength())
        : Outcome.NOT_OWNER;
  }

  /** Frees one slot of a node, for the owner only. */
  public Outcome clearSlot(PlayerId requester, NodeId nodeId, int index) {
    return isOwner(requester)
        ? slotEdits.change(nodeId, index, Optional.empty())
        : Outcome.NOT_OWNER;
  }

  /** The owner placed the controller again: the network comes back online there. */
  public Outcome placeController(PlayerId requester, BlockLocation location) {
    if (!isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    return pendingIfAccepted(status.place(location));
  }

  /** The owner broke the controller: the network is unplaced until it is placed again (146). */
  public Outcome removeController(PlayerId requester) {
    if (!isOwner(requester)) {
      return Outcome.NOT_OWNER;
    }
    loseController();
    return Outcome.ACCEPTED;
  }

  /**
   * The controller left the world without its owner breaking it, as in creative, by a command or by
   * another mod. There is nobody to ask, so the network is just unplaced (answers 49 and 146).
   */
  public void loseController() {
    status.remove();
    emissions.markPending();
  }

  /** The owner turns an online network off, at a Network Terminal (answers 132 and 209). */
  public Outcome turnOff(PlayerId requester) {
    return isOwner(requester) ? pendingIfAccepted(status.turn(false)) : Outcome.NOT_OWNER;
  }

  /** The owner turns an offline network back on; an unplaced one waits for its controller. */
  public Outcome turnOn(PlayerId requester) {
    return isOwner(requester) ? pendingIfAccepted(status.turn(true)) : Outcome.NOT_OWNER;
  }

  private Outcome pendingIfAccepted(Outcome outcome) {
    if (outcome == Outcome.ACCEPTED) {
      emissions.markPending();
    }
    return outcome;
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
    return status.controller();
  }

  public NetworkState state() {
    return status.state();
  }

  public boolean isOnline() {
    return status.state() == NetworkState.ONLINE;
  }

  /** The 16 channels, in the order of the colors. */
  public List<Channel> channels() {
    return channels.all();
  }

  public Channel channel(ChannelColor channelColor) {
    return channels.of(channelColor);
  }

  /** The nodes, in the order they were attached. */
  public List<Node> nodes() {
    return List.copyOf(nodes.all());
  }

  public Optional<Node> node(NodeId nodeId) {
    return Optional.ofNullable(nodes.at(nodeId));
  }

  /**
   * Keeps the last reading of an input slot, which stays while the chunk of the node is unloaded
   * and goes to the saves (answers 13 and 37). The server reads it, so there is no requester. A
   * slot that is not an input is ignored. Tells whether the reading changed.
   */
  public boolean readInput(NodeId nodeId, int index, Signal signal) {
    if (!nodes.isInput(nodeId, index)) {
      return false;
    }
    if (!readings.record(new SlotRef(nodeId, index), signal)) {
      return false;
    }
    emissions.markPending();
    return true;
  }

  /** The strongest of the inputs that count, and off with none (answers 23 and 24). */
  public Signal channelValue(ChannelColor channelColor) {
    return signals.channelValue(channelColor);
  }

  /** What an output slot emits into its block, or off when it does not count. */
  public Signal output(NodeId nodeId, int index) {
    return signals.output(nodeId, index);
  }

  /** The signal of one slot, for the meter of the Node Screen. */
  public Signal slotSignal(NodeId nodeId, int index) {
    return signals.slotSignal(nodeId, index);
  }

  /** What a node emits in the world: its strongest output, like a lever (answer 71). */
  public Signal emission(NodeId nodeId) {
    return signals.emission(nodeId);
  }

  /** The signal of a node: its strongest reading or emission, off when it does not count (98). */
  public Signal nodeSignal(NodeId nodeId) {
    return signals.nodeSignal(nodeId);
  }

  /** The state of a node for the screens; the caller tells whether its chunk is loaded. */
  public NodeState nodeState(NodeId nodeId, boolean loaded) {
    return signals.nodeState(nodeId, loaded);
  }

  /** Whether a signal of the network changed since the last evaluation (answer 38). */
  public boolean isPending() {
    return emissions.isPending();
  }

  /** The 16 channels in one pass over the nodes, for the simulator (_docs/limites.md, load). */
  public Map<ChannelColor, Signal> channelValues() {
    return signals.channelValues();
  }

  /** Whether a node counts: the network is online and the node is in range (answer 132). */
  public boolean counts(NodeId nodeId) {
    return signals.counts(nodeId);
  }

  /**
   * Called by the simulator with what every face should emit now. Gives back only the faces that
   * changed, and the network stops waiting for an evaluation.
   */
  public List<OutputChange> settle(Map<OutputFace, Signal> wanted) {
    return emissions.settle(wanted);
  }
}
