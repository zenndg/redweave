package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.application.NetworkView;
import io.github.marcelofabianov.redweave.block.NodeBlocks;
import io.github.marcelofabianov.redweave.block.PlayerNames;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.NodeState;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.network.SignalLog;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.registry.RedweaveMenus;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * The menu behind the Controller Screen: telas.md. Anyone may open it, and only the owner changes
 * the network, which the domain checks (answer 22). The client names a node by its row in the list
 * this menu keeps, never by its ID (rule 17). The game only delivers a click while the menu holds:
 * within reach of the controller and with the network online (answer 158).
 */
public final class ControllerMenu extends AbstractContainerMenu {
  private static final long NO_LINE_SEEN = 0;

  private final BlockPos pos;
  private final ContainerLevelAccess access;
  private final Block anchor;
  private final Optional<ServerPlayer> viewer;
  private final Optional<NetworkId> network;
  private ControllerScreenRows rows;
  private ControllerScreenSignals signals;
  private ControllerScreenLog log;
  private long logSeen;
  private long logClears;
  private Optional<NetworkView> view;

  /** On the server, for a player who opened the controller of an online network. */
  public ControllerMenu(int id, ServerPlayer player, BlockPos pos, NetworkId network) {
    this(id, player, pos, network, RedweaveBlocks.NETWORK_CONTROLLER.get());
  }

  /**
   * Opened by the Open of a Network Terminal: the screen holds while the owner is within reach of
   * the terminal, wherever the controller is, and its actions name the terminal (answer 259).
   */
  public static ControllerMenu atTerminal(
      int id, ServerPlayer player, BlockPos terminal, NetworkId network) {
    return new ControllerMenu(id, player, terminal, network, RedweaveBlocks.NETWORK_TERMINAL.get());
  }

  /** Opens the screen of a network from a Network Terminal; Esc closes it all (answer 260). */
  public static void openAtTerminal(ServerPlayer viewer, BlockPos terminal, Network network) {
    viewer.openMenu(
        new SimpleMenuProvider(
            (id, inventory, opener) -> atTerminal(id, viewer, terminal, network.id()),
            Component.literal(network.name())),
        extra -> writeOpening(extra, viewer, terminal, network.id()));
  }

  private ControllerMenu(
      int id, ServerPlayer player, BlockPos pos, NetworkId network, Block anchor) {
    super(RedweaveMenus.CONTROLLER.get(), id);
    this.pos = pos;
    this.access = ContainerLevelAccess.create(player.level(), pos);
    this.anchor = anchor;
    this.viewer = Optional.of(player);
    this.network = Optional.of(network);
    this.view = read(player, network);
    this.rows = rowsOf(player, view, id);
    this.signals = signalsOf(view, id);
    this.log = ControllerScreenLog.NONE;
    this.logSeen = lastSequenceOf(logOf(network, NO_LINE_SEEN));
    this.logClears = clearsOf(network);
  }

  /** On the client, with the position and the rows the server sent when opening. */
  public ControllerMenu(int id, Inventory inventory, RegistryFriendlyByteBuf extra) {
    super(RedweaveMenus.CONTROLLER.get(), id);
    this.pos = extra.readBlockPos();
    this.access = ContainerLevelAccess.NULL;
    this.anchor = RedweaveBlocks.NETWORK_CONTROLLER.get();
    this.viewer = Optional.empty();
    this.network = Optional.empty();
    this.view = Optional.empty();
    this.rows = ControllerScreenRows.STREAM_CODEC.decode(extra).forMenu(id);
    this.signals = ControllerScreenSignals.STREAM_CODEC.decode(extra).forMenu(id);
    this.log = ControllerScreenLog.STREAM_CODEC.decode(extra).forMenu(id);
  }

