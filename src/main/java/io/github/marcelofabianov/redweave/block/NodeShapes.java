package io.github.marcelofabianov.redweave.block;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.VoxelShape;

/** The thin plate of a node, about 2 pixels thick, on each face of its block (visual.md). */
public final class NodeShapes {
  private static final double EDGE = 3;
  private static final double FAR_EDGE = 13;
  private static final double THICKNESS = 2;
  private static final double FULL = 16;
  private static final double BACK = FULL - THICKNESS;

  private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

  static {
    SHAPES.put(Direction.UP, Block.box(EDGE, 0, EDGE, FAR_EDGE, THICKNESS, FAR_EDGE));
    SHAPES.put(Direction.DOWN, Block.box(EDGE, BACK, EDGE, FAR_EDGE, FULL, FAR_EDGE));
    SHAPES.put(Direction.SOUTH, Block.box(EDGE, EDGE, 0, FAR_EDGE, FAR_EDGE, THICKNESS));
    SHAPES.put(Direction.NORTH, Block.box(EDGE, EDGE, BACK, FAR_EDGE, FAR_EDGE, FULL));
    SHAPES.put(Direction.EAST, Block.box(0, EDGE, EDGE, THICKNESS, FAR_EDGE, FAR_EDGE));
    SHAPES.put(Direction.WEST, Block.box(BACK, EDGE, EDGE, FULL, FAR_EDGE, FAR_EDGE));
  }

  private NodeShapes() {}

  public static VoxelShape of(Direction stuckTo) {
    return SHAPES.get(stuckTo);
  }
}
