package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.application.TerminalPlacement;
import io.github.marcelofabianov.redweave.block.BlockLocations;
import io.github.marcelofabianov.redweave.block.NetworkTerminalPlacement;
import io.github.marcelofabianov.redweave.block.TerminalMessages;
import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.util.FakePlayer;

/**
 * The item of the Network Terminal: placing it asks the server for the one terminal of the player
 * (answers 137, 175 and 253), and the block keeps who placed it.
 */
public final class NetworkTerminalItem extends BlockItem {
  public NetworkTerminalItem(Block block, Properties properties) {
    super(block, properties);
  }

  @Override
  public InteractionResult place(BlockPlaceContext context) {
    if (context.getLevel().isClientSide) {
      return super.place(context);
    }
    Player player = context.getPlayer();
    Optional<RedweaveServer> server = RedweaveServer.running();
    MinecraftServer game = context.getLevel().getServer();
    if (player == null
        || player instanceof FakePlayer
        || server.isEmpty()
        || game == null
        || !fits(context)) {
      return refuse(player);
    }
    PlayerId owner = new PlayerId(player.getUUID());
    BlockLocation location = BlockLocations.of(context.getLevel(), context.getClickedPos());
    TerminalPlacement placed = NetworkTerminalPlacement.ask(server.get(), game, owner, location);
    if (placed.outcome() != Outcome.ACCEPTED) {
      placed
          .existing()
          .ifPresent(
              existing -> player.displayClientMessage(TerminalMessages.limit(existing), true));
      return refuse(player);
    }
    return placeBlock(server.get(), context, player, location);
  }

  /** The checks the game makes before placing, so the terminal is not registered for nothing. */
  private boolean fits(BlockPlaceContext context) {
    return context.canPlace() && getPlacementState(context) != null;
  }

  /**
   * The client already placed the block and spent the item on its side, so the server sends the
   * inventory back, or the player would see one item less (rule 16).
   */
  private static InteractionResult refuse(Player player) {
    if (player instanceof ServerPlayer serverPlayer) {
      serverPlayer.containerMenu.sendAllDataToRemote();
    }
    return InteractionResult.FAIL;
  }

  /** If the game still refuses the block, the player is freed again. */
  private InteractionResult placeBlock(
      RedweaveServer server, BlockPlaceContext context, Player player, BlockLocation location) {
    InteractionResult result = super.place(context);
    PlayerId owner = new PlayerId(player.getUUID());
    if (!result.consumesAction()) {
      server.terminal().breakTerminal().broke(owner, location);
      return result;
    }
    NetworkTerminalPlacement.own(context.getLevel(), context.getClickedPos(), player.getUUID());
    return result;
  }
}
