package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.datagen.PlatePieces.Look;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;

/**
 * The 3D pieces of the Signal Item Sensor over its plate, from `signal_item_sensor_parts.png`
 * (tmp/arte/signal_item_sensor/prompt.md): the steel ring of its window, laid out as the ring of
 * the Signal Button, and the light of the Signal Lever, red off and green on (answer 697). The item
 * the sensor expects is drawn in the window by the client, not by the model. The model lies as
 * every module plate (answer 429).
 */
final class ItemSensorModels {
  private static final String LIGHT_KEY = "light";
  private static final String LIGHT_REF = "#light";

  private ItemSensorModels() {}

  /** Adds the pieces of the sensor, on or off, to its plate, turned with it or not. */
  static void addPieces(
      BlockModelBuilder model,
      ResourceLocation parts,
      ResourceLocation light,
      boolean on,
      boolean turned) {
    model.texture(LeverModels.PARTS_KEY, parts).texture(LIGHT_KEY, light);
    ButtonModels.ring(model, new Look(LeverModels.PARTS_REF, turned));
    LeverModels.light(model, new Look(LIGHT_REF, turned), on);
  }
}
