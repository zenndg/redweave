package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.block.part.LockPart;
import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.network.NodeState;
import io.github.marcelofabianov.redweave.registry.RedweaveMenus;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

/**
 * The menu of the Signal Lock Screen: fechadura.md. The server keeps the live values in sync and
 * decides every click; the client only shows them (rules 16 and 17).
 */
public final class LockMenu extends AbstractContainerMenu implements NetworkChoiceMenu, ModuleMenu {
  /** The reach of a player to a block, with the slack of the game for an open screen. */
  private static final double INTERACTION_SLACK = 4.0;

  private final PartSpot spot;
  private final ContainerData data;
  private final Optional<ServerPlayer> viewer;
  private final ModuleScreenSync sync;

  /** On the server, with the live values read from the part. */
  public LockMenu(int id, ServerPlayer player, PartSpot spot) {
    super(RedweaveMenus.LOCK.get(), id);
    this.spot = spot;
    this.data = new LockScreenData(player, spot);
    this.viewer = Optional.of(player);
    this.sync = ModuleScreenSync.onServer(player, spot, id);
    addDataSlots(data);
  }

  /** On the client, with the position and the names the server sent when opening. */
  public LockMenu(int id, Inventory inventory, RegistryFriendlyByteBuf extra) {
    super(RedweaveMenus.LOCK.get(), id);
    this.spot = PartSpot.STREAM_CODEC.decode(extra);
    this.data = new SimpleContainerData(LockScreenLayout.SIZE);
    this.viewer = Optional.empty();
    this.sync = ModuleScreenSync.onClient(id, extra);
    addDataSlots(data);
  }

  /** What the server writes when it opens the screen; the client puts its own menu ID. */
  public static void writeOpening(
      RegistryFriendlyByteBuf extra, ServerPlayer player, PartSpot spot) {
    PartSpot.STREAM_CODEC.encode(extra, spot);
    ModuleScreenSync.writeOpening(extra, player, spot);
  }

  @Override
  public boolean clickMenuButton(Player player, int button) {
    Optional<LockAction> action = LockAction.of(button);
    action.ifPresent(found -> apply(player, found));
    return action.isPresent();
  }

  /** A choice names a row of the list kept here, so a client never sends a network ID. */
  private void apply(Player player, LockAction action) {
    if (action.kind() != LockAction.Kind.CHOOSE) {
      LockScreenActions.apply(player, spot, action);
      return;
    }
    sync.choiceAt(action.value())
        .ifPresent(network -> ModuleScreenActions.choose(player, spot, network));
  }

  @Override
  public void broadcastChanges() {
    super.broadcastChanges();
    viewer.ifPresent(player -> sync.broadcast(player, spot, containerId));
  }

  @Override
  public boolean stillValid(Player player) {
    return ModulePart.at(player.level(), spot, LockPart.class).isPresent()
        && player.canInteractWithBlock(spot.pos(), INTERACTION_SLACK);
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
  public PartSpot spot() {
    return spot;
  }

  @Override
  public ModuleScreenSync sync() {
    return sync;
  }

  public boolean viewerIsOwner() {
    return data.get(LockScreenLayout.VIEWER_IS_OWNER) == 1;
  }

  /** Whether the lock is open, or none before the first sync (answer 638). */
  public Optional<Boolean> open() {
    int code = data.get(LockScreenLayout.OPEN);
    return code == ModuleScreenValues.NONE
        ? Optional.empty()
        : Optional.of(code == LockScreenLayout.UNLOCKED_CODE);
  }

  /** The state of the network seen from the plate, or none without a network (answer 385). */
  public Optional<NodeState> networkState() {
    return ModuleScreenValues.valueAt(NodeState.values(), data.get(LockScreenLayout.NETWORK_STATE));
  }

  public Connection input() {
    return ModuleScreenValues.connectionOf(data.get(LockScreenLayout.INPUT));
  }

  public Connection output() {
    return ModuleScreenValues.connectionOf(data.get(LockScreenLayout.OUTPUT));
  }

  /** The key, from 1 to 15, or none for a player who does not own the lock (answer 637). */
  public Optional<Integer> key() {
    int key = data.get(LockScreenLayout.KEY);
    return key == LockScreenLayout.HIDDEN_KEY ? Optional.empty() : Optional.of(key);
  }

  /** The strength the lock gives while open, from 1 to 15 (answer 636). */
  public int strength() {
    return data.get(LockScreenLayout.STRENGTH);
  }

  /** How long the `Toggle` keeps it open, in ticks (answer 640). */
  public int pulseTicks() {
    return data.get(LockScreenLayout.PULSE);
  }
}
