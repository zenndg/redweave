package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.NetworkControllerBlock;
import io.github.marcelofabianov.redweave.block.part.ButtonPart;
import io.github.marcelofabianov.redweave.block.part.DetectorPart;
import io.github.marcelofabianov.redweave.block.part.InverterPart;
import io.github.marcelofabianov.redweave.block.part.ItemSensorPart;
import io.github.marcelofabianov.redweave.block.part.LeverPart;
import io.github.marcelofabianov.redweave.block.part.LockPart;
import io.github.marcelofabianov.redweave.block.part.PressurePlatePart;
import io.github.marcelofabianov.redweave.block.part.RandomizerPart;
import io.github.marcelofabianov.redweave.block.part.TimerPart;
import io.github.marcelofabianov.redweave.item.RemoteLook;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.List;
import java.util.Locale;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/** The blockstates and models, with the textures of _docs/imagens/conceito/texturas.png (51). */
final class RedweaveBlockStates extends BlockStateProvider {
  private static final String CONTROLLER = "network_controller";
  private static final String TERMINAL = "network_terminal";
  private static final String CONFIGURATOR = "configurator";
  private static final String DISPLAY_SUFFIX = "_display";
  private static final String COPY_SUFFIX = "_copy";
  private static final String COPY_PROPERTY = "copy";
  private static final String ITEM_FOLDER = "item/";
  private static final String ITEM_GENERATED = "item/generated";
  private static final String LAYER_0 = "layer0";
  private static final String LAYER_1 = "layer1";
  private static final String LAYER_2 = "layer2";
  private static final float WITH_COPY = 1;
  private static final String SIGNAL_REMOTE = "signal_remote";
  private static final String SIGNAL_CABLE = "signal_cable";
  private static final String CABLE_INSULATION = "signal_cable_item";
  private static final String CABLE_TIPS = "signal_cable_item_tips";
  private static final String LOOK_PROPERTY = "look";
  private static final String LOOK_SEPARATOR = "_";
  private static final String LINKED_SUFFIX = "_linked";
  private static final String BLOCK_FOLDER = "block/";
  private static final String TOP = "_top";
  private static final String TOP_ON = "_top_on";
  private static final String FRONT = "_front";
  private static final String SIDE = "_side";
  private static final String BOTTOM = "_bottom";
  private static final String NODE = "signal_node";
  private static final String POWERED_SUFFIX = "_on";
  private static final String OUT_OF_RANGE_SUFFIX = "_out_of_range";
  private static final String NODE_SLOT = "_slot";
  private static final String SLOT_KEY = "slot";
  private static final String SLOT_REF = "#slot";
  private static final float DOT = 1;
  private static final float DOT_HEIGHT = 0.25F;

  /** The 4 corners of the plate, inside its copper frame, in the order of the first 4 slots. */
  private static final float[] CORNER_X = {4, 11, 4, 11};

  private static final float[] CORNER_Z = {4, 4, 11, 11};
  private static final String NODE_FRONT = "_front";
  private static final String NODE_EDGE = "_edge";
  private static final String FRONT_KEY = "front";
  private static final String EDGE_KEY = "edge";
  private static final String PARTICLE_KEY = "particle";
  private static final String FRONT_REF = "#front";
  private static final String EDGE_REF = "#edge";
  private static final String BLOCK_PARENT = "block/block";
  private static final float PLATE_EDGE = 3;
  private static final float PLATE_FAR_EDGE = 13;
  private static final float PLATE_THICKNESS = 2;
  private static final String TURNED_SUFFIX = "_turned";
  private static final String PART_SPACE = "part_space";
  private static final String LEVER_PARTS = "_parts";

  RedweaveBlockStates(PackOutput output, ExistingFileHelper files) {
    super(output, RedweaveMod.MOD_ID, files);
  }

