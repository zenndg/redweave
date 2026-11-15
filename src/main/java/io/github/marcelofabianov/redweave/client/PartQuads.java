package io.github.marcelofabianov.redweave.client;

import com.mojang.math.Transformation;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.data.ModelData;

/**
 * The quads of a part: its model, drawn on the floor, turned to its `facing` with the same turns
 * the blockstates of the plates used, and its tints moved to the side of the part, so two parts of
 * one space never share a tint (answers 534 and 536).
 */
final class PartQuads {
  /** The tints of one part, as the 4 corners of a node (answer 87). */
  static final int TINTS_PER_PART = 4;

  private static final int HALF_TURN = 180;
  private static final int QUARTER_TURN = 90;
  private static final int FULL_TURN = 360;
  private static final long SEED = 42L;

  private PartQuads() {}

  /** The quads of the model turned to the facing, with the tints of that side. */
  static List<BakedQuad> of(BakedModel model, Direction facing) {
    Transformation turn = turnOf(facing);
    List<BakedQuad> quads = new ArrayList<>();
    for (BakedQuad quad : all(model)) {
      quads.add(turned(quad, turn, facing));
    }
    return List.copyOf(quads);
  }

  /** The side of a tint, which the colors of the client read back. */
  static Direction facingOf(int tintIndex) {
    return Direction.from3DDataValue(tintIndex / TINTS_PER_PART);
  }

  static int slotOf(int tintIndex) {
    return tintIndex % TINTS_PER_PART;
  }

  private static List<BakedQuad> all(BakedModel model) {
    List<BakedQuad> quads = new ArrayList<>();
    RandomSource random = RandomSource.create(SEED);
    quads.addAll(model.getQuads(null, null, random, ModelData.EMPTY, null));
    for (Direction side : Direction.values()) {
      quads.addAll(model.getQuads(null, side, random, ModelData.EMPTY, null));
    }
    return quads;
  }

  private static BakedQuad turned(BakedQuad quad, Transformation turn, Direction facing) {
    int tint =
        quad.isTinted()
            ? facing.get3DDataValue() * TINTS_PER_PART + quad.getTintIndex()
            : quad.getTintIndex();
    BakedQuad copy =
        new BakedQuad(
            quad.getVertices().clone(),
            tint,
            Direction.rotate(turn.getMatrix(), quad.getDirection()),
            quad.getSprite(),
            quad.isShade(),
            quad.hasAmbientOcclusion());
    QuadTransformers.applying(turn.blockCenterToCorner()).processInPlace(copy);
    return copy;
  }

  /** The turns of `directionalBlock`: the model on the floor points up (answer 72). */
  private static Transformation turnOf(Direction facing) {
    int x =
        facing == Direction.DOWN ? HALF_TURN : facing.getAxis().isHorizontal() ? QUARTER_TURN : 0;
    int y = facing.getAxis().isVertical() ? 0 : ((int) facing.toYRot() + HALF_TURN) % FULL_TURN;
    return BlockModelRotation.by(x, y).getRotation();
  }
}
