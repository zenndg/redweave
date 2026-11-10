package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.registry.RedweaveRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/** A written Label alone in the grid gives an empty Label, away from normal use (answer 116). */
public final class LabelClearRecipe extends CustomRecipe {
  public LabelClearRecipe(CraftingBookCategory category) {
    super(category);
  }

  @Override
  public boolean matches(CraftingInput input, Level level) {
    return LabelGrid.of(input).filter(grid -> grid.empty() == 0).isPresent();
  }

  @Override
  public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
    return new ItemStack(RedweaveItems.LABEL.get());
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return width * height >= 1;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return RedweaveRecipes.LABEL_CLEAR.get();
  }
}