  @Override
  protected void registerStatesAndModels() {
    ModelFile unlinked = controllerModel(CONTROLLER, TOP);
    ModelFile linked = controllerModel(CONTROLLER + LINKED_SUFFIX, TOP_ON);
    horizontalBlock(
        RedweaveBlocks.NETWORK_CONTROLLER.get(),
        state -> state.getValue(NetworkControllerBlock.LINKED) ? linked : unlinked);
    simpleBlockItem(RedweaveBlocks.NETWORK_CONTROLLER.get(), unlinked);
    ModelFile terminal = terminalModel();
    horizontalBlock(RedweaveBlocks.NETWORK_TERMINAL.get(), terminal);
    simpleBlockItem(RedweaveBlocks.NETWORK_TERMINAL.get(), terminal);
    nodeModel(NODE, NODE_FRONT);
    nodeModel(NODE + POWERED_SUFFIX, NODE_FRONT + POWERED_SUFFIX);
    nodeModel(NODE + OUT_OF_RANGE_SUFFIX, NODE_FRONT + OUT_OF_RANGE_SUFFIX);
    partSpaceModel();
    CableModels.register(models());
    LampModels.register(models(), itemModels());
    itemModels().basicItem(RedweaveItems.SIGNAL_NODE.get());
    configuratorModels();
    remoteModels();
    cableItemModel();
    timerModels();
    inverterModels();
    detectorModels();
    randomizerModels();
    leverModels();
    piecedModules();
  }

  /**
   * The Signal Timer: one model for each pair of LEDs, in two versions, with the top turned or not,
   * so the part space reaches the 24 pairs of `facing` and `front` (answers 401, 429 and 536).
   */
  private void timerModels() {
    plateModels(RedweaveItems.SIGNAL_TIMER.get(), TimerPart.ID, TimerPart.LOOKS);
  }

  /**
   * The Signal Randomizer: one model for each pair of the LED and the arrow, in two versions, with
   * the top turned or not, as the timer (answers 429 and 488).
   */
  private void randomizerModels() {
    plateModels(RedweaveItems.SIGNAL_RANDOMIZER.get(), RandomizerPart.ID, RandomizerPart.LOOKS);
  }

  /**
   * The Signal Inverter: one model for each look, off, on or burnt, in two versions, with the top
   * turned or not, as the timer (answers 429, 452 and 455).
   */
  private void inverterModels() {
    plateModels(RedweaveItems.SIGNAL_INVERTER.get(), InverterPart.ID, InverterPart.LOOKS);
  }

  /**
   * The Weather Detector: one model for each mode, with the arrow lit or not, in two versions, with
   * the top turned or not, as the other modules (answers 429 and 471).
   */
  private void detectorModels() {
    plateModels(RedweaveItems.WEATHER_DETECTOR.get(), DetectorPart.ID, DetectorPart.LOOKS);
  }

  /**
   * The Signal Lever: its plate, off or on, with the 3D base, handle and light over it, in two
   * versions, with the top turned or not, as the other modules (answers 429, 548 and 554).
   */
  private void leverModels() {
    ResourceLocation parts = modLoc(BLOCK_FOLDER + LeverPart.ID + LEVER_PARTS);
    for (String look : LeverPart.LOOKS) {
      ResourceLocation front = modLoc(BLOCK_FOLDER + LeverPart.ID + NODE_FRONT + look);
      boolean on = !look.isEmpty();
      LeverModels.addPieces(plateModel(LeverPart.ID + look, front, false), parts, on, false);
      LeverModels.addPieces(
          plateModel(LeverPart.ID + look + TURNED_SUFFIX, front, true), parts, on, true);
    }
    itemModels().basicItem(RedweaveItems.SIGNAL_LEVER.get());
  }

  /**
   * The Signal Button, the Signal Pressure Plate and the Signal Item Sensor: each plate with its 3D
   * pieces and the light over it, in two versions, with the top turned or not (429, 595, 609, 697).
   */
  private void piecedModules() {
    piecedModels(
        RedweaveItems.SIGNAL_BUTTON.get(),
        ButtonPart.ID,
        ButtonPart.LOOKS,
        ButtonModels::addPieces);
    piecedModels(
        RedweaveItems.SIGNAL_PRESSURE_PLATE.get(),
        PressurePlatePart.ID,
        PressurePlatePart.LOOKS,
        PressurePlateModels::addPieces);
    piecedModels(
        RedweaveItems.SIGNAL_ITEM_SENSOR.get(),
        ItemSensorPart.ID,
        ItemSensorPart.LOOKS,
        ItemSensorModels::addPieces);
    piecedModels(
        RedweaveItems.SIGNAL_LOCK.get(), LockPart.ID, LockPart.LOOKS, LockModels::addPieces);
  }

