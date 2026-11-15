package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.datagen.PlatePieces.Cube;
import io.github.marcelofabianov.redweave.datagen.PlatePieces.Look;
import io.github.marcelofabianov.redweave.datagen.PlatePieces.Uv;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;

/**
 * The 3D pieces of the Signal Pressure Plate over its plate, from `signal_pressure_plate_parts.png`
 * (tmp/arte/signal_pressure_plate/prompt.md): the tread of deepslate with its copper border, 1 px
 * over the plate when off and sunk almost flush with it when on, and the light of the Signal Lever,
 * red off and green on (answers 548, 554 and 609). The model lies as every module plate (429).
 */
final class PressurePlateModels {
  private static final String LIGHT_KEY = "light";
  private static final String LIGHT_REF = "#light";
  private static final float SUNK_TOP = 2.25F;
  private static final float SUNK_SIDE = 6.25F;
  private static final Cube RAISED = new Cube(6, 2, 6, 12, 3, 12);
  private static final Cube SUNK = new Cube(6, 2, 6, 12, SUNK_TOP, 12);
  private static final Uv TREAD_TOP = new Uv(0, 0, 6, 6);
  private static final Uv RAISED_SIDE = new Uv(0, 6, 6, 7);
  private static final Uv SUNK_SIDE_UV = new Uv(0, 6, 6, SUNK_SIDE);

  private PressurePlateModels() {}

  /** Adds the pieces of the plate, on or off, to its plate, turned with it or not. */
  static void addPieces(
      BlockModelBuilder model,
      ResourceLocation parts,
      ResourceLocation light,
      boolean on,
      boolean turned) {
    model.texture(LeverModels.PARTS_KEY, parts).texture(LIGHT_KEY, light);
    Look look = new Look(LeverModels.PARTS_REF, turned);
    PlatePieces.piece(model, on ? SUNK : RAISED, TREAD_TOP, on ? SUNK_SIDE_UV : RAISED_SIDE, look);
    LeverModels.light(model, new Look(LIGHT_REF, turned), on);
  }
}
