package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.block.part.CablePart;
import io.github.marcelofabianov.redweave.block.part.CableReach;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelBuilder.FaceRotation;

/**
 * The models of the pieces of the Signal Cable, on the floor and pointing north, which the client
 * turns to each side and direction (answers 521, 571 and 572): the center, the arm to the edge, the
 * short arm of the inner curve, the arm past the edge to the corner of the outer curve, the arm
 * that climbs the side of the block of a step and the arm past the edge to the plate of a node or a
 * module beside (answer 575). The insulation takes tint 0, the color of the cable, and the stripe,
 * a little above it, tint 1, its strength (answers 518, 522 and 535). The textures keep the top on
 * columns 6 to 9, the sides on 2 to 4 and the end on 11 to 14 (518).
 */
final class CableModels {
  private static final String BLOCK_PARENT = "block/block";
  private static final String CABLE_TEXTURE = "block/signal_cable";
  private static final String STRIPE_TEXTURE = "block/signal_cable_stripe";
  private static final String CABLE_KEY = "cable";
  private static final String STRIPE_KEY = "stripe";
  private static final String PARTICLE_KEY = "particle";
  private static final String CABLE_REF = "#cable";
  private static final String STRIPE_REF = "#stripe";
  private static final int INSULATION = 0;
  private static final int STRIPE = 1;

  private static final float[] CENTER = {6, 0, 6, 10, 3, 10};
  private static final float[] CENTER_TOP = {6, 6, 10, 10};
  private static final float[] CENTER_SIDE = {2, 6, 5, 10};
  private static final float[] CENTER_STRIPE = {7, 3.1F, 7, 9, 3.1F, 9};
  private static final float[] CENTER_STRIPE_UV = {7, 7, 9, 9};

  private static final float[] ARM = {6, 0, 0, 10, 3, 6};
  private static final float[] ARM_TOP = {6, 0, 10, 6};
  private static final float[] ARM_SIDE = {2, 0, 5, 6};
  private static final float[] END = {11, 0, 15, 3};
  private static final float[] ARM_STRIPE = {7, 3.1F, 0, 9, 3.1F, 7};
  private static final float[] ARM_STRIPE_UV = {7, 0, 9, 7};

  private static final float[] SHORT = {6, 0, 3, 10, 3, 6};
  private static final float[] SHORT_TOP = {6, 3, 10, 6};
  private static final float[] SHORT_SIDE = {2, 3, 5, 6};
  private static final float[] SHORT_STRIPE = {7, 3.1F, 3, 9, 3.1F, 7};
  private static final float[] SHORT_STRIPE_UV = {7, 3, 9, 7};

  private static final float[] CORNER = {6, 0, -3, 10, 3, 6};
  private static final float[] CORNER_TOP = {6, 0, 10, 9};
  private static final float[] CORNER_SIDE = {2, 0, 5, 9};
  private static final float[] CORNER_FRONT = {6, 0, 10, 3};
  private static final float[] CORNER_STRIPE = {7, 3.1F, -3.1F, 9, 3.1F, 7};
  private static final float[] CORNER_STRIPE_UV = {7, 0, 9, 10};
  private static final float[] CORNER_DOWN_STRIPE = {7, 0, -3.1F, 9, 3.1F, -3.1F};
  private static final float[] CORNER_DOWN_STRIPE_UV = {7, 0, 9, 3};

  private static final float[] PLATE = {6, 0, -3, 10, 3, 6};
  private static final float[] PLATE_STRIPE = {7, 3.1F, -3, 9, 3.1F, 7};

  private static final float[] RISER = {6, 0, 0, 10, 19, 3};
  private static final float[] RISER_FRONT = {6, 0, 10, 16};
  private static final float[] RISER_TOP = {6, 0, 10, 3};
  private static final float[] RISER_EAST = {2, 0, 5, 16};
  private static final float[] RISER_WEST = {5, 0, 2, 16};
  private static final float[] RISER_STRIPE = {7, 3.1F, 3.1F, 9, 19.1F, 3.1F};
  private static final float[] RISER_STRIPE_UV = {7, 0, 9, 16};
  private static final float[] RISER_TOP_STRIPE = {7, 19.1F, 0, 9, 19.1F, 3.1F};
  private static final float[] RISER_TOP_STRIPE_UV = {7, 0, 9, 3};
  private static final float[] STEP_STRIPE_UV = {7, 0, 9, 4};
  private static final float[] STEP_STRIPE = {7, 3.1F, 3.1F, 9, 3.1F, 7};

  private static final int X1 = 0;
  private static final int Y1 = 1;
  private static final int Z1 = 2;
  private static final int X2 = 3;
  private static final int Y2 = 4;
  private static final int Z2 = 5;
  private static final int U1 = 0;
  private static final int V1 = 1;
  private static final int U2 = 2;
  private static final int V2 = 3;

  private final BlockModelProvider models;

  private CableModels(BlockModelProvider models) {
    this.models = models;
  }

  /** One face of an element: its side, its UV, its turn on the UV and its tint. */
  private record Face(Direction side, float[] uv, FaceRotation rotation, int tint) {
    static Face cable(Direction side, float[] uv, FaceRotation rotation) {
      return new Face(side, uv, rotation, INSULATION);
    }

    static Face stripe(Direction side, float[] uv) {
      return new Face(side, uv, FaceRotation.ZERO, STRIPE);
    }
  }

  static void register(BlockModelProvider models) {
    CableModels cable = new CableModels(models);
    cable.center();
    cable.arm();
    cable.shortArm();
    cable.corner();
    cable.step();
    cable.toPlate();
  }

