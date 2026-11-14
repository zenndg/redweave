package io.github.marcelofabianov.redweave.datagen;

import java.util.List;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;

/**
 * How the model of a module plate turns for a pair of `facing` and `front` (answer 429). The model
 * lies on the floor with its arrow to the north, or to the east when its top is turned 90° on the
 * UV. A blockstate only turns a model on `x` and `y`, which gives 16 of the 24 pairs, and the
 * turned top gives the other 8. The turns are found with the math of the game, BlockModelRotation,
 * which datagen cannot load because it is a client class (rule 19).
 *
 * @param turned whether the model with the top turned 90° is used
 */
record PlateTurn(int x, int y, boolean turned) {
  private static final int QUARTER = 90;
  private static final int HALF = 180;
  private static final int THREE_QUARTERS = 270;
  private static final List<Integer> ANGLES = List.of(0, QUARTER, HALF, THREE_QUARTERS);
  private static final String NO_TURN = "No turn lays a plate on %s with its front to %s";
  private static final List<Boolean> TOPS = List.of(false, true);

  /** A pair that is not across, which the block never places, shows as the default one. */
  static PlateTurn of(Direction facing, Direction front) {
    Direction across = front.getAxis() == facing.getAxis() ? acrossOf(facing) : front;
    for (boolean turned : TOPS) {
      for (int x : ANGLES) {
        for (int y : ANGLES) {
          if (lands(x, y, turned, facing, across)) {
            return new PlateTurn(x, y, turned);
          }
        }
      }
    }
    throw new IllegalStateException(String.format(NO_TURN, facing, across));
  }

  private static boolean lands(int x, int y, boolean turned, Direction facing, Direction front) {
    Matrix4f turn =
        new Matrix4f().rotateYXZ((float) Math.toRadians(-y), (float) Math.toRadians(-x), 0);
    Direction arrow = turned ? Direction.EAST : Direction.NORTH;
    return Direction.rotate(turn, Direction.UP) == facing && Direction.rotate(turn, arrow) == front;
  }

  private static Direction acrossOf(Direction facing) {
    return facing.getAxis() == Direction.Axis.Y ? Direction.NORTH : Direction.UP;
  }
}
