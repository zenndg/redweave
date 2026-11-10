package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.NetworkControllerBlock;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
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
