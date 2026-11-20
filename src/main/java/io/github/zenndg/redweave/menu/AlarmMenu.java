package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.block.part.AlarmPart;
import io.github.zenndg.redweave.block.part.ModulePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.domain.module.AlarmListeners;
import io.github.zenndg.redweave.domain.module.Connection;
import io.github.zenndg.redweave.domain.network.NodeState;
import io.github.zenndg.redweave.registry.RedweaveMenus;
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
 * The menu of the Signal Alarm Screen: alarme.md. The server keeps the live values in sync and
 * decides every click; the client only shows them (rules 16 and 17).
 */
public final class AlarmMenu extends AbstractContainerMenu
    implements NetworkChoiceMenu, ModuleMenu {
  /** The reach of a player to a block, with the slack of the game for an open screen. */
  private static final double INTERACTION_SLACK = 4.0;

  private final PartSpot spot;
  private final ContainerData data;
  private final Optional<ServerPlayer> viewer;
  private final ModuleScreenSync sync;

  /** On the server, with the live values read from the part. */
  public AlarmMenu(int id, ServerPlayer player, PartSpot spot) {
    super(RedweaveMenus.ALARM.get(), id);
    this.spot = spot;
    this.data = new AlarmScreenData(player, spot);
    this.viewer = Optional.of(player);
    this.sync = ModuleScreenSync.onServer(player, spot, id);
    addDataSlots(data);
  }

  /** On the client, with the position and the names the server sent when opening. */
  public AlarmMenu(int id, Inventory inventory, RegistryFriendlyByteBuf extra) {
    super(RedweaveMenus.ALARM.get(), id);
    this.spot = PartSpot.STREAM_CODEC.decode(extra);
    this.data = new SimpleContainerData(AlarmScreenLayout.SIZE);
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
    Optional<AlarmAction> action = AlarmAction.of(button);
    action.ifPresent(found -> apply(player, found));
    return action.isPresent();
  }

  /** A choice names a row of the list kept here, so a client never sends a network ID. */
  private void apply(Player player, AlarmAction action) {
    if (action.kind() != AlarmAction.Kind.CHOOSE) {
      AlarmScreenActions.apply(player, spot, action);
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
    return ModulePart.at(player.level(), spot, AlarmPart.class).isPresent()
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
    return data.get(AlarmScreenLayout.VIEWER_IS_OWNER) == 1;
  }

  /** Whether the alarm rings, or none before the first sync (answer 649). */
  public Optional<Boolean> ringing() {
    int code = data.get(AlarmScreenLayout.RINGING);
    return code == ModuleScreenValues.NONE
        ? Optional.empty()
        : Optional.of(code == AlarmScreenLayout.RINGING_CODE);
  }

  /** The state of the network seen from the plate, or none without a network (answer 385). */
  public Optional<NodeState> networkState() {
    return ModuleScreenValues.valueAt(
        NodeState.values(), data.get(AlarmScreenLayout.NETWORK_STATE));
  }

  public Connection input() {
    return ModuleScreenValues.connectionOf(data.get(AlarmScreenLayout.INPUT));
  }

  /** Who hears the alarm (answer 645). */
  public AlarmListeners listeners() {
    return ModuleScreenValues.valueAt(
            AlarmListeners.values(), data.get(AlarmScreenLayout.LISTENERS))
        .orElse(AlarmListeners.OWNER);
  }

  /** How many times it rings (answer 646). */
  public int repeats() {
    return data.get(AlarmScreenLayout.REPEATS);
  }

  /** The ticks between rings (answer 646). */
  public int intervalTicks() {
    return data.get(AlarmScreenLayout.INTERVAL);
  }

  /** The ticks it rings for (answer 648). */
  public int durationTicks() {
    return data.get(AlarmScreenLayout.DURATION);
  }
}
