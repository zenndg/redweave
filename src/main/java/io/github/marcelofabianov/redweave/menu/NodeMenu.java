package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.NodeState;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.registry.RedweaveMenus;
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
 * The menu behind the Node Screen: _docs/funcionalidades/telas.md. It has no item slots. The client
 * reads the live values, and every action goes to the server as a button click, which the game only
 * delivers while the menu is still valid, so within reach of the node (answer 81).
 */
public final class NodeMenu extends AbstractContainerMenu {
  private static final int NO_STATE = 0;

  private final BlockPos pos;
  private final ContainerLevelAccess access;
  private final ContainerData data;
  private final Optional<ServerPlayer> viewer;
  private NodeScreenNames names;

  /** On the server, with the live values read from the network. */
  public NodeMenu(int id, ServerPlayer player, BlockPos pos) {
    super(RedweaveMenus.NODE.get(), id);
    this.pos = pos;
    this.access = ContainerLevelAccess.create(player.level(), pos);
    this.data = new NodeScreenData(player.level(), pos, NodeScreenActions.playerOf(player));
    this.viewer = Optional.of(player);
    this.names = NodeScreenNames.read(player, pos, id);
    addDataSlots(data);
  }

  /** On the client, with the position and the names the server sent when opening. */
  public NodeMenu(int id, Inventory inventory, RegistryFriendlyByteBuf extra) {
    super(RedweaveMenus.NODE.get(), id);
    this.pos = extra.readBlockPos();
    this.access = ContainerLevelAccess.NULL;
    this.data = new SimpleContainerData(NodeScreenLayout.SIZE);
    this.viewer = Optional.empty();
    NodeScreenNames opening = NodeScreenNames.STREAM_CODEC.decode(extra);
    this.names = new NodeScreenNames(id, opening.network(), opening.owner());
    addDataSlots(data);
  }

  /**
   * What the server writes when it opens the screen, read by the client constructor. The menu ID is
   * not known yet, so the client puts its own.
   */
  public static void writeOpening(
      RegistryFriendlyByteBuf extra, ServerPlayer player, BlockPos pos) {
    extra.writeBlockPos(pos);
    NodeScreenNames.STREAM_CODEC.encode(extra, NodeScreenNames.read(player, pos, 0));
  }

  @Override
  public boolean clickMenuButton(Player player, int button) {
    Optional<NodeAction> action = NodeScreenCodes.actionOf(button);
    action.ifPresent(found -> NodeScreenActions.apply(player, pos, found));
    return action.isPresent();
  }

  /** Sends the names again when they change, as when the network is renamed (answer 82). */
  @Override
  public void broadcastChanges() {
    super.broadcastChanges();
    viewer.ifPresent(player -> {
      NodeScreenNames now = NodeScreenNames.read(player, pos, containerId);
      if (!now.equals(names)) {
        names = now;
        NodeScreenNames.send(player, now);
      }
    });
  }

  @Override
  public boolean stillValid(Player player) {
    return stillValid(access, player, RedweaveBlocks.SIGNAL_NODE.get());
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    return ItemStack.EMPTY;
  }

  void acceptNames(NodeScreenNames received) {
    names = received;
  }

  public BlockPos pos() {
    return pos;
  }

  public String networkName() {
    return names.network();
  }

  public String ownerName() {
    return names.owner();
  }

  public int slotCount() {
    return data.get(NodeScreenLayout.SLOT_COUNT);
  }

  public Optional<ChannelSlot> slot(int index) {
    return NodeScreenCodes.slotOf(data.get(NodeScreenLayout.FIRST_SLOT + index));
  }

  public int signal(int index) {
    return data.get(NodeScreenLayout.FIRST_SIGNAL + index);
  }

  /** The state of the node, or none without a network. */
  public Optional<NodeState> state() {
    int value = data.get(NodeScreenLayout.STATE);
    if (value == NO_STATE || value > NodeState.values().length) {
      return Optional.empty();
    }
    return Optional.of(NodeState.values()[value - 1]);
  }

  public boolean viewerIsOwner() {
    return data.get(NodeScreenLayout.VIEWER_IS_OWNER) == 1;
  }
}
