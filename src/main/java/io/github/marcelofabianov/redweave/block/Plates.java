package io.github.marcelofabianov.redweave.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;

/**
 * The plates of the mod, the Signal Node and the Signal Modules, stick to any face of a block that
 * is not air, and fall when it is gone (answers 74, 313, 394 and 416).
 */
final class Plates {
  private Plates() {}

  /** Whether the block behind a plate stuck on `facing` holds it. */
  static boolean isHeld(LevelReader level, BlockPos pos, Direction facing) {
    return !level.getBlockState(pos.relative(facing.getOpposite())).isAir();
  }

  /** Whether the change came from the block that holds the plate and it no longer does. */
  static boolean falls(LevelReader level, BlockPos pos, Direction facing, Direction changed) {
    return changed == facing.getOpposite() && !isHeld(level, pos, facing);
  }
}
