package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.block.part.ItemSensorPart;
import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.network.NodeState;
import io.github.marcelofabianov.redweave.registry.RedweaveMenus;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * The menu of the Signal Item Sensor Screen: sensor-de-itens.md. It shows the inventory of the
 * player, so the owner can take an item on the cursor and click the ghost slot with it; the item is
 * never taken from the cursor (answer 696). The server keeps the live values in sync and decides
 * every click; the client only shows them (rules 16 and 17).
 */
public final class ItemSensorMenu extends AbstractContainerMenu
    implements NetworkChoiceMenu, ModuleMenu {
  /** Where the inventory of the player starts, under the frame of every module screen. */
  public static final int INVENTORY_X = 90;

  public static final int INVENTORY_Y = 177;
  public static final int HOTBAR_Y = 235;
  public static final int SLOT_SIZE = 18;
  public static final int COLUMNS = 9;
  public static final int ROWS = 3;

  /** The reach of a player to a block, with the slack of the game for an open screen. */
  private static final double INTERACTION_SLACK = 4.0;

  private final PartSpot spot;
  private final ContainerData data;
  private final Optional<ServerPlayer> viewer;
  private final ModuleScreenSync sync;

  /** On the server, with the live values read from the part. */
  public ItemSensorMenu(int id, Inventory inventory, ServerPlayer player, PartSpot spot) {
    super(RedweaveMenus.ITEM_SENSOR.get(), id);
    this.spot = spot;
    this.data = new ItemSensorScreenData(player, spot);
    this.viewer = Optional.of(player);
    this.sync = ModuleScreenSync.onServer(player, spot, id);
    addInventory(inventory);
    addDataSlots(data);
  }

  /** On the client, with the position and the names the server sent when opening. */
  public ItemSensorMenu(int id, Inventory inventory, RegistryFriendlyByteBuf extra) {
    super(RedweaveMenus.ITEM_SENSOR.get(), id);
    this.spot = PartSpot.STREAM_CODEC.decode(extra);
    this.data = new SimpleContainerData(ItemSensorScreenLayout.SIZE);
    this.viewer = Optional.empty();
    this.sync = ModuleScreenSync.onClient(id, extra);
    addInventory(inventory);
    addDataSlots(data);
  }

  /** What the server writes when it opens the screen; the client puts its own menu ID. */
  public static void writeOpening(
      RegistryFriendlyByteBuf extra, ServerPlayer player, PartSpot spot) {
    PartSpot.STREAM_CODEC.encode(extra, spot);
    ModuleScreenSync.writeOpening(extra, player, spot);
  }

  /** The three rows of the inventory and the hotbar, as every screen with slots of the game. */
  private void addInventory(Inventory inventory) {
    for (int row = 0; row < ROWS; row++) {
      for (int column = 0; column < COLUMNS; column++) {
        addSlot(
            new Slot(
                inventory,
                COLUMNS + row * COLUMNS + column,
                INVENTORY_X + column * SLOT_SIZE,
                INVENTORY_Y + row * SLOT_SIZE));
      }
    }
    for (int column = 0; column < COLUMNS; column++) {
      addSlot(new Slot(inventory, column, INVENTORY_X + column * SLOT_SIZE, HOTBAR_Y));
    }
  }

  @Override
  public boolean clickMenuButton(Player player, int button) {
    Optional<ItemSensorAction> action = ItemSensorAction.of(button);
    action.ifPresent(found -> apply(player, found));
    return action.isPresent();
  }

  /** A choice names a row of the list kept here, so a client never sends a network ID. */
  private void apply(Player player, ItemSensorAction action) {
    if (action.kind() == ItemSensorAction.Kind.DELAY) {
      DelayScreenActions.step(player, spot, action.value());
      return;
    }
    if (action.kind() != ItemSensorAction.Kind.CHOOSE) {
      ItemSensorScreenActions.apply(player, spot, action, cursorItem());
      return;
    }
    sync.choiceAt(action.value())
        .ifPresent(network -> ModuleScreenActions.choose(player, spot, network));
  }

  /** The item on the cursor, as the server sees it, or none with an empty cursor. */
  private Optional<String> cursorItem() {
    ItemStack carried = getCarried();
    return carried.isEmpty()
        ? Optional.empty()
        : Optional.of(BuiltInRegistries.ITEM.getKey(carried.getItem()).toString());
  }

  @Override
  public void broadcastChanges() {
    super.broadcastChanges();
    viewer.ifPresent(player -> sync.broadcast(player, spot, containerId));
  }

  @Override
  public boolean stillValid(Player player) {
    return ModulePart.at(player.level(), spot, ItemSensorPart.class).isPresent()
        && player.canInteractWithBlock(spot.pos(), INTERACTION_SLACK);
  }

  /** Shift + click moves nothing: the screen has no slot of its own (answer 689). */
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
    return data.get(ItemSensorScreenLayout.VIEWER_IS_OWNER) == 1;
  }

  /** Whether the sensor is on, or none before the first sync (answer 697). */
  public Optional<Boolean> on() {
    int code = data.get(ItemSensorScreenLayout.ON);
    return code == ModuleScreenValues.NONE
        ? Optional.empty()
        : Optional.of(code == ItemSensorScreenLayout.ON_CODE);
  }

  public Optional<NodeState> networkState() {
    return ModuleScreenValues.valueAt(
        NodeState.values(), data.get(ItemSensorScreenLayout.NETWORK_STATE));
  }

  public Connection output() {
    return ModuleScreenValues.connectionOf(data.get(ItemSensorScreenLayout.OUTPUT));
  }

  /** The strength it gives while on, from 1 to 15 (answer 696). */
  public int strength() {
    return data.get(ItemSensorScreenLayout.STRENGTH);
  }

  /** The expected item, or none before one is picked (answer 696). */
  public Optional<Item> item() {
    int code = whole(ItemSensorScreenLayout.ITEM);
    return code == ModuleScreenValues.NONE
        ? Optional.empty()
        : Optional.of(BuiltInRegistries.ITEM.byId(code - 1));
  }

  /** The expected count (answer 693). */
  public int count() {
    return whole(ItemSensorScreenLayout.COUNT);
  }

  /** What the last read found in the container, as the `7` of `7 / 10` (answer 696). */
  public int counted() {
    return whole(ItemSensorScreenLayout.COUNTED);
  }

  private int whole(int first) {
    return TimerScreenLayout.whole(data.get(first), data.get(first + 1));
  }

  /** The delay of the output, in whole seconds (answer 830). */
  public int delaySeconds() {
    return data.get(ItemSensorScreenLayout.DELAY_SECONDS);
  }
}
