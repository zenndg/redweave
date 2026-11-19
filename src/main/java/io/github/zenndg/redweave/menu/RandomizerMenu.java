package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.block.part.ModulePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.block.part.RandomizerPart;
import io.github.zenndg.redweave.domain.module.Connection;
import io.github.zenndg.redweave.domain.module.RandomizerMode;
import io.github.zenndg.redweave.domain.module.RandomizerState;
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
 * The menu of the Signal Randomizer Screen: telas.md, Tela do Signal Randomizer. The server keeps
 * the live values in sync and decides every click; the client only shows them (rules 16 and 17).
 */
public final class RandomizerMenu extends AbstractContainerMenu
    implements NetworkChoiceMenu, ModuleMenu {
  /** The reach of a player to a block, with the slack of the game for an open screen. */
  private static final double INTERACTION_SLACK = 4.0;

  private final PartSpot spot;
  private final ContainerData data;
  private final Optional<ServerPlayer> viewer;
  private final ModuleScreenSync sync;

  /** On the server, with the live values read from the block. */
  public RandomizerMenu(int id, ServerPlayer player, PartSpot spot) {
    super(RedweaveMenus.RANDOMIZER.get(), id);
    this.spot = spot;
    this.data = new RandomizerScreenData(player, spot);
    this.viewer = Optional.of(player);
    this.sync = ModuleScreenSync.onServer(player, spot, id);
    addDataSlots(data);
  }

  /** On the client, with the position and the names the server sent when opening. */
  public RandomizerMenu(int id, Inventory inventory, RegistryFriendlyByteBuf extra) {
    super(RedweaveMenus.RANDOMIZER.get(), id);
    this.spot = PartSpot.STREAM_CODEC.decode(extra);
    this.data = new SimpleContainerData(RandomizerScreenLayout.SIZE);
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
    Optional<RandomizerAction> action = RandomizerAction.of(button);
    action.ifPresent(found -> apply(player, found));
    return action.isPresent();
  }

  /** A choice names a row of the list kept here, so a client never sends a network ID. */
  private void apply(Player player, RandomizerAction action) {
    if (action.kind() == RandomizerAction.Kind.DELAY) {
      DelayScreenActions.step(player, spot, action.value());
      return;
    }
    if (action.kind() != RandomizerAction.Kind.CHOOSE) {
      RandomizerScreenActions.apply(player, spot, action);
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
    return ModulePart.at(player.level(), spot, RandomizerPart.class).isPresent()
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
    return data.get(RandomizerScreenLayout.VIEWER_IS_OWNER) == 1;
  }

  /** `Running` or `Stopped` (answer 484), or none before the first sync. */
  public Optional<RandomizerState> state() {
    return ModuleScreenValues.valueAt(
        RandomizerState.values(), data.get(RandomizerScreenLayout.STATE));
  }

  /** The state of the network seen from the plate, or none without a network (answer 385). */
  public Optional<NodeState> networkState() {
    return ModuleScreenValues.valueAt(
        NodeState.values(), data.get(RandomizerScreenLayout.NETWORK_STATE));
  }

  public Optional<RandomizerMode> mode() {
    return ModuleScreenValues.valueAt(
        RandomizerMode.values(), data.get(RandomizerScreenLayout.MODE));
  }

  public Connection output() {
    return ModuleScreenValues.connectionOf(data.get(RandomizerScreenLayout.OUTPUT));
  }

  /** The value on the output, from 0 to 15, for the big number (answer 487). */
  public int value() {
    return data.get(RandomizerScreenLayout.VALUE);
  }

  public int intervalTicks() {
    return TimerScreenLayout.whole(
        data.get(RandomizerScreenLayout.INTERVAL), data.get(RandomizerScreenLayout.INTERVAL + 1));
  }

  /** The delay of the output, in whole seconds (answer 830). */
  public int delaySeconds() {
    return data.get(RandomizerScreenLayout.DELAY_SECONDS);
  }
}
