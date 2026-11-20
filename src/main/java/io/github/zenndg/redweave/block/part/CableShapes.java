package io.github.zenndg.redweave.block.part;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * The shape of a Signal Cable for the aim and the outline, which follows what is drawn: the center
 * of 4 × 3 and each arm, inside its own space, but for the arm that reaches the plate of a node or
 * a module beside it, 3 px past the edge (answers 521, 571 and 575). The boxes are the ones of the
 * models, on the floor and pointing north, turned as the pieces are.
 */
final class CableShapes {
  /** The pixels of a block, and its center, which the turns go around. */
  private static final float PIXELS = 16;

  private static final float MIDDLE = PIXELS / 2;
  private static final float LEFT = 6;
  private static final float RIGHT = 10;
  private static final float HEIGHT = 3;
  private static final float CENTER_NEAR = 6;
  private static final float CENTER_FAR = 10;
  private static final float SHORT_START = 3;
  private static final float PAST_EDGE = -3;
  private static final float ROUNDING = 1024;
  private static final Box CENTER = new Box(LEFT, 0, CENTER_NEAR, RIGHT, HEIGHT, CENTER_FAR);
  private static final Box ARM = new Box(LEFT, 0, 0, RIGHT, HEIGHT, CENTER_NEAR);
  private static final Box SHORT = new Box(LEFT, 0, SHORT_START, RIGHT, HEIGHT, CENTER_NEAR);
  private static final Box RISER = new Box(LEFT, 0, 0, RIGHT, PIXELS, HEIGHT);
  private static final Box TO_PLATE = new Box(LEFT, 0, PAST_EDGE, RIGHT, HEIGHT, CENTER_NEAR);
  private static final Map<CableReach, List<Box>> ARMS = new EnumMap<>(CableReach.class);
  private static final Map<Key, VoxelShape> SHAPES = new ConcurrentHashMap<>();

  static {
    ARMS.put(CableReach.EDGE, List.of(ARM));
    ARMS.put(CableReach.SHORT, List.of(SHORT));
    ARMS.put(CableReach.CORNER, List.of(ARM));
    ARMS.put(CableReach.STEP, List.of(SHORT, RISER));
    ARMS.put(CableReach.PLATE, List.of(TO_PLATE));
  }

  private CableShapes() {}

  /** A box in pixels, on the floor and pointing north. */
  private record Box(float x1, float y1, float z1, float x2, float y2, float z2) {}

  private record Key(Direction face, Map<Direction, CableReach> arms) {}

  /** The shape of a cable on the side with the arms, kept once built. */
  static VoxelShape of(Direction face, Map<Direction, CableReach> arms) {
    return SHAPES.computeIfAbsent(new Key(face, Map.copyOf(arms)), CableShapes::build);
  }

  private static VoxelShape build(Key key) {
    Direction across = CableLinks.across(key.face()).getFirst();
    VoxelShape shape = turned(CENTER, CableTurn.of(key.face(), across));
    for (Map.Entry<Direction, CableReach> arm : key.arms().entrySet()) {
      Matrix4f turn = CableTurn.of(key.face(), arm.getKey());
      for (Box box : ARMS.get(arm.getValue())) {
        shape = Shapes.or(shape, turned(box, turn));
      }
    }
    return shape.optimize();
  }

  private static VoxelShape turned(Box box, Matrix4f turn) {
    Vector3f from = point(box.x1(), box.y1(), box.z1(), turn);
    Vector3f to = point(box.x2(), box.y2(), box.z2(), turn);
    return Shapes.box(
        Math.min(from.x, to.x),
        Math.min(from.y, to.y),
        Math.min(from.z, to.z),
        Math.max(from.x, to.x),
        Math.max(from.y, to.y),
        Math.max(from.z, to.z));
  }

  /** A corner of a box, turned around the center of the block, in blocks. */
  private static Vector3f point(float x, float y, float z, Matrix4f turn) {
    Vector3f point = turn.transformPosition(new Vector3f(x - MIDDLE, y - MIDDLE, z - MIDDLE));
    return new Vector3f(rounded(point.x), rounded(point.y), rounded(point.z));
  }

  private static float rounded(float pixels) {
    return Math.round((pixels + MIDDLE) / PIXELS * ROUNDING) / ROUNDING;
  }
}
