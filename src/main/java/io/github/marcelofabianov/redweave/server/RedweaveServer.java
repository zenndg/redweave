package io.github.marcelofabianov.redweave.server;

import io.github.marcelofabianov.redweave.application.BreakController;
import io.github.marcelofabianov.redweave.application.BreakNode;
import io.github.marcelofabianov.redweave.application.ClearSlot;
import io.github.marcelofabianov.redweave.application.ConfigureSlot;
import io.github.marcelofabianov.redweave.application.CopyNode;
import io.github.marcelofabianov.redweave.application.CreateNetwork;
import io.github.marcelofabianov.redweave.application.EvaluateNetworks;
import io.github.marcelofabianov.redweave.application.LinkNode;
import io.github.marcelofabianov.redweave.application.LoseController;
import io.github.marcelofabianov.redweave.application.LoseNode;
import io.github.marcelofabianov.redweave.application.OpenNetwork;
import io.github.marcelofabianov.redweave.application.PasteNode;
import io.github.marcelofabianov.redweave.application.PlaceController;
import io.github.marcelofabianov.redweave.application.PlaceNode;
import io.github.marcelofabianov.redweave.application.ReadInput;
import io.github.marcelofabianov.redweave.application.UseFreeSlot;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NetworkLimits;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.simulation.Simulator;
import java.util.List;
import java.util.Optional;

/**
 * The state of one running server and the use cases over it. It is born when the world opens, with
 * the networks of its save, and dropped when it closes, so nothing leaks from one world to the next
 * (answers 63 and 88).
 */
public final class RedweaveServer {
  private static RedweaveServer running;

  private final SavedDataNetworks networks;
  private final PlaceController placeController;
  private final BreakController breakController;
  private final LoseController loseController;
  private final OpenNetwork openNetwork;
  private final PlaceNode placeNode;
  private final ReadInput readInput;
  private final BreakNode breakNode;
  private final LoseNode loseNode;
  private final EvaluateNetworks evaluateNetworks;
  private final UseFreeSlot useFreeSlot;
  private final ConfigureSlot configureSlot;
  private final ClearSlot clearSlot;
  private final LinkNode linkNode;
  private final CopyNode copyNode;
  private final PasteNode pasteNode;

  private RedweaveServer(NetworkLimits limits, SavedDataNetworks networks) {
    this.networks = networks;
    RandomIds ids = new RandomIds();
    CreateNetwork create = new CreateNetwork(ids, networks, networks, networks, limits);
    placeController = new PlaceController(create, networks, networks);
    breakController = new BreakController(networks, networks);
    loseController = new LoseController(networks, networks);
    openNetwork = new OpenNetwork(networks, networks);
    placeNode = new PlaceNode(ids, networks, networks, networks);
    readInput = new ReadInput(networks, networks);
    breakNode = new BreakNode(networks, networks);
    loseNode = new LoseNode(networks, networks);
    evaluateNetworks = new EvaluateNetworks(networks, new Simulator());
    useFreeSlot = new UseFreeSlot(networks, networks);
    configureSlot = new ConfigureSlot(networks, networks);
    clearSlot = new ClearSlot(networks, networks);
    linkNode = new LinkNode(networks, networks, networks);
    copyNode = new CopyNode(networks);
    pasteNode = new PasteNode(networks, networks);
  }

  /** The server that is running now, or none, as on a client that only shows the world. */
  public static Optional<RedweaveServer> running() {
    return Optional.ofNullable(running);
  }

  static void start(NetworkLimits limits, SavedDataNetworks networks) {
    running = new RedweaveServer(limits, networks);
  }

  static void stop() {
    running = null;
  }

  public PlaceController placeController() {
    return placeController;
  }

  public BreakController breakController() {
    return breakController;
  }

  public LoseController loseController() {
    return loseController;
  }

  public OpenNetwork openNetwork() {
    return openNetwork;
  }

  public PlaceNode placeNode() {
    return placeNode;
  }

  public ReadInput readInput() {
    return readInput;
  }

  public BreakNode breakNode() {
    return breakNode;
  }

  public LoseNode loseNode() {
    return loseNode;
  }

  public EvaluateNetworks evaluateNetworks() {
    return evaluateNetworks;
  }

  public UseFreeSlot useFreeSlot() {
    return useFreeSlot;
  }

  public ConfigureSlot configureSlot() {
    return configureSlot;
  }

  public ClearSlot clearSlot() {
    return clearSlot;
  }

  public LinkNode linkNode() {
    return linkNode;
  }

  public CopyNode copyNode() {
    return copyNode;
  }

  public PasteNode pasteNode() {
    return pasteNode;
  }

  /** The networks waiting for the next tick, read before it evaluates them. */
  public List<Network> pendingNetworks() {
    return networks.all().stream().filter(Network::isPending).toList();
  }

  public Optional<Network> networkOfNode(NodeId node) {
    return networks.findByNode(node);
  }

  public Optional<Network> network(NetworkId id) {
    return networks.find(id);
  }

  public Optional<NetworkId> currentOf(PlayerId player) {
    return networks.currentOf(player);
  }
}
