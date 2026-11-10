package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.item.LabelItem;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/** The models of the items with more than one look, as the Label (answer 120). */
final class RedweaveItemModels extends ItemModelProvider {
  private static final String LABEL = "label";
  private static final String LABEL_WRITTEN = "label_written";
  private static final String ITEM_FOLDER = "item/";
  private static final String GENERATED = "item/generated";
  private static final String LAYER = "layer0";
  private static final float WRITTEN = 1;

  RedweaveItemModels(PackOutput output, ExistingFileHelper files) {
    super(output, RedweaveMod.MOD_ID, files);
  }

  @Override
  protected void registerModels() {
    ModelFile written = flat(LABEL_WRITTEN);
    getBuilder(LABEL)
        .parent(new ModelFile.UncheckedModelFile(GENERATED))
        .texture(LAYER, texture(LABEL))
        .override()
        .predicate(LabelItem.WRITTEN, WRITTEN)
        .model(written)
        .end();
  }

  private ModelFile flat(String name) {
    return getBuilder(name)
        .parent(new ModelFile.UncheckedModelFile(GENERATED))
        .texture(LAYER, texture(name));
  }

  private static ResourceLocation texture(String name) {
    return ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, ITEM_FOLDER + name);
  }
}
