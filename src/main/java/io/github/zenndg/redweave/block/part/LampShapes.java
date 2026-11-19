package io.github.zenndg.redweave.block.part;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The shape of the Signal Lamp, as its model: a copper base of 6 × 12 px with a glass dome on it, 5
 * px high in all, 12 px long along its front and 6 px wide, centered on the side of the space it
 * rests on (answers 652, 820, 824 and 829).
 */
final class LampShapes {
  private static final double FULL = 16;
  private static final double THICKNESS = 5;
  private static final double LONG_EDGE = 2;
  private static final double LONG_FAR_EDGE = 14;
  private static final double WIDE_EDGE = 5;
  private static final double WIDE_FAR_EDGE = 11;

  private LampShapes() {}

  /** The bar on the face, long along the axis of the front. */
  static VoxelShape of(Direction face, Direction front) {
    double[] from = new double[Direction.Axis.values().length];
    double[] to = new double[Direction.Axis.values().length];
    for (Direction.Axis axis : Direction.Axis.values()) {
      int index = axis.ordinal();
      if (axis == face.getAxis()) {
        boolean low = face.getAxisDirection() == Direction.AxisDirection.NEGATIVE;
        from[index] = low ? 0 : FULL - THICKNESS;
        to[index] = low ? THICKNESS : FULL;
      } else {
        boolean along = axis == front.getAxis();
        from[index] = along ? LONG_EDGE : WIDE_EDGE;
        to[index] = along ? LONG_FAR_EDGE : WIDE_FAR_EDGE;
      }
    }
    int x = Direction.Axis.X.ordinal();
    int y = Direction.Axis.Y.ordinal();
    int z = Direction.Axis.Z.ordinal();
    return Block.box(from[x], from[y], from[z], to[x], to[y], to[z]);
  }
}
