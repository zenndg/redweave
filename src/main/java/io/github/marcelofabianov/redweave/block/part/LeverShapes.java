package io.github.marcelofabianov.redweave.block.part;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
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
  private static final double FULL = 16;
  private static final double BACK = FULL - HEIGHT;

  private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

  static {
    SHAPES.put(Direction.DOWN, Block.box(EDGE, 0, EDGE, FAR_EDGE, HEIGHT, FAR_EDGE));
    SHAPES.put(Direction.UP, Block.box(EDGE, BACK, EDGE, FAR_EDGE, FULL, FAR_EDGE));
    SHAPES.put(Direction.NORTH, Block.box(EDGE, EDGE, 0, FAR_EDGE, FAR_EDGE, HEIGHT));
    SHAPES.put(Direction.SOUTH, Block.box(EDGE, EDGE, BACK, FAR_EDGE, FAR_EDGE, FULL));
    SHAPES.put(Direction.WEST, Block.box(0, EDGE, EDGE, HEIGHT, FAR_EDGE, FAR_EDGE));
    SHAPES.put(Direction.EAST, Block.box(BACK, EDGE, EDGE, FULL, FAR_EDGE, FAR_EDGE));
  }

  private LeverShapes() {}

  /** The room on the side of the space the lever rests on. */
  static VoxelShape of(Direction face) {
    return SHAPES.get(face);
  }
}
