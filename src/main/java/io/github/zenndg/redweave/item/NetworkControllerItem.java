package io.github.zenndg.redweave.item;

import io.github.zenndg.redweave.application.PlacedController;
import io.github.zenndg.redweave.block.BlockLocations;
import io.github.zenndg.redweave.block.ControllerLink;
import io.github.zenndg.redweave.block.ControllerMessages;
import io.github.zenndg.redweave.config.NeoForgeNetworkLimits;
import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.registry.RedweaveDataComponents;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.util.FakePlayer;

/**
 * The Network Controller as an item. On the server, the network is decided before the block goes
 * into the world, so a refusal places nothing and keeps the item (answers 55 to 58).
 */
public final class NetworkControllerItem extends BlockItem {
  public NetworkControllerItem(Block block, Properties properties) {
    super(block, properties);
  }

  @Override
  public InteractionResult place(BlockPlaceContext context) {
    if (context.getLevel().isClientSide) {
      return super.place(context);
    }
    Player player = context.getPlayer();
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (player == null || player instanceof FakePlayer || server.isEmpty() || !fits(context)) {
      return refuse(player);
    }
    PlacedController placed = decide(server.get(), context, player);
    if (placed.network().isEmpty()) {
      ControllerMessages.refusal(placed.outcome(), new NeoForgeNetworkLimits().networksPerPlayer())
          .ifPresent(message -> player.displayClientMessage(message, true));
      return refuse(player);
    }
    return placeBlock(server.get(), context, player, placed.network().get());
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

  /** The checks the game makes before placing, so a network is not born for nothing. */
  private boolean fits(BlockPlaceContext context) {
    return context.canPlace() && getPlacementState(context) != null;
  }

  private static PlacedController decide(
      RedweaveServer server, BlockPlaceContext context, Player player) {
    Optional<NetworkId> stored =
        Optional.ofNullable(context.getItemInHand().get(RedweaveDataComponents.NETWORK.get()));
    return server
        .placeController()
        .place(
            new PlayerId(player.getUUID()),
            stored,
            BlockLocations.of(context.getLevel(), context.getClickedPos()));
  }

  /** If the game still refuses the block, the network goes back to unplaced (answer 49). */
  private InteractionResult placeBlock(
      RedweaveServer server, BlockPlaceContext context, Player player, NetworkId network) {
    InteractionResult result = super.place(context);
    if (!result.consumesAction()) {
      server.loseController().lose(network);
      return result;
    }
    boolean online = server.network(network).map(Network::isOnline).orElse(false);
    ControllerLink.link(
        context.getLevel(), context.getClickedPos(), network, player.getUUID(), online);
    return result;
  }
}
