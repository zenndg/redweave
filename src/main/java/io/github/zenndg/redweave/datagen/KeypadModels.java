package io.github.zenndg.redweave.datagen;

import io.github.zenndg.redweave.datagen.PlatePieces.Cube;
import io.github.zenndg.redweave.datagen.PlatePieces.Look;
import io.github.zenndg.redweave.datagen.PlatePieces.Uv;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;

/**
 * The 3D pieces of the Signal Keypad over its plate, from `signal_keypad_parts.png`
 * (tmp/arte/signal_keypad/prompt.md): a low keypad of 6 × 6 px and 1 px high at columns 5 to 10 and
 * rows 6 to 11, the light of the Signal Lever of 2 × 1 px on its back row, red locked and green
 * unlocked, and the dot of the input channel, which the client tints (answers 667, 909 and 912).
 * The model lies as every module plate (answer 429).
 */
final class KeypadModels {
  private static final String LIGHT_KEY = "light";
  private static final String LIGHT_REF = "#light";
  private static final float KEYPAD_TOP = 3;
  private static final float LIGHT_TOP = 3.5F;
  private static final float DOT_TOP = 3.01F;
  private static final Cube KEYPAD = new Cube(5, 2, 6, 11, KEYPAD_TOP, 12);
  private static final Cube LIGHT = new Cube(5, KEYPAD_TOP, 11, 7, LIGHT_TOP, 12);
  private static final Cube DOT = new Cube(9, KEYPAD_TOP, 11, 10, DOT_TOP, 12);
  private static final Uv KEYPAD_FACE = new Uv(0, 0, 6, 6);
  private static final Uv KEYPAD_SIDE = new Uv(0, 7, 6, 8);
  private static final Uv DOT_FACE = new Uv(4, 5, 5, 6);
  private static final Uv RED_TOP = new Uv(12, 0, 14, 1);
  private static final Uv GREEN_TOP = new Uv(12, 4, 14, 5);
  private static final Uv RED_SIDE = new Uv(12, 2, 14, 3);
  private static final Uv GREEN_SIDE = new Uv(12, 6, 14, 7);

  /** The slot of the tint of the dot, the color of the input channel. */
  static final int DOT_TINT = 0;

  private KeypadModels() {}

  /** Adds the pieces of the keypad, unlocked or not, to its plate, turned with it or not. */
  static void addPieces(
      BlockModelBuilder model,
      ResourceLocation parts,
      ResourceLocation light,
      boolean on,
      boolean turned) {
    model.texture(LeverModels.PARTS_KEY, parts).texture(LIGHT_KEY, light);
    Look look = new Look(LeverModels.PARTS_REF, turned);
    PlatePieces.piece(model, KEYPAD, KEYPAD_FACE, KEYPAD_SIDE, look);
    PlatePieces.piece(model, DOT, DOT_FACE, DOT_FACE, look)
        .faces((side, face) -> face.tintindex(DOT_TINT));
    PlatePieces.piece(
            model,
            LIGHT,
            on ? GREEN_TOP : RED_TOP,
            on ? GREEN_SIDE : RED_SIDE,
            new Look(LIGHT_REF, turned))
        .emissivity(LeverModels.GLOW, LeverModels.GLOW);
  }
}
