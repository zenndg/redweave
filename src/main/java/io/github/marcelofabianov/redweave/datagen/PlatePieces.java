package io.github.marcelofabianov.redweave.datagen;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;

/**
 * The 3D pieces of a source module over its plate, as the handle of the Signal Lever and the push
 * button of the Signal Button: boxes in pixels with a top and four sides from a texture of parts.
 * The model lies with its front to the north, or to the east when its top is turned, as every
 * module plate (answer 429).
 */
final class PlatePieces {
  private static final float FULL = 16;
  private static final Direction[] SIDES = {
    Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST
  };

  private PlatePieces() {}

  /** A box with its top, if any, and its four sides from the texture named by `ref`. */
  static BlockModelBuilder.ElementBuilder piece(
      BlockModelBuilder model, Cube cube, Uv top, Uv side, Look look) {
    Cube placed = cube.turned(look.turned());
    BlockModelBuilder.ElementBuilder element =
        model
            .element()
            .from(placed.x1(), placed.y1(), placed.z1())
            .to(placed.x2(), placed.y2(), placed.z2());
    if (top != null) {
      BlockModelBuilder.ElementBuilder.FaceBuilder face =
          element
              .face(Direction.UP)
              .uvs(top.u1(), top.v1(), top.u2(), top.v2())
              .texture(look.ref());
      if (look.turned()) {
        face.rotation(ModelBuilder.FaceRotation.CLOCKWISE_90);
      }
    }
    for (Direction direction : SIDES) {
      element.face(direction).uvs(side.u1(), side.v1(), side.u2(), side.v2()).texture(look.ref());
    }
    return element;
  }

  /** The texture a piece takes, as `#parts`, and whether the top of the plate is turned. */
  record Look(String ref, boolean turned) {}

  /** A rectangle of a texture of parts, in pixels. */
  record Uv(float u1, float v1, float u2, float v2) {}

  /** A box in pixels, which the turned model takes a quarter turn clockwise, seen from above. */
  record Cube(float x1, float y1, float z1, float x2, float y2, float z2) {
    Cube turned(boolean turned) {
      return turned ? new Cube(FULL - z2, y1, x1, FULL - z1, y2, x2) : this;
    }
  }
}
