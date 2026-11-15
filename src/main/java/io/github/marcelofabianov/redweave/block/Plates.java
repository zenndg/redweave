package io.github.marcelofabianov.redweave.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelReader;

/**
 * The plates of the mod, the Signal Node and the Signal Modules, stick to any face of a block that
 * is not air, and fall when it is gone (answers 74, 313, 394 and 416).
 */
public final class Plates {
  private Plates() {}

  /** Whether the block behind a plate stuck on `facing` holds it. */
  public static boolean isHeld(LevelReader level, BlockPos pos, Direction facing) {
    return !level.getBlockState(pos.relative(facing.getOpposite())).isAir();
  }

  /** Whether the change came from the block that holds the plate and it no longer does. */
  public static boolean falls(
      LevelReader level, BlockPos pos, Direction facing, Direction changed) {
    return changed == facing.getOpposite() && !isHeld(level, pos, facing);
  }

  /**
   * The front of a plate turned 90° clockwise, seen from the front of the plate: the cross product
   * of the front and the face it sticks on (answer 403).
   */
  public static Direction clockwise(Direction facing, Direction front) {
    return across(front.getNormal().cross(facing.getNormal()));
  }

  /** The front of a plate turned 90° counterclockwise, seen from its front (answer 616). */
  public static Direction counterclockwise(Direction facing, Direction front) {
    return across(facing.getNormal().cross(front.getNormal()));
  }

  private static Direction across(Vec3i turned) {
    return Direction.fromDelta(turned.getX(), turned.getY(), turned.getZ());
  }
}