  private void center() {
    BlockModelBuilder model = start(CablePart.CENTER);
    element(
        model, CENTER, sides(CENTER_TOP, CENTER_SIDE, List.of(Direction.NORTH, Direction.SOUTH)));
    element(model, CENTER_STRIPE, List.of(Face.stripe(Direction.UP, CENTER_STRIPE_UV)));
  }

  private void arm() {
    BlockModelBuilder model = start(CableReach.EDGE.model());
    element(model, ARM, withEnd(sides(ARM_TOP, ARM_SIDE, List.of())));
    element(model, ARM_STRIPE, List.of(Face.stripe(Direction.UP, ARM_STRIPE_UV)));
  }

  private void shortArm() {
    BlockModelBuilder model = start(CableReach.SHORT.model());
    element(model, SHORT, withEnd(sides(SHORT_TOP, SHORT_SIDE, List.of())));
    element(model, SHORT_STRIPE, List.of(Face.stripe(Direction.UP, SHORT_STRIPE_UV)));
  }

  /** Past the edge, to the corner, whose front goes on as the top of the cable below (572). */
  private void corner() {
    BlockModelBuilder model = start(CableReach.CORNER.model());
    List<Face> faces =
        List.of(
            Face.cable(Direction.UP, CORNER_TOP, FaceRotation.ZERO),
            Face.cable(Direction.EAST, CORNER_SIDE, FaceRotation.CLOCKWISE_90),
            Face.cable(Direction.WEST, CORNER_SIDE, FaceRotation.CLOCKWISE_90),
            Face.cable(Direction.NORTH, CORNER_FRONT, FaceRotation.ZERO));
    element(model, CORNER, faces);
    element(model, CORNER_STRIPE, List.of(Face.stripe(Direction.UP, CORNER_STRIPE_UV)));
    element(
        model, CORNER_DOWN_STRIPE, List.of(Face.stripe(Direction.NORTH, CORNER_DOWN_STRIPE_UV)));
  }

  /** Past the edge, to the plate of a node or a module of the space beside, 3 px away (575). */
  private void toPlate() {
    BlockModelBuilder model = start(CableReach.PLATE.model());
    element(model, PLATE, withEnd(sides(CORNER_TOP, CORNER_SIDE, List.of())));
    element(model, PLATE_STRIPE, List.of(Face.stripe(Direction.UP, CORNER_STRIPE_UV)));
  }

  /** A short arm and a riser up the side of the block, past its top, as the game wire (572). */
  private void step() {
    BlockModelBuilder model = start(CableReach.STEP.model());
    element(model, SHORT, sides(SHORT_TOP, SHORT_SIDE, List.of()));
    List<Face> riser =
        List.of(
            Face.cable(Direction.SOUTH, RISER_FRONT, FaceRotation.ZERO),
            Face.cable(Direction.UP, RISER_TOP, FaceRotation.ZERO),
            Face.cable(Direction.EAST, RISER_EAST, FaceRotation.ZERO),
            Face.cable(Direction.WEST, RISER_WEST, FaceRotation.ZERO));
    element(model, RISER, riser);
    element(model, STEP_STRIPE, List.of(Face.stripe(Direction.UP, STEP_STRIPE_UV)));
    element(model, RISER_STRIPE, List.of(Face.stripe(Direction.SOUTH, RISER_STRIPE_UV)));
    element(model, RISER_TOP_STRIPE, List.of(Face.stripe(Direction.UP, RISER_TOP_STRIPE_UV)));
  }

  /** The top and the sides along the cable, east and west, and any other sides asked. */
  private static List<Face> sides(float[] top, float[] side, List<Direction> more) {
    List<Face> faces = new ArrayList<>();
    faces.add(Face.cable(Direction.UP, top, FaceRotation.ZERO));
    faces.add(Face.cable(Direction.EAST, side, FaceRotation.CLOCKWISE_90));
    faces.add(Face.cable(Direction.WEST, side, FaceRotation.CLOCKWISE_90));
    more.forEach(found -> faces.add(Face.cable(found, side, FaceRotation.CLOCKWISE_90)));
    return faces;
  }

  /** The end of the cable at the edge of its space, to the north. */
  private static List<Face> withEnd(List<Face> faces) {
    List<Face> all = new ArrayList<>(faces);
    all.add(Face.cable(Direction.NORTH, END, FaceRotation.ZERO));
    return all;
  }

  private BlockModelBuilder start(ResourceLocation name) {
    return models
        .getBuilder(name.getPath())
        .parent(models.getExistingFile(models.mcLoc(BLOCK_PARENT)))
        .texture(CABLE_KEY, models.modLoc(CABLE_TEXTURE))
        .texture(STRIPE_KEY, models.modLoc(STRIPE_TEXTURE))
        .texture(PARTICLE_KEY, models.modLoc(CABLE_TEXTURE));
  }

  private static void element(BlockModelBuilder model, float[] box, List<Face> faces) {
    var element = model.element().from(box[X1], box[Y1], box[Z1]).to(box[X2], box[Y2], box[Z2]);
    for (Face face : faces) {
      element
          .face(face.side())
          .uvs(face.uv()[U1], face.uv()[V1], face.uv()[U2], face.uv()[V2])
          .texture(face.tint() == STRIPE ? STRIPE_REF : CABLE_REF)
          .tintindex(face.tint())
          .rotation(face.rotation())
          .end();
    }
    element.end();
  }
}
