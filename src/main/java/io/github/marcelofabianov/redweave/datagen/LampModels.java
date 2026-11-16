package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.part.LampPart;
import io.github.marcelofabianov.redweave.datagen.PlatePieces.Cube;
import io.github.marcelofabianov.redweave.datagen.PlatePieces.Uv;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/**
 * The models of the Signal Lamp, from `signal_lamp_body.png` and `signal_lamp_glow.png`
 * (tmp/arte/signal_lamp/prompt.md): a dark rail of 12 × 4 px with copper caps, long along its
 * front, and a diffuser of 10 × 2 px over it, gray, which the client tints in the color of its
 * channel and which glows; switched off by its control, a red light of the lever at one end
 * (answers 650, 652 and 657). Each look has a version turned a quarter, as every module.
 */
final class LampModels {
  private static final String BLOCK_PARENT = "block/block";
  private static final String ITEM_GENERATED = "item/generated";
  private static final String BLOCK_FOLDER = "block/";
  private static final String ITEM_FOLDER = "item/";
  private static final String TURNED = "_turned";
  private static final String BODY_KEY = "body";
  private static final String GLOW_KEY = "glow";
  private static final String LIGHT_KEY = "light";
  private static final String PARTICLE_KEY = "particle";
  private static final String BODY_REF = "#body";
  private static final String GLOW_REF = "#glow";
  private static final String LIGHT_REF = "#light";
  private static final String BODY = "signal_lamp_body";
  private static final String GLOW = "signal_lamp_glow";
  private static final String ITEM_GLOW = "signal_lamp_glow";
  private static final String LEVER_PARTS = "signal_lever_parts";
  private static final String LAYER_0 = "layer0";
  private static final String LAYER_1 = "layer1";
  private static final int CHANNEL_TINT = 0;
  private static final int SHINE = 15;
  private static final Cube RAIL = new Cube(6, 0, 2, 10, 1, 14);
  private static final Cube DIFFUSER = new Cube(7, 1, 3, 9, 2, 13);
  private static final Cube INDICATOR = new Cube(7.5F, 1, 13, 8.5F, 2, 14);
  private static final Uv RAIL_TOP = new Uv(2, 6, 14, 10);
  private static final Uv RAIL_SIDE = new Uv(2, 11, 14, 12);
  private static final Uv RAIL_END = new Uv(0, 6, 2, 7);
  private static final Uv DIFFUSER_TOP = new Uv(3, 7, 13, 9);
  private static final Uv DIFFUSER_SIDE = new Uv(3, 10, 13, 11);
  private static final Uv DIFFUSER_END = new Uv(3, 10, 5, 11);
  private static final Uv RED_TOP = new Uv(12, 0, 13, 1);
  private static final Uv RED_SIDE = new Uv(12, 2, 13, 3);

  private final BlockModelProvider models;

  private LampModels(BlockModelProvider models) {
    this.models = models;
  }

  /** Every look of the lamp, turned or not, and its item in two layers. */
  static void register(BlockModelProvider models, ItemModelProvider items) {
    LampModels lamp = new LampModels(models);
    for (String look : LampPart.LOOKS) {
      lamp.model(LampPart.ID + look, !look.isEmpty(), false);
      lamp.model(LampPart.ID + look + TURNED, !look.isEmpty(), true);
    }
    items
        .getBuilder(LampPart.ID)
        .parent(new ModelFile.UncheckedModelFile(ResourceLocation.parse(ITEM_GENERATED)))
        .texture(LAYER_0, modLoc(ITEM_FOLDER + LampPart.ID))
        .texture(LAYER_1, modLoc(ITEM_FOLDER + ITEM_GLOW));
  }

  private void model(String name, boolean switchedOff, boolean turned) {
    BlockModelBuilder model =
        models
            .getBuilder(name)
            .parent(models.getExistingFile(ResourceLocation.parse(BLOCK_PARENT)))
            .texture(BODY_KEY, modLoc(BLOCK_FOLDER + BODY))
            .texture(GLOW_KEY, modLoc(BLOCK_FOLDER + GLOW))
            .texture(LIGHT_KEY, modLoc(BLOCK_FOLDER + LEVER_PARTS))
            .texture(PARTICLE_KEY, modLoc(BLOCK_FOLDER + BODY));
    bar(model, RAIL.turned(turned), new Faces(RAIL_TOP, RAIL_SIDE, RAIL_END), BODY_REF, turned);
    bar(
            model,
            DIFFUSER.turned(turned),
            new Faces(DIFFUSER_TOP, DIFFUSER_SIDE, DIFFUSER_END),
            GLOW_REF,
            turned)
        .emissivity(SHINE, SHINE)
        .faces((side, face) -> face.tintindex(CHANNEL_TINT));
    if (switchedOff) {
      bar(
              model,
              INDICATOR.turned(turned),
              new Faces(RED_TOP, RED_SIDE, RED_SIDE),
              LIGHT_REF,
              turned)
          .emissivity(SHINE, SHINE);
    }
  }

  /**
   * A box long along the front: its top and bottom, its long sides and its ends. The texture runs
   * along the length, so the top turns a quarter when the bar lies along `z`.
   */
  private static BlockModelBuilder.ElementBuilder bar(
      BlockModelBuilder model, Cube cube, Faces faces, String ref, boolean turned) {
    BlockModelBuilder.ElementBuilder element =
        model.element().from(cube.x1(), cube.y1(), cube.z1()).to(cube.x2(), cube.y2(), cube.z2());
    for (Direction direction : Direction.values()) {
      Uv uv = faces.of(direction, turned);
      BlockModelBuilder.ElementBuilder.FaceBuilder face =
          element.face(direction).uvs(uv.u1(), uv.v1(), uv.u2(), uv.v2()).texture(ref);
      if (direction.getAxis() == Direction.Axis.Y && !turned) {
        face.rotation(ModelBuilder.FaceRotation.CLOCKWISE_90);
      }
    }
    return element;
  }

  private static ResourceLocation modLoc(String path) {
    return ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, path);
  }

  /** The parts of a texture for the top, the long sides and the ends of a bar. */
  private record Faces(Uv top, Uv side, Uv end) {
    /**
     * The long sides face across the length: east and west along `z`, north and south along `x`.
     */
    Uv of(Direction direction, boolean turned) {
      if (direction.getAxis() == Direction.Axis.Y) {
        return top;
      }
      Direction.Axis across = turned ? Direction.Axis.Z : Direction.Axis.X;
      return direction.getAxis() == across ? side : end;
    }
  }
}
