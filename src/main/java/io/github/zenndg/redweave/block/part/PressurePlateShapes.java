package io.github.zenndg.redweave.block.part;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The room the tread of a Signal Pressure Plate takes over its plate, so the player aims at it too:
 * the 6 × 6 tread, up to its top when nothing stands on it
 * (tmp/arte/signal_pressure_plate/prompt.md).
 */
final class PressurePlateShapes {
  private static final double EDGE = 6;
  private static final double FAR_EDGE = 12;
  private static final double HEIGHT = 3;
  private static final RaisedShapes SHAPES = new RaisedShapes(EDGE, FAR_EDGE, HEIGHT);

  private PressurePlateShapes() {}

  /** The room on the side of the space the plate rests on. */
  static VoxelShape of(Direction face) {
    return SHAPES.of(face);
  }
}
