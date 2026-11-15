package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.datagen.PlatePieces.Cube;
import io.github.marcelofabianov.redweave.datagen.PlatePieces.Look;
import io.github.marcelofabianov.redweave.datagen.PlatePieces.Uv;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;

/**
 * The 3D pieces of the Signal Lever over its plate, from `signal_lever_parts.png`
 * (tmp/arte/signal_lever/prompt.md): the copper base, the steel handle with its knob, tilted to the
 * front when on and to the back when off, and the light, red off and green on, which glows (answers
 * 548, 554 and 581). The model lies with its front to the north, or to the east when its top is
 * turned, as every module plate (answer 429).
 */
final class LeverModels {
  static final String PARTS_KEY = "parts";
  static final String PARTS_REF = "#parts";
  private static final float CENTER = 8;
  private static final float HANDLE_FOOT = 4;
  private static final float TILT = 22.5F;
  static final int GLOW = 15;
  private static final Cube BASE = new Cube(6, 2, 6, 10, 4, 10);
  private static final Cube HANDLE = new Cube(7, 4, 7, 9, 11, 9);
  private static final Cube KNOB = new Cube(7, 11, 7, 9, 13, 9);
  static final Cube LIGHT = new Cube(4, 2, 10, 6, 3, 12);
  private static final Uv BASE_TOP = new Uv(0, 0, 4, 4);
  private static final Uv BASE_SIDE = new Uv(0, 4, 4, 6);
  private static final Uv HANDLE_SIDE = new Uv(6, 0, 8, 7);
  private static final Uv KNOB_SIDE = new Uv(6, 7, 8, 9);
  private static final Uv KNOB_TOP = new Uv(9, 0, 11, 2);
  static final Uv RED_TOP = new Uv(12, 0, 14, 2);
  static final Uv RED_SIDE = new Uv(12, 2, 14, 3);
  static final Uv GREEN_TOP = new Uv(12, 4, 14, 6);
  static final Uv GREEN_SIDE = new Uv(12, 6, 14, 7);

  private LeverModels() {}

  /** Adds the pieces of the lever, on or off, to its plate, turned with it or not. */
  static void addPieces(
      BlockModelBuilder model, ResourceLocation parts, boolean on, boolean turned) {
    model.texture(PARTS_KEY, parts);
    Look look = new Look(PARTS_REF, turned);
    PlatePieces.piece(model, BASE, BASE_TOP, BASE_SIDE, look);
    tilted(PlatePieces.piece(model, HANDLE, null, HANDLE_SIDE, look), on, turned);
    tilted(PlatePieces.piece(model, KNOB, KNOB_TOP, KNOB_SIDE, look), on, turned);
    light(model, look, on);
  }

  /** The light of a source module, red off and green on, which glows (answers 554 and 581). */
  static void light(BlockModelBuilder model, Look look, boolean on) {
    PlatePieces.piece(model, LIGHT, on ? GREEN_TOP : RED_TOP, on ? GREEN_SIDE : RED_SIDE, look)
        .emissivity(GLOW, GLOW);
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
}
