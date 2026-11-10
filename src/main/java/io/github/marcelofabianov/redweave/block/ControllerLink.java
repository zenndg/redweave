package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Ties a placed controller block to its network and lights its core (answer 64). */
public final class ControllerLink {
  private ControllerLink() {}

  public static void link(Level level, BlockPos pos, NetworkId network, UUID owner) {
    if (level.getBlockEntity(pos) instanceof NetworkControllerBlockEntity controller) {
      controller.link(network, owner);
    }
    BlockState state = level.getBlockState(pos);
    if (state.hasProperty(NetworkControllerBlock.LINKED)) {
      level.setBlock(pos, state.setValue(NetworkControllerBlock.LINKED, true), Block.UPDATE_ALL);
    }
  }
}
