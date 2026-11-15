package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.application.NetworkChoice;
import io.github.marcelofabianov.redweave.block.part.NodePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.NodeState;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.registry.RedweaveMenus;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

/**
 * The menu behind the Node Screen: _docs/funcionalidades/telas.md. It has no item slots. The client
 * reads the live values, and every action goes to the server as a button click, which the game only
 * delivers while the menu is still valid, so within reach of the node (answer 81).
 */
public final class NodeMenu extends AbstractContainerMenu implements NetworkChoiceMenu {
  private static final int NO_STATE = 0;
  private static final String NO_NAME = "";

  /** How far past the reach of the player the menu stays open, as the game does for blocks. */
  private static final double INTERACTION_SLACK = 4.0;

  private final PartSpot spot;
  private final ContainerData data;
  private final Optional<ServerPlayer> viewer;
  private NodeScreenNames names;
  private List<NetworkChoice> choices = List.of();
  private List<NodeScreenChoices.Row> rows = List.of();

  /** On the server, with the live values read from the network. */
  public NodeMenu(int id, ServerPlayer player, PartSpot spot) {
    super(RedweaveMenus.NODE.get(), id);
    this.spot = spot;
    this.data = new NodeScreenData(player, spot);
    this.viewer = Optional.of(player);
    this.names = NodeScreenNames.read(player, spot, id);
    addDataSlots(data);
  }

  /** On the client, with the position and the names the server sent when opening. */
  public NodeMenu(int id, Inventory inventory, RegistryFriendlyByteBuf extra) {
    super(RedweaveMenus.NODE.get(), id);
    this.spot = PartSpot.STREAM_CODEC.decode(extra);
    this.data = new SimpleContainerData(NodeScreenLayout.SIZE);
    this.viewer = Optional.empty();
    NodeScreenNames opening = NodeScreenNames.STREAM_CODEC.decode(extra);
    this.names =
        new NodeScreenNames(
            id,
            opening.node(),
            opening.network(),
            opening.color(),
            opening.owner(),
            opening.labels());
    addDataSlots(data);
  }

  /**
   * What the server writes when it opens the screen, read by the client constructor. The menu ID is
   * not known yet, so the client puts its own.
   */
  public static void writeOpening(
      RegistryFriendlyByteBuf extra, ServerPlayer player, PartSpot spot) {
    PartSpot.STREAM_CODEC.encode(extra, spot);
    NodeScreenNames.STREAM_CODEC.encode(extra, NodeScreenNames.read(player, spot, 0));
  }

  @Override
  public boolean clickMenuButton(Player player, int button) {
    Optional<NodeAction> action = NodeScreenCodes.actionOf(button);
    action.ifPresent(found -> apply(player, found));
    return action.isPresent();
  }

  /** A choice names a row of the list kept here, so a client never sends a network ID. */
  private void apply(Player player, NodeAction action) {
    if (action.kind() != NodeAction.Kind.CHOOSE) {
      NodeScreenActions.apply(player, spot, action);
      return;
    }
    if (action.index() < choices.size()) {
      NodeScreenActions.choose(player, spot, choices.get(action.index()).id());
    }
  }

  /**
   * Sends the names and the network choice again when they change, as when the network is renamed
   * or another network comes online (answer 82).
   */
  @Override
  public void broadcastChanges() {
    super.broadcastChanges();
    viewer.ifPresent(player -> {
      NodeScreenNames now = NodeScreenNames.read(player, spot, containerId);
      if (!now.equals(names)) {
        names = now;
        NodeScreenNames.send(player, now);
      }
      List<NetworkChoice> nowChoices = NodeScreenChoices.read(player, spot);
      if (!nowChoices.equals(choices)) {
        choices = nowChoices;
        NodeScreenChoices.send(player, containerId, nowChoices);
      }
    });
  }

  @Override
  public boolean stillValid(Player player) {
    return NodePart.at(player.level(), spot).isPresent()
        && player.canInteractWithBlock(spot.pos(), INTERACTION_SLACK);
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    return ItemStack.EMPTY;
  }

  void acceptNames(NodeScreenNames received) {
    names = received;
  }

  @Override
  public void acceptChoices(List<NodeScreenChoices.Row> received) {
    rows = received;
  }

  public PartSpot spot() {
    return spot;
  }

  /** Opens the Node Screen, from the Configurator in the Open mode (answer 140). */
  public static void open(ServerPlayer viewer, PartSpot spot) {
    viewer.openMenu(
        new SimpleMenuProvider(
            (id, inventory, opener) -> new NodeMenu(id, viewer, spot),
            RedweaveItems.SIGNAL_NODE.get().getDescription()),
        extra -> writeOpening(extra, viewer, spot));
  }

  public String nodeName() {
    return names.node();
  }

  public String networkName() {
    return names.network();
  }

  /** The color of the network, or none for a node with no network (answer 208). */
  public Optional<ChannelColor> networkColor() {
    int color = names.color();
    if (color < 0 || color >= ChannelColor.values().length) {
      return Optional.empty();
    }
    return Optional.of(ChannelColor.values()[color]);
  }

  /** The networks the viewer may pick, in the order the server keeps (answers 157 and 203). */
  public List<NodeScreenChoices.Row> choices() {
    return rows;
  }

  /** The name the owner gave a channel, or empty for the name of its color (answer 221). */
  public String channelName(ChannelColor channel) {
    return nameAt(names.labels().channels(), channel);
  }

  private static String nameAt(List<String> names, ChannelColor channel) {
    return channel.ordinal() < names.size() ? names.get(channel.ordinal()) : NO_NAME;
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

  /** Whether the viewer placed the node, the only one who picks its first network (172). */
  public boolean viewerPlacedIt() {
    return data.get(NodeScreenLayout.VIEWER_PLACED) == 1;
  }
}
