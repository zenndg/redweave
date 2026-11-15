package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.item.SignalCableItem;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;

/**
 * The recipes of the mod, as datapack JSON a pack can override with KubeJS (rule 4). For now, the
 * 16 dyes of the Signal Cable: 8 cables of any color around a dye give 8 cables of that color, as
 * the stained glass of the game, and the white dye takes a cable back to white (answers 506, 562
 * and 563).
 */
public final class RedweaveRecipes extends RecipeProvider {
  /** The cables around the dye, and the cables the recipe gives. */
  private static final int DYED = 8;

  private static final String RING = "###";
  private static final String AROUND = "#D#";
  private static final char CABLE = '#';
  private static final char DYE = 'D';
  private static final String GROUP = "signal_cable";
  private static final String UNLOCKED_BY = "has_signal_cable";
  private static final String SUFFIX = "_signal_cable";

  RedweaveRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
    super(output, registries);
  }

  @Override
  protected void buildRecipes(RecipeOutput output) {
    for (ChannelColor color : ChannelColor.values()) {
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, SignalCableItem.stackOf(color, DYED))
          .pattern(RING)
          .pattern(AROUND)
          .pattern(RING)
          .define(CABLE, RedweaveItems.SIGNAL_CABLE.get())
          .define(DYE, DyeItem.byColor(DyeColor.byId(color.ordinal())))
          .group(GROUP)
          .unlockedBy(UNLOCKED_BY, has(RedweaveItems.SIGNAL_CABLE.get()))
          .save(output, idOf(color));
    }
  }

  /** `redweave:<color>_signal_cable`, as `red_stained_glass` in the game (answer 563). */
  public static ResourceLocation idOf(ChannelColor color) {
    return ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, color.key() + SUFFIX);
  }
}
