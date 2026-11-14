package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.application.NetworkChoice;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.TimerState;
import io.github.marcelofabianov.redweave.domain.network.NodeState;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.registry.RedweaveMenus;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

/**
 * The menu of the Signal Timer Screen: telas.md, Tela do Signal Timer. The server keeps the live
 * values in sync and decides every click; the client only shows them (rules 16 and 17).
 */
public final class TimerMenu extends AbstractContainerMenu implements NetworkChoiceMenu {
  private static final String NO_NAME = "";

  private final BlockPos pos;
  private final ContainerLevelAccess access;
  private final ContainerData data;
  private final Optional<ServerPlayer> viewer;
  private TimerScreenNames names;
  private List<NetworkChoice> choices = List.of();
  private List<NodeScreenChoices.Row> rows = List.of();

  /** On the server, with the live values read from the block. */
  public TimerMenu(int id, ServerPlayer player, BlockPos pos) {
    super(RedweaveMenus.TIMER.get(), id);
    this.pos = pos;
    this.access = ContainerLevelAccess.create(player.level(), pos);
    this.data = new TimerScreenData(player, pos);
    this.viewer = Optional.of(player);
    this.names = TimerScreenNames.read(player, pos, id);
    addDataSlots(data);
  }

  /** On the client, with the position and the names the server sent when opening. */
  public TimerMenu(int id, Inventory inventory, RegistryFriendlyByteBuf extra) {
    super(RedweaveMenus.TIMER.get(), id);
    this.pos = extra.readBlockPos();
    this.access = ContainerLevelAccess.NULL;
    this.data = new SimpleContainerData(TimerScreenLayout.SIZE);
    this.viewer = Optional.empty();
    TimerScreenNames opening = TimerScreenNames.STREAM_CODEC.decode(extra);
    this.names =
        new TimerScreenNames(
            id, opening.module(), opening.network(), opening.color(), opening.labels());
    addDataSlots(data);
  }

  /** What the server writes when it opens the screen; the client puts its own menu ID. */
  public static void writeOpening(
      RegistryFriendlyByteBuf extra, ServerPlayer player, BlockPos pos) {
    extra.writeBlockPos(pos);
    TimerScreenNames.STREAM_CODEC.encode(extra, TimerScreenNames.read(player, pos, 0));
  }

  @Override
  public boolean clickMenuButton(Player player, int button) {
    Optional<TimerAction> action = TimerScreenCodes.actionOf(button);
    action.ifPresent(found -> apply(player, found));
    return action.isPresent();
  }

  /** A choice names a row of the list kept here, so a client never sends a network ID. */
  private void apply(Player player, TimerAction action) {
    if (action.kind() != TimerAction.Kind.CHOOSE) {
      TimerScreenActions.apply(player, pos, action);
      return;
    }
    if (action.value() < choices.size()) {
      TimerScreenActions.choose(player, pos, choices.get(action.value()).id());
    }
  }

  /** Sends the names and the network choice again when they change. */
  @Override
  public void broadcastChanges() {
    super.broadcastChanges();
    viewer.ifPresent(player -> {
      TimerScreenNames now = TimerScreenNames.read(player, pos, containerId);
      if (!now.equals(names)) {
        names = now;
        TimerScreenNames.send(player, now);
      }
      List<NetworkChoice> nowChoices = TimerScreenActions.choices(player, pos);
      if (!nowChoices.equals(choices)) {
        choices = nowChoices;
        NodeScreenChoices.send(player, containerId, nowChoices);
      }
    });
  }

  @Override
  public boolean stillValid(Player player) {
    return stillValid(access, player, RedweaveBlocks.SIGNAL_TIMER.get());
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    return ItemStack.EMPTY;
  }

  void acceptNames(TimerScreenNames received) {
    names = received;
  }

  @Override
  public void acceptChoices(List<NodeScreenChoices.Row> received) {
    rows = received;
  }

  public BlockPos pos() {
    return pos;
  }

  public String moduleName() {
    return names.module();
  }

  public String networkName() {
    return names.network();
  }

  /** The color of the network, or none for a module with no network. */
  public Optional<ChannelColor> networkColor() {
    return colorAt(names.color());
  }

  /** The name the owner gave a channel, or empty for the name of its color (answer 221). */
  public String channelName(ChannelColor channel) {
    List<String> labels = names.labels().channels();
    return channel.ordinal() < labels.size() ? labels.get(channel.ordinal()) : NO_NAME;
  }

  public List<NodeScreenChoices.Row> choices() {
    return rows;
  }

  public boolean viewerIsOwner() {
    return data.get(TimerScreenLayout.VIEWER_IS_OWNER) == 1;
  }

  public Optional<TimerState> state() {
    return valueAt(TimerState.values(), data.get(TimerScreenLayout.STATE));
  }

  /** The state of the network seen from the plate, or none without a network (answer 385). */
  public Optional<NodeState> networkState() {
    return valueAt(NodeState.values(), data.get(TimerScreenLayout.NETWORK_STATE));
  }

  public Connection output() {
    return connectionOf(data.get(TimerScreenLayout.OUTPUT));
  }

  public Connection pause() {
    return connectionOf(data.get(TimerScreenLayout.PAUSE));
  }

  public int intervalTicks() {
    return ticksAt(TimerScreenLayout.INTERVAL);
  }

  public int pulseTicks() {
    return ticksAt(TimerScreenLayout.PULSE);
  }

  public int elapsedTicks() {
    return ticksAt(TimerScreenLayout.ELAPSED);
  }

  private int ticksAt(int first) {
    return TimerScreenLayout.whole(data.get(first), data.get(first + 1));
  }

  private static Connection connectionOf(int code) {
    return colorAt(code - 1).map(Connection::on).orElse(Connection.EDGE);
  }

  private static Optional<ChannelColor> colorAt(int index) {
    return index >= 0 && index < ChannelColor.values().length
        ? Optional.of(ChannelColor.values()[index])
        : Optional.empty();
  }

  /** A state kept as its order plus one, where 0 is none. */
  private static <T> Optional<T> valueAt(T[] values, int code) {
    return code > TimerScreenLayout.NONE && code <= values.length
        ? Optional.of(values[code - 1])
        : Optional.empty();
  }
}
