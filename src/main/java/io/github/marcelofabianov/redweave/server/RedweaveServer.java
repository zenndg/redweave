package io.github.marcelofabianov.redweave.server;

import io.github.marcelofabianov.redweave.application.BreakController;
import io.github.marcelofabianov.redweave.application.BreakNode;
import io.github.marcelofabianov.redweave.application.ClearLog;
import io.github.marcelofabianov.redweave.application.ConfigureSlot;
import io.github.marcelofabianov.redweave.application.CopyNode;
import io.github.marcelofabianov.redweave.application.CreateNetwork;
import io.github.marcelofabianov.redweave.application.DisableSlot;
import io.github.marcelofabianov.redweave.application.EnableSlot;
import io.github.marcelofabianov.redweave.application.EvaluateNetworks;
import io.github.marcelofabianov.redweave.application.LinkNode;
import io.github.marcelofabianov.redweave.application.LoseController;
import io.github.marcelofabianov.redweave.application.LoseNode;
import io.github.marcelofabianov.redweave.application.NetworkChoices;
import io.github.marcelofabianov.redweave.application.PasteNode;
import io.github.marcelofabianov.redweave.application.PlaceController;
import io.github.marcelofabianov.redweave.application.PlaceNode;
import io.github.marcelofabianov.redweave.application.ReadInput;
import io.github.marcelofabianov.redweave.application.RecolorNetwork;
import io.github.marcelofabianov.redweave.application.RenameChannel;
import io.github.marcelofabianov.redweave.application.RenameNetwork;
import io.github.marcelofabianov.redweave.application.RenameNode;
import io.github.marcelofabianov.redweave.application.ServerClock;
import io.github.marcelofabianov.redweave.application.ShowNetwork;
import io.github.marcelofabianov.redweave.application.SignalLogs;
import io.github.marcelofabianov.redweave.application.ToggleSlots;
import io.github.marcelofabianov.redweave.config.NeoForgeLogLimits;
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
  private final PlaceNode placeNode;
  private final ReadInput readInput;
  private final BreakNode breakNode;
  private final LoseNode loseNode;
  private final EvaluateNetworks evaluateNetworks;
  private final EnableSlot enableSlot;
  private final ConfigureSlot configureSlot;
  private final DisableSlot disableSlot;
  private final LinkNode linkNode;
  private final NetworkChoices networkChoices;
  private final RenameNode renameNode;
  private final CopyNode copyNode;
  private final PasteNode pasteNode;
  private final ToggleSlots toggleSlots;
  private final ShowNetwork showNetwork;
  private final RenameNetwork renameNetwork;
  private final RecolorNetwork recolorNetwork;
  private final RenameChannel renameChannel;
  private final SignalLogs signalLogs;
  private final TerminalServices terminal;
  private final RemoteServices remotes;
  private final ModuleServices modules;
  private final LabelWatch labelWatch = new LabelWatch(this);
  private final RandomIds ids = new RandomIds();

  private RedweaveServer(NetworkLimits limits, SavedDataNetworks networks) {
    this.networks = networks;
    CreateNetwork create = new CreateNetwork(ids, networks, networks, limits);
    placeController = new PlaceController(create, networks, networks);
    breakController = new BreakController(networks, networks);
    loseController = new LoseController(networks, networks);
    placeNode = new PlaceNode(ids);
    signalLogs = new SignalLogs(new NeoForgeLogLimits());
    ServerClock clock = new SystemServerClock();
    readInput = new ReadInput(networks, networks, signalLogs, clock);
    terminal = new TerminalServices(networks, signalLogs);
    remotes = new RemoteServices(networks);
    modules = new ModuleServices(ids, networks, signalLogs, clock);
    breakNode = new BreakNode(networks, networks);
    loseNode = new LoseNode(networks, networks);
    evaluateNetworks = new EvaluateNetworks(networks, new Simulator(), signalLogs, clock);
    enableSlot = new EnableSlot(networks, networks);
    configureSlot = new ConfigureSlot(networks, networks);
    disableSlot = new DisableSlot(networks, networks);
    linkNode = new LinkNode(networks, networks, networks, networks);
    networkChoices = new NetworkChoices(networks, networks);
    renameNode = new RenameNode(networks, networks);
    copyNode = new CopyNode(networks);
    pasteNode = new PasteNode(networks, networks);
    toggleSlots = new ToggleSlots(networks, networks);
    showNetwork = new ShowNetwork(networks);
    renameNetwork = new RenameNetwork(networks, networks);
    recolorNetwork = new RecolorNetwork(networks, networks);
    renameChannel = new RenameChannel(networks, networks);
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

  public EnableSlot enableSlot() {
    return enableSlot;
  }

  public ConfigureSlot configureSlot() {
    return configureSlot;
  }

  public DisableSlot disableSlot() {
    return disableSlot;
  }

  public LinkNode linkNode() {
    return linkNode;
  }

  public NetworkChoices networkChoices() {
    return networkChoices;
  }

  public RenameNode renameNode() {
    return renameNode;
  }

  public CopyNode copyNode() {
    return copyNode;
  }

  public PasteNode pasteNode() {
    return pasteNode;
  }

  public ToggleSlots toggleSlots() {
    return toggleSlots;
  }

  public ShowNetwork showNetwork() {
    return showNetwork;
  }

  public RenameNetwork renameNetwork() {
    return renameNetwork;
  }

  public RecolorNetwork recolorNetwork() {
    return recolorNetwork;
  }

  public RenameChannel renameChannel() {
    return renameChannel;
  }

  /** The log of each network, only in memory, for the Controller Screen (answer 147). */
  public SignalLogs signalLogs() {
    return signalLogs;
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

  /** The labels over the nodes of each player with the Configurator in hand (answer 269). */
  public LabelWatch labelWatch() {
    return labelWatch;
  }

  /** The owner clears the log of a network (answer 264). */
  public ClearLog clearLog() {
    return new ClearLog(networks, signalLogs);
  }

  /** The use cases of the Network Terminal (terminal-de-rede.md). */
  public TerminalServices terminal() {
    return terminal;
  }

  public Optional<NetworkId> lastUsedOf(PlayerId player) {
    return networks.lastUsedOf(player);
  }

  public RemoteServices remotes() {
    return remotes;
  }

  public ModuleServices modules() {
    return modules;
  }
}
