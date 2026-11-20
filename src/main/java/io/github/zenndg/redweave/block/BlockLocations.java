package io.github.zenndg.redweave.block;

import io.github.zenndg.redweave.domain.network.BlockLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/** Translates a block of the world into the location of the domain. */
public final class BlockLocations {
  private BlockLocations() {}

  public static BlockLocation of(Level level, BlockPos pos) {
    return new BlockLocation(
        level.dimension().location().toString(), pos.getX(), pos.getY(), pos.getZ());
  }
}
