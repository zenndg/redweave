package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.application.NetworkChoice;
import io.github.zenndg.redweave.application.NetworkView;
import io.github.zenndg.redweave.application.NodeRow;
import io.github.zenndg.redweave.application.SlotRow;
import io.github.zenndg.redweave.block.NodeBlocks;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.item.RemoteLinking;
import io.github.zenndg.redweave.item.RemoteMode;
import io.github.zenndg.redweave.item.SignalRemoteItem;
import io.github.zenndg.redweave.registry.RedweaveMenus;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * The screen of the link of a Signal Remote: networks, nodes with an input and input slots, with
 * the mode on top and Back (answers 279, 286, 291, 359 and 365). The server keeps the lists and
 * decides; the client points to a position (rules 16 and 17).
 */
public final class RemoteLinkMenu extends AbstractContainerMenu {
  private final InteractionHand hand;
  private final Optional<ServerPlayer> viewer;
  private RemoteLinkStep step = RemoteLinkStep.NETWORKS;
  private List<NetworkChoice> networks = List.of();
  private Optional<NetworkId> networkId = Optional.empty();
  private Optional<NetworkView> network = Optional.empty();
  private Optional<NodeRow> node = Optional.empty();
  private RemoteLinkRows rows;

  /** On the server, for the player who holds the tablet in that hand. */
  public RemoteLinkMenu(int id, ServerPlayer player, InteractionHand hand) {
    super(RedweaveMenus.REMOTE_LINK.get(), id);
    this.hand = hand;
    this.viewer = Optional.of(player);
    this.rows = read(player).forMenu(id);
  }

  /** On the client, with the hand and the rows the server sent when opening. */
  public RemoteLinkMenu(int id, Inventory inventory, RegistryFriendlyByteBuf extra) {
    super(RedweaveMenus.REMOTE_LINK.get(), id);
    this.hand = extra.readEnum(InteractionHand.class);
    this.viewer = Optional.empty();
    this.rows = RemoteLinkRows.STREAM_CODEC.decode(extra).forMenu(id);
  }

  /** Opens the screen of the link, from the Shift + right click (answers 279 and 291). */
  public static void open(ServerPlayer player, InteractionHand hand, Component title) {
    player.openMenu(
        new SimpleMenuProvider(
            (id, inventory, opener) -> new RemoteLinkMenu(id, player, hand), title),
        extra -> {
          extra.writeEnum(hand);
          RemoteLinkRows.STREAM_CODEC.encode(extra, new RemoteLinkMenu(0, player, hand).rows);
        });
  }

  private RemoteLinkRows read(ServerPlayer player) {
    refresh(player);
    return new RemoteLinkRows(
        containerId,
        step.ordinal(),
        RemoteLinking.describe(stack(player)),
        modeOf(player),
        linesNow());
  }

  /** The lists of now: the networks of the player and the view of the network chosen. */
  private void refresh(ServerPlayer player) {
    PlayerId id = new PlayerId(player.getUUID());
    Optional<RedweaveServer> server = RedweaveServer.running();
    networks = server.map(found -> found.networkChoices().of(id)).orElse(List.of());
    network =
        networkId.flatMap(chosen ->
            server.flatMap(found ->
                found.showNetwork().of(id, chosen, NodeBlocks.loadedIn(player.getServer()))));
  }

  private List<RemoteLinkLine> linesNow() {
    return switch (step) {
      case NETWORKS -> networks.stream().map(RemoteLinkLines::ofNetwork).toList();
      case NODES ->
          network
              .map(view ->
                  RemoteLinkLines.inputNodes(view).stream().map(RemoteLinkLines::ofNode).toList())
              .orElse(List.of());
      case SLOTS ->
          slotsNow().stream()
              .map(slot -> RemoteLinkLines.ofSlot(slot, network.orElseThrow()))
              .toList();
    };
  }

  private List<SlotRow> slotsNow() {
    return node.map(RemoteLinkLines::inputSlots).orElse(List.of());
  }

  /** Sends the rows again only when they changed (rule 18). */
  @Override
  public void broadcastChanges() {
    super.broadcastChanges();
    viewer.ifPresent(player -> {
      RemoteLinkRows now = read(player);
      if (!now.equals(rows)) {
        rows = now;
        PacketDistributor.sendToPlayer(player, now);
      }
    });
  }

  @Override
  public boolean clickMenuButton(Player player, int button) {
    if (!(player instanceof ServerPlayer holder) || viewer.isEmpty()) {
      return false;
    }
    switch (button) {
      case RemoteLinkCodes.BACK -> back();
      case RemoteLinkCodes.LEVER -> RemoteLinking.mode(stack(holder), RemoteMode.LEVER);
      case RemoteLinkCodes.BUTTON -> RemoteLinking.mode(stack(holder), RemoteMode.BUTTON);
      default -> RemoteLinkCodes.lineOf(button).ifPresent(line -> choose(holder, line));
    }
    return true;
  }

  private void back() {
    if (step == RemoteLinkStep.SLOTS) {
      step = RemoteLinkStep.NODES;
    } else {
      step = RemoteLinkStep.NETWORKS;
      networkId = Optional.empty();
    }
    node = Optional.empty();
  }

  private void choose(ServerPlayer player, int line) {
    switch (step) {
      case NETWORKS -> chooseNetwork(player, line);
      case NODES -> chooseNode(player, line);
      case SLOTS -> chooseSlot(player, line);
    }
  }

  private void chooseNetwork(ServerPlayer player, int line) {
    if (line < 0 || line >= networks.size()) {
      return;
    }
    networkId = Optional.of(networks.get(line).id());
    step = RemoteLinkStep.NODES;
    refresh(player);
  }

  /** A node with a single input slot skips the step of the slots (answer 286). */
  private void chooseNode(ServerPlayer player, int line) {
    List<NodeRow> nodes = network.map(RemoteLinkLines::inputNodes).orElse(List.of());
    if (line < 0 || line >= nodes.size()) {
      return;
    }
    node = Optional.of(nodes.get(line));
    List<SlotRow> slots = slotsNow();
    if (slots.size() == 1) {
      link(player, slots.get(0));
      return;
    }
    step = RemoteLinkStep.SLOTS;
  }

  private void chooseSlot(ServerPlayer player, int line) {
    List<SlotRow> slots = slotsNow();
    if (line >= 0 && line < slots.size()) {
      link(player, slots.get(line));
    }
  }

  private void link(ServerPlayer player, SlotRow slot) {
    if (RemoteLinking.link(
        player, stack(player), networkId.orElseThrow(), node.orElseThrow(), slot)) {
      player.closeContainer();
    }
  }

  /** Only while the player holds a Signal Remote in that hand. */
  @Override
  public boolean stillValid(Player player) {
    return stack(player).getItem() instanceof SignalRemoteItem;
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    return ItemStack.EMPTY;
  }

  private ItemStack stack(Player player) {
    return player.getItemInHand(hand);
  }

  private int modeOf(Player player) {
    return RemoteLinking.stateOf(stack(player)).mode().ordinal();
  }

  void acceptRows(RemoteLinkRows received) {
    rows = received;
  }

  /** The step on the server, which the rows reach the client on the next tick. */
  public RemoteLinkStep step() {
    return step;
  }

  public RemoteLinkRows rows() {
    return rows;
  }
}
