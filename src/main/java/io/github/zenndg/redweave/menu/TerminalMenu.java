package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.application.TerminalRow;
import io.github.zenndg.redweave.block.NetworkTerminalBlockEntity;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.registry.RedweaveBlocks;
import io.github.zenndg.redweave.registry.RedweaveMenus;
import io.github.zenndg.redweave.server.RedweaveServer;
import io.github.zenndg.redweave.server.TerminalServices;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * The menu behind the screen of the Network Terminal: terminal-de-rede.md. Only the owner uses it,
 * within reach of the terminal (answers 134 and 153, and rule 17). The client names a network by
 * its row in the list this menu keeps, never by its ID, and the domain checks each action.
 */
public final class TerminalMenu extends AbstractContainerMenu {
  private final BlockPos pos;
  private final ContainerLevelAccess access;
  private final Optional<ServerPlayer> viewer;
  private TerminalScreenRows rows;
  private List<TerminalRow> found = List.of();

  /** On the server, for the player who clicked the terminal. */
  public TerminalMenu(int id, ServerPlayer player, BlockPos pos) {
    super(RedweaveMenus.TERMINAL.get(), id);
    this.pos = pos;
    this.access = ContainerLevelAccess.create(player.level(), pos);
    this.viewer = Optional.of(player);
    this.found = read(player);
    this.rows = TerminalScreenRows.of(id, found);
  }

  /** On the client, with the position and the rows the server sent when opening. */
  public TerminalMenu(int id, Inventory inventory, RegistryFriendlyByteBuf extra) {
    super(RedweaveMenus.TERMINAL.get(), id);
    this.pos = extra.readBlockPos();
    this.access = ContainerLevelAccess.NULL;
    this.viewer = Optional.empty();
    this.rows = TerminalScreenRows.STREAM_CODEC.decode(extra).forMenu(id);
  }

  /** What the server writes when it opens the screen; the client puts its own menu ID. */
  public static void writeOpening(
      RegistryFriendlyByteBuf extra, ServerPlayer player, BlockPos pos) {
    extra.writeBlockPos(pos);
    TerminalScreenRows.STREAM_CODEC.encode(extra, TerminalScreenRows.of(0, read(player)));
  }

  /** The networks of the player, as the terminal lists them; none without a running server. */
  private static List<TerminalRow> read(Player player) {
    return RedweaveServer.running()
        .map(server -> server.terminal().terminalNetworks().of(new PlayerId(player.getUUID())))
        .orElse(List.of());
  }

  /** Sends the rows again only when they changed (rule 18). */
  @Override
  public void broadcastChanges() {
    super.broadcastChanges();
    viewer.ifPresent(player -> {
      found = read(player);
      TerminalScreenRows now = TerminalScreenRows.of(containerId, found);
      if (!now.equals(rows)) {
        rows = now;
        PacketDistributor.sendToPlayer(player, now);
      }
    });
  }

  /** A button is a row and an action; the domain refuses what the player may not do. */
  @Override
  public boolean clickMenuButton(Player player, int button) {
    Optional<TerminalScreenCodes.Click> click = TerminalScreenCodes.clickOf(button);
    Optional<NetworkId> network = click.flatMap(chosen -> networkAt(chosen.row()));
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (network.isEmpty() || server.isEmpty() || !ownedBy(player)) {
      return false;
    }
    act(server.get(), player, click.get().action(), network.get());
    return true;
  }

  private void act(RedweaveServer server, Player player, TerminalAction action, NetworkId id) {
    PlayerId owner = new PlayerId(player.getUUID());
    TerminalServices terminal = server.terminal();
    switch (action) {
      case OPEN -> open(server, player, id);
      case TURN_OFF -> terminal.turnNetwork().off(owner, id);
      case TURN_ON -> terminal.turnNetwork().on(owner, id);
      case CLEAR_COMPONENTS -> terminal.clearComponents().clear(owner, id);
      case DELETE -> terminal.deleteNetwork().delete(owner, id);
    }
  }

  /** Only an online network opens its Controller Screen, held at this terminal (158 and 259). */
  private void open(RedweaveServer server, Player player, NetworkId id) {
    server
        .network(id)
        .filter(network -> network.isOnline() && network.isOwner(new PlayerId(player.getUUID())))
        .ifPresent(network -> {
          if (player instanceof ServerPlayer serverPlayer) {
            ControllerMenu.openAtTerminal(serverPlayer, pos, network);
          }
        });
  }

  /** The owner renames a network of a row (answer 156); the domain checks the name. */
  public void rename(Player player, int row, String name) {
    Optional<RedweaveServer> server = RedweaveServer.running();
    Optional<NetworkId> network = networkAt(row);
    if (server.isPresent() && network.isPresent() && ownedBy(player)) {
      server.get().renameNetwork().rename(new PlayerId(player.getUUID()), network.get(), name);
    }
  }

  private Optional<NetworkId> networkAt(int row) {
    return row >= 0 && row < found.size() ? Optional.of(found.get(row).id()) : Optional.empty();
  }

  /** Only the owner of the terminal, within reach of it (answers 134 and 153). */
  @Override
  public boolean stillValid(Player player) {
    return stillValid(access, player, RedweaveBlocks.NETWORK_TERMINAL.get()) && ownedBy(player);
  }

  /** On the client there is no block to check, and the server closes the screen. */
  private boolean ownedBy(Player player) {
    if (viewer.isEmpty()) {
      return true;
    }
    return player.level().getBlockEntity(pos) instanceof NetworkTerminalBlockEntity terminal
        && terminal.isOwner(player);
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    return ItemStack.EMPTY;
  }

  void acceptRows(TerminalScreenRows received) {
    rows = received;
  }

  public BlockPos pos() {
    return pos;
  }

  public List<TerminalScreenRows.NetworkLine> networks() {
    return rows.networks();
  }
}