  /**
   * A module with 3D pieces and the light of the lever over its plate: every look, off or on, in
   * two versions, with the top turned or not, and its item (answers 429 and 554).
   */
  private void piecedModels(Item item, String id, List<String> looks, Pieces pieces) {
    ResourceLocation parts = modLoc(BLOCK_FOLDER + id + LEVER_PARTS);
    ResourceLocation light = modLoc(BLOCK_FOLDER + LeverPart.ID + LEVER_PARTS);
    for (String look : looks) {
      ResourceLocation front = modLoc(BLOCK_FOLDER + id + NODE_FRONT + look);
      boolean on = !look.isEmpty();
      pieces.add(plateModel(id + look, front, false), parts, light, on, false);
      pieces.add(plateModel(id + look + TURNED_SUFFIX, front, true), parts, light, on, true);
    }
    itemModels().basicItem(item);
  }

  /** How a module adds its 3D pieces to one model of its plate. */
  private interface Pieces {
    void add(
        BlockModelBuilder model,
        ResourceLocation parts,
        ResourceLocation light,
        boolean on,
        boolean turned);
  }

  /**
   * Every look of a module plate, in two versions, with the top turned or not, which the part space
   * turns to the 24 pairs of `facing` and `front` (answers 429 and 536), and its item.
   */
  private void plateModels(Item item, String id, List<String> looks) {
    for (String look : looks) {
      ResourceLocation front = modLoc(BLOCK_FOLDER + id + NODE_FRONT + look);
      plateModel(id + look, front, false);
      plateModel(id + look + TURNED_SUFFIX, front, true);
    }
    itemModels().basicItem(item);
  }

  /**
   * The plate of a module, with the edge of the timer and the given top, turned 90° on the UV when
   * asked (answer 429).
   */
  private BlockModelBuilder plateModel(String name, ResourceLocation front, boolean turned) {
    return models()
        .getBuilder(name)
        .parent(models().getExistingFile(mcLoc(BLOCK_PARENT)))
        .texture(FRONT_KEY, front)
        .texture(EDGE_KEY, timerTexture(NODE_EDGE))
        .texture(PARTICLE_KEY, timerTexture(NODE_EDGE))
        .element()
        .from(PLATE_EDGE, 0, PLATE_EDGE)
        .to(PLATE_FAR_EDGE, PLATE_THICKNESS, PLATE_FAR_EDGE)
        .allFaces((side, face) -> {
          face.texture(side == Direction.UP ? FRONT_REF : EDGE_REF);
          if (side == Direction.UP && turned) {
            face.rotation(ModelBuilder.FaceRotation.CLOCKWISE_90);
          }
        })
        .end();
  }

  private ResourceLocation timerTexture(String part) {
    return modLoc(BLOCK_FOLDER + TimerPart.ID + part);
  }

  /**
   * The Configurator with its display layer, tinted by the mode, and a second model with the copy
   * pixel, picked by the item property `copy` (answer 271).
   */
  private void configuratorModels() {
    ModelFile generated = new ModelFile.UncheckedModelFile(mcLoc(ITEM_GENERATED));
    ModelFile withCopy =
        itemModels()
            .getBuilder(CONFIGURATOR + COPY_SUFFIX)
            .parent(generated)
            .texture(LAYER_0, itemTexture(CONFIGURATOR))
            .texture(LAYER_1, itemTexture(CONFIGURATOR + DISPLAY_SUFFIX))
            .texture(LAYER_2, itemTexture(CONFIGURATOR + COPY_SUFFIX));
    itemModels()
        .getBuilder(CONFIGURATOR)
        .parent(generated)
        .texture(LAYER_0, itemTexture(CONFIGURATOR))
        .texture(LAYER_1, itemTexture(CONFIGURATOR + DISPLAY_SUFFIX))
        .override()
        .predicate(modLoc(COPY_PROPERTY), WITH_COPY)
        .model(withCopy)
        .end();
  }