  /** What the server writes when it opens the screen; the client puts its own menu ID. */
  public static void writeOpening(
      RegistryFriendlyByteBuf extra, ServerPlayer player, BlockPos pos, NetworkId network) {
    Optional<NetworkView> opening = read(player, network);
    extra.writeBlockPos(pos);
    ControllerScreenRows.STREAM_CODEC.encode(extra, rowsOf(player, opening, 0));
    ControllerScreenSignals.STREAM_CODEC.encode(extra, signalsOf(opening, 0));
    ControllerScreenLog.STREAM_CODEC.encode(
        extra, ControllerScreenLog.of(0, logSize(), logOf(network, NO_LINE_SEEN)));
  }

  /** The lines of the log of a network newer than a sequence, newest first (answer 147). */
  private static List<SignalLog.Line> logOf(NetworkId network, long sequence) {
    return RedweaveServer.running()
        .map(server -> server.signalLogs().after(network, sequence))
        .orElse(List.of());
  }

  private static long clearsOf(NetworkId network) {
    return RedweaveServer.running().map(server -> server.signalLogs().clearsOf(network)).orElse(0L);
  }

  private static int logSize() {
    return RedweaveServer.running().map(server -> server.signalLogs().size()).orElse(0);
  }

  private static long lastSequenceOf(List<SignalLog.Line> newestFirst) {
    return newestFirst.isEmpty() ? NO_LINE_SEEN : newestFirst.get(0).sequence();
  }

  /**
   * The lines the open screen has not received yet, once: the server sends them with the other
   * changes, and only to the players with the screen open (answer 147 and rule 18).
   */
  public Optional<ControllerScreenLog> newLog() {
    if (network.isEmpty()) {
      return Optional.empty();
    }
    long clears = clearsOf(network.get());
    if (clears != logClears) {
      logClears = clears;
      List<SignalLog.Line> all = logOf(network.get(), NO_LINE_SEEN);
      logSeen = Math.max(logSeen, lastSequenceOf(all));
      return Optional.of(ControllerScreenLog.restart(containerId, logSize(), all));
    }
    List<SignalLog.Line> lines = logOf(network.get(), logSeen);
    if (lines.isEmpty()) {
      return Optional.empty();
    }
    logSeen = lastSequenceOf(lines);
    return Optional.of(ControllerScreenLog.of(containerId, logSize(), lines));
  }

  private static Optional<NetworkView> read(ServerPlayer player, NetworkId network) {
    PlayerId viewer = new PlayerId(player.getUUID());
    return RedweaveServer.running()
        .flatMap(server ->
            server.showNetwork().of(viewer, network, NodeBlocks.loadedIn(player.getServer())));
  }

  /** Empty rows when the network is gone, so the screen opens blank instead of failing (14). */
  private static ControllerScreenRows rowsOf(
      ServerPlayer player, Optional<NetworkView> view, int menu) {
    return view.map(
            found -> {
              String owner = PlayerNames.of(player.getServer(), found.owner().value());
              return ControllerScreenRows.of(menu, found, owner);
            })
        .orElseGet(() -> ControllerScreenRows.empty(menu));
  }

  private static ControllerScreenSignals signalsOf(Optional<NetworkView> view, int menu) {
    return view.map(found -> ControllerScreenSignals.of(menu, found))
        .orElse(ControllerScreenSignals.NONE);
  }

  /** Sends the rows and the live values again, each only when it changed (answer 82). */
  @Override
  public void broadcastChanges() {
    super.broadcastChanges();
    if (viewer.isEmpty() || network.isEmpty()) {
      return;
    }
    ServerPlayer player = viewer.get();
    view = read(player, network.get());
    view.ifPresent(now -> {
      ControllerScreenRows nowRows = rowsOf(player, view, containerId);
      if (!nowRows.equals(rows)) {
        rows = nowRows;
        PacketDistributor.sendToPlayer(player, nowRows);
      }
      ControllerScreenSignals nowSignals = ControllerScreenSignals.of(containerId, now);
      if (!nowSignals.equals(signals)) {
        signals = nowSignals;
        PacketDistributor.sendToPlayer(player, nowSignals);
      }
    });
    newLog().ifPresent(lines -> PacketDistributor.sendToPlayer(player, lines));
  }

