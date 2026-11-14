package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.DetectorState;
import io.github.marcelofabianov.redweave.domain.module.Weather;
import io.github.marcelofabianov.redweave.domain.module.WeatherMode;
import io.github.marcelofabianov.redweave.domain.network.NodeState;
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
 * The menu of the Weather Detector Screen: telas.md, Tela do Weather Detector. The server keeps the
 * live values in sync and decides every click; the client only shows them (rules 16 and 17).
 */
public final class DetectorMenu extends AbstractContainerMenu
    implements NetworkChoiceMenu, ModuleMenu {
  private final BlockPos pos;
  private final ContainerLevelAccess access;
  private final ContainerData data;
  private final Optional<ServerPlayer> viewer;
  private final ModuleScreenSync sync;

  /** On the server, with the live values read from the block. */
  public DetectorMenu(int id, ServerPlayer player, BlockPos pos) {
    super(RedweaveMenus.DETECTOR.get(), id);
    this.pos = pos;
    this.access = ContainerLevelAccess.create(player.level(), pos);
    this.data = new DetectorScreenData(player, pos);
    this.viewer = Optional.of(player);
    this.sync = ModuleScreenSync.onServer(player, pos, id);
    addDataSlots(data);
  }

  /** On the client, with the position and the names the server sent when opening. */
  public DetectorMenu(int id, Inventory inventory, RegistryFriendlyByteBuf extra) {
    super(RedweaveMenus.DETECTOR.get(), id);
    this.pos = extra.readBlockPos();
    this.access = ContainerLevelAccess.NULL;
    this.data = new SimpleContainerData(DetectorScreenLayout.SIZE);
    this.viewer = Optional.empty();
    this.sync = ModuleScreenSync.onClient(id, extra);
    addDataSlots(data);
  }

  /** What the server writes when it opens the screen; the client puts its own menu ID. */
  public static void writeOpening(
      RegistryFriendlyByteBuf extra, ServerPlayer player, BlockPos pos) {
    extra.writeBlockPos(pos);
    ModuleScreenSync.writeOpening(extra, player, pos);
  }

  @Override
  public boolean clickMenuButton(Player player, int button) {
    Optional<DetectorAction> action = DetectorAction.of(button);
    action.ifPresent(found -> apply(player, found));
    return action.isPresent();
  }

  /** A choice names a row of the list kept here, so a client never sends a network ID. */
  private void apply(Player player, DetectorAction action) {
    if (action.kind() != DetectorAction.Kind.CHOOSE) {
      DetectorScreenActions.apply(player, pos, action);
      return;
    }
    sync.choiceAt(action.value())
        .ifPresent(network -> ModuleScreenActions.choose(player, pos, network));
  }

  @Override
  public void broadcastChanges() {
    super.broadcastChanges();
    viewer.ifPresent(player -> sync.broadcast(player, pos, containerId));
  }

  @Override
  public boolean stillValid(Player player) {
    return stillValid(access, player, RedweaveBlocks.WEATHER_DETECTOR.get());
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    return ItemStack.EMPTY;
  }

  @Override
  public void acceptChoices(List<NodeScreenChoices.Row> received) {
    sync.acceptRows(received);
  }

  @Override
  public BlockPos pos() {
    return pos;
  }

  @Override
  public ModuleScreenSync sync() {
    return sync;
  }

  public boolean viewerIsOwner() {
    return data.get(DetectorScreenLayout.VIEWER_IS_OWNER) == 1;
  }

  /** `On`, `Off` or `No weather` (answer 465), or none before the first sync. */
  public Optional<DetectorState> state() {
    return ModuleScreenValues.valueAt(DetectorState.values(), data.get(DetectorScreenLayout.STATE));
  }

  /** The state of the network seen from the plate, or none without a network (answer 385). */
  public Optional<NodeState> networkState() {
    return ModuleScreenValues.valueAt(
        NodeState.values(), data.get(DetectorScreenLayout.NETWORK_STATE));
  }

  public Optional<WeatherMode> mode() {
    return ModuleScreenValues.valueAt(WeatherMode.values(), data.get(DetectorScreenLayout.MODE));
  }

  public Connection output() {
    return ModuleScreenValues.connectionOf(data.get(DetectorScreenLayout.OUTPUT));
  }

  /** The weather of the last tick, for the icon (answer 470). */
  public Optional<Weather> weather() {
    return ModuleScreenValues.valueAt(Weather.values(), data.get(DetectorScreenLayout.WEATHER));
  }
}
