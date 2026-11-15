package io.github.marcelofabianov.redweave.block.part;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Direction;
import org.joml.Matrix4f;

/**
 * The turn that takes a piece of a Signal Cable from the floor, pointing north, to the side it
 * rests on, pointing toward a direction across it. It is found among the quarter turns with the
 * math of the game, so the shape on the server and the model on the client turn alike (rule 19).
 */
public final class CableTurn {
  private static final int QUARTER = 90;
  private static final int HALF = 180;
  private static final int THREE_QUARTERS = 270;
  private static final List<Integer> ANGLES = List.of(0, QUARTER, HALF, THREE_QUARTERS);
  private static final String NO_TURN = "No turn lays a cable piece on %s toward %s";
  private static final Map<Long, Matrix4f> TURNS = new ConcurrentHashMap<>();
  private static final int KEY_SHIFT = 8;

  private CableTurn() {}

  /** A copy of the turn, about the center of the block, for the piece on the side, toward. */
  public static Matrix4f of(Direction face, Direction toward) {
    long key = ((long) face.ordinal() << KEY_SHIFT) | toward.ordinal();
    return new Matrix4f(TURNS.computeIfAbsent(key, found -> find(face, toward)));
  }

  private static Matrix4f find(Direction face, Direction toward) {
    for (int x : ANGLES) {
      for (int y : ANGLES) {
        for (int z : ANGLES) {
          Matrix4f turn =
              new Matrix4f()
                  .rotateYXZ(
                      (float) Math.toRadians(y),
                      (float) Math.toRadians(x),
                      (float) Math.toRadians(z));
          if (Direction.rotate(turn, Direction.UP) == face.getOpposite()
              && Direction.rotate(turn, Direction.NORTH) == toward) {
            return turn;
          }
        }
      }
    }
    throw new IllegalArgumentException(String.format(NO_TURN, face, toward));
  }
}