  /**
   * The Signal Remote with no link, and one model for each other look, picked by the item property
   * `look`, the order of RemoteLook; the last override that matches wins (answer 362).
   */
  private void remoteModels() {
    ModelFile generated = new ModelFile.UncheckedModelFile(mcLoc(ITEM_GENERATED));
    ItemModelBuilder remote =
        itemModels()
            .getBuilder(SIGNAL_REMOTE)
            .parent(generated)
            .texture(LAYER_0, itemTexture(SIGNAL_REMOTE));
    for (RemoteLook look : RemoteLook.values()) {
      if (look == RemoteLook.UNLINKED) {
        continue;
      }
      String name = SIGNAL_REMOTE + LOOK_SEPARATOR + look.name().toLowerCase(Locale.ROOT);
      ModelFile model =
          itemModels().getBuilder(name).parent(generated).texture(LAYER_0, itemTexture(name));
      remote.override().predicate(modLoc(LOOK_PROPERTY), look.ordinal()).model(model).end();
    }
  }

  /**
   * The Signal Cable item in two layers: the gray insulation, tinted with the color of the cable,
   * and the copper tips, never tinted (answers 518 and 535).
   */
  private void cableItemModel() {
    itemModels()
        .getBuilder(SIGNAL_CABLE)
        .parent(new ModelFile.UncheckedModelFile(mcLoc(ITEM_GENERATED)))
        .texture(LAYER_0, itemTexture(CABLE_INSULATION))
        .texture(LAYER_1, itemTexture(CABLE_TIPS));
  }

  private ResourceLocation itemTexture(String name) {
    return modLoc(ITEM_FOLDER + name);
  }

  /**
   * The part space has no shape of its own: the client joins the models of its parts (answer 536),
   * and this one only gives the particles of breaking.
   */
  private void partSpaceModel() {
    ModelFile empty =
        models()
            .getBuilder(PART_SPACE)
            .parent(models().getExistingFile(mcLoc(BLOCK_PARENT)))
            .texture(PARTICLE_KEY, nodeTexture(NODE_EDGE));
    simpleBlock(RedweaveBlocks.PART_SPACE.get(), empty);
  }

  /**
   * A thin plate on the floor of the block, which the part space turns to each face (answers 72 and
   * 536).
   */
  private ModelFile nodeModel(String name, String front) {
    BlockModelBuilder model =
        models()
            .getBuilder(name)
            .parent(models().getExistingFile(mcLoc(BLOCK_PARENT)))
            .texture(FRONT_KEY, nodeTexture(front))
            .texture(EDGE_KEY, nodeTexture(NODE_EDGE))
            .texture(SLOT_KEY, nodeTexture(NODE_SLOT))
            .texture(PARTICLE_KEY, nodeTexture(NODE_EDGE))
            .element()
            .from(PLATE_EDGE, 0, PLATE_EDGE)
            .to(PLATE_FAR_EDGE, PLATE_THICKNESS, PLATE_FAR_EDGE)
            .allFaces((side, face) -> face.texture(side == Direction.UP ? FRONT_REF : EDGE_REF))
            .end();
    for (int corner = 0; corner < CORNER_X.length; corner++) {
      cornerDot(model, corner);
    }
    return model;
  }

  /**
   * A dot on a corner of the plate, tinted with the channel of a slot by the client: the tint index
   * is the slot, from 0 to 3 (answers 61 and 87).
   */
  private static void cornerDot(BlockModelBuilder model, int corner) {
    model
        .element()
        .from(CORNER_X[corner], PLATE_THICKNESS, CORNER_Z[corner])
        .to(CORNER_X[corner] + DOT, PLATE_THICKNESS + DOT_HEIGHT, CORNER_Z[corner] + DOT)
        .face(Direction.UP)
        .texture(SLOT_REF)
        .tintindex(corner)
        .end()
        .end();
  }

  private ResourceLocation nodeTexture(String part) {
    return modLoc(BLOCK_FOLDER + NODE + part);
  }

  /** The top shows the core: off without a network and lit with one (answer 64). */
  private ModelFile controllerModel(String name, String top) {
    return models()
        .orientableWithBottom(name, texture(SIDE), texture(FRONT), texture(BOTTOM), texture(top));
  }

  /** The bottom is the one of the controller, the same family (answers 251 and 258). */
  private ModelFile terminalModel() {
    return models()
        .orientableWithBottom(
            TERMINAL,
            terminalTexture(SIDE),
            terminalTexture(FRONT),
            texture(BOTTOM),
            terminalTexture(TOP));
  }

  private ResourceLocation terminalTexture(String part) {
    return modLoc(BLOCK_FOLDER + TERMINAL + part);
  }

  private ResourceLocation texture(String part) {
    return modLoc(BLOCK_FOLDER + CONTROLLER + part);
  }
}
