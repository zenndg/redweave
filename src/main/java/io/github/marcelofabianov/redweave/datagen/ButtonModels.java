package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.datagen.PlatePieces.Cube;
import io.github.marcelofabianov.redweave.datagen.PlatePieces.Look;
import io.github.marcelofabianov.redweave.datagen.PlatePieces.Uv;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;

/**
 * The 3D pieces of the Signal Button over its plate, from `signal_button_parts.png`
 * (tmp/arte/signal_button/prompt.md): the copper ring, in four bars around the push button, the
 * push button of deepslate, which sinks 1 px when pressed, and the light of the Signal Lever, red
 * off and green on (answers 548, 554 and 595). The model lies as every module plate (answer 429).
 */
final class ButtonModels {
  private static final String LIGHT_KEY = "light";
  private static final String LIGHT_REF = "#light";
  private static final Cube RING_NORTH = new Cube(6, 2, 6, 12, 3, 7);
  private static final Cube RING_SOUTH = new Cube(6, 2, 11, 12, 3, 12);
  private static final Cube RING_WEST = new Cube(6, 2, 7, 7, 3, 11);
  private static final Cube RING_EAST = new Cube(11, 2, 7, 12, 3, 11);
  private static final Cube RELEASED = new Cube(7, 2, 7, 11, 4, 11);
  private static final Cube PRESSED = new Cube(7, 2, 7, 11, 3, 11);
  private static final Uv RING_NORTH_TOP = new Uv(0, 0, 6, 1);
  private static final Uv RING_SOUTH_TOP = new Uv(0, 5, 6, 6);
  private static final Uv RING_WEST_TOP = new Uv(0, 1, 1, 5);
  private static final Uv RING_EAST_TOP = new Uv(5, 1, 6, 5);
  private static final Uv RING_SIDE = new Uv(0, 6, 6, 7);
  private static final Uv BUTTON_TOP = new Uv(8, 0, 12, 4);
  private static final Uv RELEASED_SIDE = new Uv(8, 4, 12, 6);
  private static final Uv PRESSED_SIDE = new Uv(8, 4, 12, 5);

  private ButtonModels() {}

  /** Adds the pieces of the button, pressed or not, to its plate, turned with it or not. */
  static void addPieces(
      BlockModelBuilder model,
      ResourceLocation parts,
      ResourceLocation light,
      boolean on,
      boolean turned) {
    model.texture(LeverModels.PARTS_KEY, parts).texture(LIGHT_KEY, light);
    Look look = new Look(LeverModels.PARTS_REF, turned);
    ring(model, look);
    PlatePieces.piece(
        model, on ? PRESSED : RELEASED, BUTTON_TOP, on ? PRESSED_SIDE : RELEASED_SIDE, look);
    LeverModels.light(model, new Look(LIGHT_REF, turned), on);
  }

  /**
   * The 6 × 6 ring of four bars at columns and rows 6 to 11, from the top left of the parts
   * texture, which the window of the Signal Item Sensor has too
   * (tmp/arte/signal_item_sensor/prompt.md).
   */
  static void ring(BlockModelBuilder model, Look look) {
    PlatePieces.piece(model, RING_NORTH, RING_NORTH_TOP, RING_SIDE, look);
    PlatePieces.piece(model, RING_SOUTH, RING_SOUTH_TOP, RING_SIDE, look);
    PlatePieces.piece(model, RING_WEST, RING_WEST_TOP, RING_SIDE, look);
    PlatePieces.piece(model, RING_EAST, RING_EAST_TOP, RING_SIDE, look);
  }
}
