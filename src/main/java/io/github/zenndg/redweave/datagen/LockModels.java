package io.github.zenndg.redweave.datagen;

import io.github.zenndg.redweave.datagen.PlatePieces.Cube;
import io.github.zenndg.redweave.datagen.PlatePieces.Look;
import io.github.zenndg.redweave.datagen.PlatePieces.Uv;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;

/**
 * The 3D pieces of the Signal Lock over its plate, from `signal_lock_parts.png`
 * (tmp/arte/signal_lock/prompt.md): the copper body of a small padlock, standing at columns 6 to 9
 * and rows 7 and 8, and its steel shackle, closed over the body while locked, and raised 1 px and
 * turned a quarter on one leg while unlocked, with the light of the Signal Lever, red locked and
 * green unlocked (answer 638). The model lies as every module plate (answer 429).
 */
final class LockModels {
  private static final String LIGHT_KEY = "light";
  private static final String LIGHT_REF = "#light";
  private static final Cube BODY = new Cube(6, 2, 7, 10, 5, 9);
  private static final Cube CLOSED_BAR = new Cube(6, 6, 7.5F, 10, 7, 8.5F);
  private static final Cube CLOSED_LEFT = new Cube(6, 5, 7.5F, 7, 6, 8.5F);
  private static final Cube CLOSED_RIGHT = new Cube(9, 5, 7.5F, 10, 6, 8.5F);
  private static final Cube OPEN_BAR = new Cube(6, 7, 7.5F, 7, 8, 11.5F);
  private static final Cube OPEN_NEAR = new Cube(6, 6, 7.5F, 7, 7, 8.5F);
  private static final Cube OPEN_FAR = new Cube(6, 6, 10.5F, 7, 7, 11.5F);
  private static final Uv BODY_FRONT = new Uv(0, 0, 4, 3);
  private static final Uv BODY_TOP = new Uv(0, 4, 4, 5);
  private static final Uv BAR = new Uv(6, 0, 10, 1);
  private static final Uv LEG = new Uv(6, 1, 7, 3);

  private LockModels() {}

  /** Adds the pieces of the lock, unlocked or not, to its plate, turned with it or not. */
  static void addPieces(
      BlockModelBuilder model,
      ResourceLocation parts,
      ResourceLocation light,
      boolean on,
      boolean turned) {
    model.texture(LeverModels.PARTS_KEY, parts).texture(LIGHT_KEY, light);
    Look look = new Look(LeverModels.PARTS_REF, turned);
    PlatePieces.piece(model, BODY, BODY_TOP, BODY_FRONT, look);
    if (on) {
      shackle(model, look, OPEN_BAR, OPEN_NEAR, OPEN_FAR);
    } else {
      shackle(model, look, CLOSED_BAR, CLOSED_LEFT, CLOSED_RIGHT);
    }
    LeverModels.light(model, new Look(LIGHT_REF, turned), on);
  }

  private static void shackle(BlockModelBuilder model, Look look, Cube bar, Cube leg, Cube other) {
    PlatePieces.piece(model, bar, BAR, BAR, look);
    PlatePieces.piece(model, leg, LEG, LEG, look);
    PlatePieces.piece(model, other, LEG, LEG, look);
  }
}
