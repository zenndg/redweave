package io.github.zenndg.redweave.block.part;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The room the copper ring and the push button of a Signal Button take over its plate, on each side
 * of the space, so the player aims at the button too: the ring, up to the top of the button when
 * released (tmp/arte/signal_button/prompt.md).
 */
final class ButtonShapes {
  private static final double EDGE = 6;
  private static final double FAR_EDGE = 12;
  private static final double HEIGHT = 4;
  private static final RaisedShapes SHAPES = new RaisedShapes(EDGE, FAR_EDGE, HEIGHT);

  private ButtonShapes() {}

  /** The room on the side of the space the button rests on. */
  static VoxelShape of(Direction face) {
    return SHAPES.of(face);
  }
}
