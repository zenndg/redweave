package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.NetworkControllerBlock;
import io.github.marcelofabianov.redweave.block.SignalNodeBlock;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/** The blockstates and models, with the textures of _docs/imagens/conceito/texturas.png (51). */
final class RedweaveBlockStates extends BlockStateProvider {
  private static final String CONTROLLER = "network_controller";
  private static final String LINKED_SUFFIX = "_linked";
  private static final String BLOCK_FOLDER = "block/";
  private static final String TOP = "_top";
  private static final String TOP_ON = "_top_on";
  private static final String FRONT = "_front";
  private static final String SIDE = "_side";
  private static final String BOTTOM = "_bottom";
  private static final String NODE = "signal_node";
  private static final String POWERED_SUFFIX = "_on";
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
    ModelFile off = nodeModel(NODE, NODE_FRONT);
    ModelFile on = nodeModel(NODE + POWERED_SUFFIX, NODE_FRONT + POWERED_SUFFIX);
    directionalBlock(
        RedweaveBlocks.SIGNAL_NODE.get(),
        state -> state.getValue(SignalNodeBlock.POWERED) ? on : off);
    itemModels().basicItem(RedweaveItems.SIGNAL_NODE.get());
  }

  /** A thin plate on the floor of the block, turned to each face by the blockstate (answer 72). */
  private ModelFile nodeModel(String name, String front) {
    return models()
        .getBuilder(name)
        .parent(models().getExistingFile(mcLoc(BLOCK_PARENT)))
        .texture(FRONT_KEY, nodeTexture(front))
        .texture(EDGE_KEY, nodeTexture(NODE_EDGE))
        .texture(PARTICLE_KEY, nodeTexture(NODE_EDGE))
        .element()
        .from(PLATE_EDGE, 0, PLATE_EDGE)
        .to(PLATE_FAR_EDGE, PLATE_THICKNESS, PLATE_FAR_EDGE)
        .allFaces((side, face) -> face.texture(side == Direction.UP ? FRONT_REF : EDGE_REF))
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

  private ResourceLocation texture(String part) {
    return modLoc(BLOCK_FOLDER + CONTROLLER + part);
  }
}
