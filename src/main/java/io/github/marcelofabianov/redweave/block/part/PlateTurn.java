package io.github.marcelofabianov.redweave.block.part;

import java.util.List;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;

/**
 * How the model of a plate turns from the floor to its place, in turns on `x` and `y` as a
 * blockstate gave them (answers 429 and 536). A module plate lies with its arrow to the north, or
 * to the east when its top is turned 90° on the UV: `x` and `y` give 16 of the 24 pairs of `facing`
 * and `front`, and the turned top gives the other 8. The turns are found with the math of the game,
 * without the client class BlockModelRotation, so datagen and the server use them too (rule 19).
 *
 * @param turned whether the model with the top turned 90° is used
 */
public record PlateTurn(int x, int y, boolean turned) {
  private static final int QUARTER = 90;
  private static final int HALF = 180;
  private static final int THREE_QUARTERS = 270;
  private static final int FULL = 360;
  private static final List<Integer> ANGLES = List.of(0, QUARTER, HALF, THREE_QUARTERS);
  private static final String NO_TURN = "No turn lays a plate on %s with its front to %s";
  private static final List<Boolean> TOPS = List.of(false, true);

  /** A pair that is not across, which a plate never has, shows as the default one. */
  public static PlateTurn of(Direction facing, Direction front) {
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

  /**
   * The turns of a plate with no front, as the Signal Node, which `directionalBlock` gave: the
   * model on the floor points up (answer 72).
   */
  public static PlateTurn facing(Direction facing) {
    int x = facing == Direction.DOWN ? HALF : facing.getAxis().isHorizontal() ? QUARTER : 0;
    int y = facing.getAxis().isVertical() ? 0 : ((int) facing.toYRot() + HALF) % FULL;
    return new PlateTurn(x, y, false);
  }

  /** The default front of a plate on the facing, across it. */
  public static Direction acrossOf(Direction facing) {
    return facing.getAxis() == Direction.Axis.Y ? Direction.NORTH : Direction.UP;
  }

  private static boolean lands(int x, int y, boolean turned, Direction facing, Direction front) {
    Matrix4f turn =
        new Matrix4f().rotateYXZ((float) Math.toRadians(-y), (float) Math.toRadians(-x), 0);
    Direction arrow = turned ? Direction.EAST : Direction.NORTH;
    return Direction.rotate(turn, Direction.UP) == facing && Direction.rotate(turn, arrow) == front;
  }
}
