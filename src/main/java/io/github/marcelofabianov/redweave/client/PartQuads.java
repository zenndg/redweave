package io.github.marcelofabianov.redweave.client;

import com.mojang.math.Transformation;
import io.github.marcelofabianov.redweave.block.part.PartLook;
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
 * The quads of a part: its model, drawn on the floor, turned to its place with the turns the
 * blockstates of the plates used, and its tints moved to the side of the part, so two parts of one
 * space never share a tint (answers 534 and 536).
 */
final class PartQuads {
  /** The tints of one part, as the 4 corners of a node (answer 87). */
  static final int TINTS_PER_PART = 4;

  private static final long SEED = 42L;

  private PartQuads() {}

  /** The quads of the model turned as the look says, with the tints of its side. */
  static List<BakedQuad> of(BakedModel model, PartLook look) {
    Transformation turn = BlockModelRotation.by(look.turn().x(), look.turn().y()).getRotation();
    return of(model, turn, look.face());
  }

  /** The quads of the model with the turn, about the center of the block, and the side's tints. */
  static List<BakedQuad> of(BakedModel model, Transformation turn, Direction face) {
    List<BakedQuad> quads = new ArrayList<>();
    for (BakedQuad quad : all(model)) {
      quads.add(turned(quad, turn, face));
    }
    return List.copyOf(quads);
  }

  /** The side of the part of a tint, which the colors of the client read back. */
  static Direction faceOf(int tintIndex) {
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

  private static BakedQuad turned(BakedQuad quad, Transformation turn, Direction face) {
    int tint =
        quad.isTinted()
            ? face.get3DDataValue() * TINTS_PER_PART + quad.getTintIndex()
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
}
