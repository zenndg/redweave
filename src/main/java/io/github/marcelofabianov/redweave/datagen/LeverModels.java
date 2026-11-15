package io.github.marcelofabianov.redweave.datagen;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;

/**
 * The 3D pieces of the Signal Lever over its plate, from `signal_lever_parts.png`
 * (tmp/arte/signal_lever/prompt.md): the copper base, the steel handle with its knob, tilted to the
 * front when on and to the back when off, and the light, red off and green on, which glows (answers
 * 548, 554 and 581). The model lies with its front to the north, or to the east when its top is
 * turned, as every module plate (answer 429).
 */
final class LeverModels {
  private static final String PARTS_KEY = "parts";
  private static final String PARTS_REF = "#parts";
  private static final float FULL = 16;
  private static final float CENTER = 8;
  private static final float HANDLE_FOOT = 4;
  private static final float TILT = 22.5F;
  private static final int GLOW = 15;
  private static final Cube BASE = new Cube(6, 2, 6, 10, 4, 10);
  private static final Cube HANDLE = new Cube(7, 4, 7, 9, 11, 9);
  private static final Cube KNOB = new Cube(7, 11, 7, 9, 13, 9);
  private static final Cube LIGHT = new Cube(4, 2, 10, 6, 3, 12);
  private static final Uv BASE_TOP = new Uv(0, 0, 4, 4);
  private static final Uv BASE_SIDE = new Uv(0, 4, 4, 6);
  private static final Uv HANDLE_SIDE = new Uv(6, 0, 8, 7);
  private static final Uv KNOB_SIDE = new Uv(6, 7, 8, 9);
  private static final Uv KNOB_TOP = new Uv(9, 0, 11, 2);
  private static final Uv RED_TOP = new Uv(12, 0, 14, 2);
  private static final Uv RED_SIDE = new Uv(12, 2, 14, 3);
  private static final Uv GREEN_TOP = new Uv(12, 4, 14, 6);
  private static final Uv GREEN_SIDE = new Uv(12, 6, 14, 7);
  private static final Direction[] SIDES = {
    Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST
  };

  private LeverModels() {}

  /** Adds the pieces of the lever, on or off, to its plate, turned with it or not. */
  static void addPieces(
      BlockModelBuilder model, ResourceLocation parts, boolean on, boolean turned) {
    model.texture(PARTS_KEY, parts);
    piece(model, BASE.turned(turned), BASE_TOP, BASE_SIDE, turned);
    tilted(piece(model, HANDLE.turned(turned), null, HANDLE_SIDE, turned), on, turned);
    tilted(piece(model, KNOB.turned(turned), KNOB_TOP, KNOB_SIDE, turned), on, turned);
    piece(model, LIGHT.turned(turned), on ? GREEN_TOP : RED_TOP, on ? GREEN_SIDE : RED_SIDE, turned)
        .emissivity(GLOW, GLOW);
  }

  /** A box with its top, if any, and its four sides from the parts texture. */
  private static BlockModelBuilder.ElementBuilder piece(
      BlockModelBuilder model, Cube cube, Uv top, Uv side, boolean turned) {
    BlockModelBuilder.ElementBuilder element =
        model.element().from(cube.x1(), cube.y1(), cube.z1()).to(cube.x2(), cube.y2(), cube.z2());
    if (top != null) {
      BlockModelBuilder.ElementBuilder.FaceBuilder face =
          element.face(Direction.UP).uvs(top.u1(), top.v1(), top.u2(), top.v2()).texture(PARTS_REF);
      if (turned) {
        face.rotation(ModelBuilder.FaceRotation.CLOCKWISE_90);
      }
    }
    for (Direction direction : SIDES) {
      element.face(direction).uvs(side.u1(), side.v1(), side.u2(), side.v2()).texture(PARTS_REF);
    }
    return element;
  }

  /**
   * The handle and its knob lean from the foot of the handle: toward the front when on and toward
   * the back when off. A negative angle leans the top toward the north on `x`, and toward the east
   * on `z`, the front of the turned model.
   */
  private static void tilted(BlockModelBuilder.ElementBuilder element, boolean on, boolean turned) {
    element
        .rotation()
        .origin(CENTER, HANDLE_FOOT, CENTER)
        .axis(turned ? Direction.Axis.Z : Direction.Axis.X)
        .angle(on ? -TILT : TILT)
        .end();
  }

  /** A rectangle of the parts texture, in pixels. */
  private record Uv(float u1, float v1, float u2, float v2) {}

  /** A box in pixels, which the turned model takes a quarter turn clockwise, seen from above. */
  private record Cube(float x1, float y1, float z1, float x2, float y2, float z2) {
    Cube turned(boolean turned) {
      return turned ? new Cube(FULL - z2, y1, x1, FULL - z1, y2, x2) : this;
    }
  }
}
