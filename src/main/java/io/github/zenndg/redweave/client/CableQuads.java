package io.github.zenndg.redweave.client;

import com.mojang.math.Transformation;
import io.github.zenndg.redweave.block.part.CableLook;
import io.github.zenndg.redweave.block.part.CableTurn;
import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;

/**
 * The quads of a piece of a Signal Cable: its model, on the floor and pointing north, turned to its
 * side and direction with the turn the shape uses, and its tints moved to its side, as with the
 * plates: the first is the insulation, and the second, the stripe (answers 518, 522 and 572).
 */
final class CableQuads {
  /** The tint of the insulation, in the color of the cable (answer 518). */
  static final int INSULATION = 0;

  /** The tint of the stripe, from dark to bright red by the strength (answer 522). */
  static final int STRIPE = 1;

  private CableQuads() {}

  static List<BakedQuad> of(BakedModel model, CableLook look) {
    Transformation turn = new Transformation(CableTurn.of(look.face(), look.toward()));
    return PartQuads.of(model, turn, look.face());
  }
}
