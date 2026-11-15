package io.github.marcelofabianov.redweave.block.part;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The room the 3D pieces of a source module take over its plate, on each side of the space, so the
 * player aims at them too, as the handle of the Signal Lever and the push button of the Signal
 * Button: a square in the middle of the plate, up to the top of its tallest piece.
 */
final class RaisedShapes {
  private static final double FULL = 16;

  private final Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);

  /** The square from `edge` to `farEdge` on the plate, `height` pixels from the face. */
  RaisedShapes(double edge, double farEdge, double height) {
    double back = FULL - height;
    shapes.put(Direction.DOWN, Block.box(edge, 0, edge, farEdge, height, farEdge));
    shapes.put(Direction.UP, Block.box(edge, back, edge, farEdge, FULL, farEdge));
    shapes.put(Direction.NORTH, Block.box(edge, edge, 0, farEdge, farEdge, height));
    shapes.put(Direction.SOUTH, Block.box(edge, edge, back, farEdge, farEdge, FULL));
    shapes.put(Direction.WEST, Block.box(0, edge, edge, height, farEdge, farEdge));
    shapes.put(Direction.EAST, Block.box(back, edge, edge, FULL, farEdge, farEdge));
  }

  /** The room on the side of the space the module rests on. */
  VoxelShape of(Direction face) {
    return shapes.get(face);
  }
}
