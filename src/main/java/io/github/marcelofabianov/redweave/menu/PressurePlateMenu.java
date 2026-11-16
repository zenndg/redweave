package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.block.part.PressurePlatePart;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.PlateMode;
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
 * The menu of the Signal Pressure Plate Screen: placa-de-pressao.md. The server keeps the live
 * values in sync and decides every click; the client only shows them (rules 16 and 17).
 */
public final class PressurePlateMenu extends AbstractContainerMenu
    implements NetworkChoiceMenu, ModuleMenu {
  /** The reach of a player to a block, with the slack of the game for an open screen. */
  private static final double INTERACTION_SLACK = 4.0;

  private final PartSpot spot;
  private final ContainerData data;
  private final Optional<ServerPlayer> viewer;
  private final ModuleScreenSync sync;

  /** On the server, with the live values read from the part. */
  public PressurePlateMenu(int id, ServerPlayer player, PartSpot spot) {
    super(RedweaveMenus.PRESSURE_PLATE.get(), id);
    this.spot = spot;
    this.data = new PressurePlateScreenData(player, spot);
    this.viewer = Optional.of(player);
    this.sync = ModuleScreenSync.onServer(player, spot, id);
    addDataSlots(data);
  }

  /** On the client, with the position and the names the server sent when opening. */
  public PressurePlateMenu(int id, Inventory inventory, RegistryFriendlyByteBuf extra) {
    super(RedweaveMenus.PRESSURE_PLATE.get(), id);
    this.spot = PartSpot.STREAM_CODEC.decode(extra);
    this.data = new SimpleContainerData(PressurePlateScreenLayout.SIZE);
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
    Optional<PressurePlateAction> action = PressurePlateAction.of(button);
    action.ifPresent(found -> apply(player, found));
    return action.isPresent();
  }

  /** A choice names a row of the list kept here, so a client never sends a network ID. */
  private void apply(Player player, PressurePlateAction action) {
    if (action.kind() != PressurePlateAction.Kind.CHOOSE) {
      PressurePlateScreenActions.apply(player, spot, action);
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
    return ModulePart.at(player.level(), spot, PressurePlatePart.class).isPresent()
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
    return data.get(PressurePlateScreenLayout.VIEWER_IS_OWNER) == 1;
  }

  /** Whether the plate is on, or none before the first sync (answer 609). */
  public Optional<Boolean> on() {
    int code = data.get(PressurePlateScreenLayout.ON);
    return code == ModuleScreenValues.NONE
        ? Optional.empty()
        : Optional.of(code == PressurePlateScreenLayout.ON_CODE);
  }

  /** The state of the network seen from the plate, or none without a network (answer 385). */
  public Optional<NodeState> networkState() {
    return ModuleScreenValues.valueAt(
        NodeState.values(), data.get(PressurePlateScreenLayout.NETWORK_STATE));
  }

  public Connection output() {
    return ModuleScreenValues.connectionOf(data.get(PressurePlateScreenLayout.OUTPUT));
  }

  /** The strength the plate gives while on, from 1 to 15 (answer 552). */
  public int strength() {
    return data.get(PressurePlateScreenLayout.STRENGTH);
  }

  /** The mode the plate senses, or none before the first sync (answer 621). */
  public Optional<PlateMode> mode() {
    return ModuleScreenValues.valueAt(PlateMode.values(), data.get(PressurePlateScreenLayout.MODE));
  }
}
