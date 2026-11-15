package io.github.marcelofabianov.redweave.block.part;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The room the base and the handle of a Signal Lever take over its plate, on each side of the
 * space, so the player aims at the handle too: the middle of the plate, up to the top of the tilted
 * handle.
 */
final class LeverShapes {
  private static final double EDGE = 4;
  private static final double FAR_EDGE = 12;
  private static final double HEIGHT = 13;
  private static final RaisedShapes SHAPES = new RaisedShapes(EDGE, FAR_EDGE, HEIGHT);

  private LeverShapes() {}

  /** The room on the side of the space the lever rests on. */
  static VoxelShape of(Direction face) {
    return SHAPES.of(face);
  }
}
