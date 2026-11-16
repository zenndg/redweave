package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.datagen.PlatePieces.Cube;
import io.github.marcelofabianov.redweave.datagen.PlatePieces.Look;
import io.github.marcelofabianov.redweave.datagen.PlatePieces.Uv;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;

/**
 * The 3D pieces of the Signal Alarm over its plate, from `signal_alarm_parts.png`
 * (tmp/arte/signal_alarm/prompt.md): a copper base, a speaker with a grille, and a warning light
 * standing beside them. Ringing, the light takes `signal_alarm_lamp.png`, an animated texture that
 * pulses red and dark on the client with no packet, and glows; quiet, it is dark (answer 649). The
 * model lies as every module plate (answer 429).
 */
final class AlarmModels {
  private static final String LAMP_KEY = "lamp";
  private static final String LAMP_REF = "#lamp";
  private static final String PARTS = "_parts";
  private static final String LAMP = "_lamp";
  private static final Cube BASE = new Cube(5, 2, 5, 11, 3, 11);
  private static final Cube SPEAKER = new Cube(6, 3, 6, 10, 4, 10);
  private static final Cube LIGHT = new Cube(4, 2, 10, 6, 4, 12);
  private static final Uv BASE_TOP = new Uv(0, 0, 6, 6);
  private static final Uv BASE_SIDE = new Uv(0, 6, 6, 7);
  private static final Uv SPEAKER_TOP = new Uv(7, 0, 11, 4);
  private static final Uv SPEAKER_SIDE = new Uv(7, 4, 11, 5);
  private static final Uv LIT_TOP = new Uv(12, 0, 14, 2);
  private static final Uv LIT_SIDE = new Uv(12, 2, 14, 4);
  private static final Uv DARK_TOP = new Uv(12, 5, 14, 7);
  private static final Uv DARK_SIDE = new Uv(12, 7, 14, 9);

  private AlarmModels() {}

  /** Adds the pieces of the alarm, ringing or not, to its plate, turned with it or not. */
  static void addPieces(
      BlockModelBuilder model,
      ResourceLocation parts,
      ResourceLocation light,
      boolean ringing,
      boolean turned) {
    ResourceLocation lamp =
        ResourceLocation.fromNamespaceAndPath(
            parts.getNamespace(), parts.getPath().replace(PARTS, LAMP));
    model.texture(LeverModels.PARTS_KEY, parts).texture(LAMP_KEY, lamp);
    Look look = new Look(LeverModels.PARTS_REF, turned);
    PlatePieces.piece(model, BASE, BASE_TOP, BASE_SIDE, look);
    PlatePieces.piece(model, SPEAKER, SPEAKER_TOP, SPEAKER_SIDE, look);
    if (ringing) {
      PlatePieces.piece(model, LIGHT, LIT_TOP, LIT_SIDE, new Look(LAMP_REF, turned))
          .emissivity(LeverModels.GLOW, LeverModels.GLOW);
    } else {
      PlatePieces.piece(model, LIGHT, DARK_TOP, DARK_SIDE, look);
    }
  }
}
