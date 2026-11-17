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
 * The models of the Signal Lamp (tmp/arte/signal_lamp/prompt.md): a copper base of 6 × 12 px, a
 * band of 1 px on it that the client tints in the color of the channel, a frosted glass dome of 4 ×
 * 10 px and 3 px high, and a white LED core of 2 × 8 × 2 px inside it, which glows; switched off by
 * its control, a red light of the lever at one end of the base (answers 650, 657, 800, 820 to 827
 * and 829). The glass goes to the translucent layer by its texture. Each look has a version turned
 * a quarter, as every module.
 */
final class LampModels {
  private static final String BLOCK_PARENT = "block/block";
  private static final String ITEM_GENERATED = "item/generated";
  private static final String BLOCK_FOLDER = "block/";
  private static final String ITEM_FOLDER = "item/";
  private static final String TURNED = "_turned";
  private static final String BODY_KEY = "body";
  private static final String GLOW_KEY = "glow";
  private static final String BAND_KEY = "band";
  private static final String GLASS_KEY = "glass";
  private static final String LIGHT_KEY = "light";
  private static final String PARTICLE_KEY = "particle";
  private static final String BODY_REF = "#body";
  private static final String GLOW_REF = "#glow";
  private static final String BAND_REF = "#band";
  private static final String GLASS_REF = "#glass";
  private static final String LIGHT_REF = "#light";
  private static final String BODY = "signal_lamp_body";
  private static final String GLOW = "signal_lamp_glow";
  private static final String BAND = "signal_lamp_band";

  /** The texture of the glass, which the model of the space draws translucent (answer 825). */
  static final String GLASS = "signal_lamp_glass";

  private static final String ITEM_GLOW = "signal_lamp_glow";
  private static final String LEVER_PARTS = "signal_lever_parts";
  private static final String LAYER_0 = "layer0";
  private static final String LAYER_1 = "layer1";

  /** The slots of the tint: the core, white, and the band, in the color of the channel. */
  private static final int CORE_TINT = 0;

  private static final int BAND_TINT = 1;
  private static final int SHINE = 15;
  private static final Cube BASE = new Cube(5, 0, 2, 11, 1, 14);
  private static final Cube BAND_STRIP = new Cube(5, 1, 2, 11, 2, 14);
  private static final Cube DOME = new Cube(6, 2, 3, 10, 5, 13);
  private static final Cube CORE = new Cube(7, 2, 4, 9, 4, 12);
  private static final Cube INDICATOR = new Cube(7.5F, 2, 13, 8.5F, 3, 14);
  private static final Uv BASE_TOP = new Uv(2, 5, 14, 11);
  private static final Uv BASE_SIDE = new Uv(2, 15, 14, 16);
  private static final Uv BASE_END = new Uv(5, 15, 11, 16);
  private static final Uv BAND_TOP = new Uv(2, 5, 14, 11);
  private static final Uv BAND_SIDE = new Uv(2, 7, 14, 8);
  private static final Uv BAND_END = new Uv(5, 7, 11, 8);
  private static final Uv DOME_TOP = new Uv(3, 6, 13, 10);
  private static final Uv DOME_SIDE = new Uv(3, 6, 13, 9);
  private static final Uv DOME_END = new Uv(6, 6, 10, 9);
  private static final Uv CORE_TOP = new Uv(4, 7, 12, 9);
  private static final Uv CORE_SIDE = new Uv(4, 7, 12, 9);
  private static final Uv CORE_END = new Uv(7, 7, 9, 9);
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
            .texture(BAND_KEY, modLoc(BLOCK_FOLDER + BAND))
            .texture(GLASS_KEY, modLoc(BLOCK_FOLDER + GLASS))
            .texture(LIGHT_KEY, modLoc(BLOCK_FOLDER + LEVER_PARTS))
            .texture(PARTICLE_KEY, modLoc(BLOCK_FOLDER + BODY));
    pieces(model, turned);
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

  /** The base, its band, the core and the glass dome over them (answers 820 to 826). */
  private static void pieces(BlockModelBuilder model, boolean turned) {
    bar(model, BASE.turned(turned), new Faces(BASE_TOP, BASE_SIDE, BASE_END), BODY_REF, turned);
    bar(
            model,
            BAND_STRIP.turned(turned),
            new Faces(BAND_TOP, BAND_SIDE, BAND_END),
            BAND_REF,
            turned)
        .faces((side, face) -> face.tintindex(BAND_TINT));
    bar(model, CORE.turned(turned), new Faces(CORE_TOP, CORE_SIDE, CORE_END), GLOW_REF, turned)
        .emissivity(SHINE, SHINE)
        .faces((side, face) -> face.tintindex(CORE_TINT));
    bar(model, DOME.turned(turned), new Faces(DOME_TOP, DOME_SIDE, DOME_END), GLASS_REF, turned);
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
