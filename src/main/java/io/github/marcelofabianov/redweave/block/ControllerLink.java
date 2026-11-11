package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Ties a placed controller block to its network and lights its core while the network is online, so
 * a network turned off shows its core off (answers 64 and 177).
 */
public final class ControllerLink {
  private ControllerLink() {}

  public static void link(
      Level level, BlockPos pos, NetworkId network, UUID owner, boolean online) {
    if (level.getBlockEntity(pos) instanceof NetworkControllerBlockEntity controller) {
      controller.link(network, owner);
    }
    light(level, pos, online);
  }

  /**
   * Lights the core of a loaded controller by the state its network has now, as after the world
   * opens with a network turned off (answer 177). Only a change reaches the clients.
   */
  public static void refreshCore(RedweaveServer server, Level level, BlockPos pos) {
    if (level.getBlockEntity(pos) instanceof NetworkControllerBlockEntity controller) {
      controller
          .network()
          .flatMap(server::network)
          .ifPresent(network -> light(level, pos, network.isOnline()));
    }
  }

  private static void light(Level level, BlockPos pos, boolean online) {
    BlockState state = level.getBlockState(pos);
    if (state.hasProperty(NetworkControllerBlock.LINKED)
        && state.getValue(NetworkControllerBlock.LINKED) != online) {
      level.setBlock(pos, state.setValue(NetworkControllerBlock.LINKED, online), Block.UPDATE_ALL);
    }
  }
}