  /**
   * A button is a color of the palette, which recolors the network, or the Clear log, which clears
   * its log; the domain refuses anyone but the owner (answers 212 and 264).
   */
  @Override
  public boolean clickMenuButton(Player player, int button) {
    if (button == ControllerScreenCodes.CLEAR_LOG && network.isPresent()) {
      RedweaveServer.running()
          .ifPresent(
              server -> server.clearLog().clear(new PlayerId(player.getUUID()), network.get()));
      return true;
    }
    Optional<ChannelColor> color = ControllerScreenCodes.colorOf(button);
    if (color.isEmpty() || network.isEmpty()) {
      return false;
    }
    RedweaveServer.running()
        .ifPresent(server ->
            server
                .recolorNetwork()
                .recolor(new PlayerId(player.getUUID()), network.get(), color.get()));
    return true;
  }

  /** A text of the screen: the name of the network, of a channel or of a node row (156, 217). */
  public void rename(Player player, RenameTarget target, int index, String name) {
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (server.isEmpty() || network.isEmpty()) {
      return;
    }
    PlayerId requester = new PlayerId(player.getUUID());
    switch (target) {
      case NETWORK -> server.get().renameNetwork().rename(requester, network.get(), name);
      case CHANNEL ->
          ControllerScreenCodes.colorOf(index)
              .ifPresent(channel ->
                  server.get().renameChannel().rename(requester, network.get(), channel, name));
      case NODE ->
          nodeAt(index).ifPresent(node -> server.get().renameNode().rename(requester, node, name));
    }
  }

  private Optional<NodeId> nodeAt(int index) {
    return view.map(NetworkView::nodes)
        .filter(rows -> index >= 0 && index < rows.size())
        .map(rows -> rows.get(index).id());
  }

  @Override
  public boolean stillValid(Player player) {
    return stillValid(access, player, anchor) && isOnline();
  }

  /** On the client there is no network to check, and the server closes the screen. */
  private boolean isOnline() {
    if (network.isEmpty()) {
      return true;
    }
    return RedweaveServer.running()
        .flatMap(server -> server.network(network.get()))
        .map(Network::isOnline)
        .orElse(false);
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    return ItemStack.EMPTY;
  }

  void acceptRows(ControllerScreenRows received) {
    rows = received;
  }

  void acceptSignals(ControllerScreenSignals received) {
    signals = received;
  }

  void acceptLog(ControllerScreenLog received) {
    log = log.then(received);
  }

  /** The lines of the log, newest first, as the server sent them (answer 147). */
  public List<ControllerScreenLog.LogLine> log() {
    return log.lines();
  }

  public BlockPos pos() {
    return pos;
  }

  public ControllerScreenRows.Header header() {
    return rows.header();
  }

  public List<ControllerScreenRows.NodeLine> nodes() {
    return rows.nodes();
  }

  public List<ControllerScreenRows.ChannelLine> channels() {
    return rows.channels();
  }

  /** The signal of a node row, 0 before the values arrive. */
  public int nodeSignal(int row) {
    return valueAt(signals.nodeSignals(), row, 0);
  }

  /** The state of a node row, Online before the values arrive. */
  public NodeState nodeState(int row) {
    int ordinal = valueAt(signals.nodeStates(), row, NodeState.ONLINE.ordinal());
    NodeState[] states = NodeState.values();
    return ordinal >= 0 && ordinal < states.length ? states[ordinal] : NodeState.ONLINE;
  }

  public int channelValue(ChannelColor channel) {
    return valueAt(signals.channelValues(), channel.ordinal(), 0);
  }

  private static int valueAt(List<Integer> values, int index, int fallback) {
    return index >= 0 && index < values.size() ? values.get(index) : fallback;
  }
}
