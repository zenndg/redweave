package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.registry.RedweaveDataComponents;
import io.github.marcelofabianov.redweave.registry.RedweaveRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * One written Label with empty ones gives the empty ones written the same, and the written one
 * stays in the grid, as the game copies a written book (answers 110 and 117).
 */
public final class LabelCopyRecipe extends CustomRecipe {
  public LabelCopyRecipe(CraftingBookCategory category) {
    super(category);
  }

  @Override
  public boolean matches(CraftingInput input, Level level) {
    return LabelGrid.of(input).filter(grid -> grid.empty() > 0).isPresent();
  }

  @Override
  public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
    return LabelGrid.of(input)
        .filter(grid -> grid.empty() > 0)
        .map(grid -> grid.written().copyWithCount(grid.empty()))
        .orElse(ItemStack.EMPTY);
  }

  /** The written Label goes back to the grid, so the player keeps the original. */
  @Override
  public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
    NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
    for (int index = 0; index < input.size(); index++) {
      ItemStack stack = input.getItem(index);
      if (stack.has(RedweaveDataComponents.NODE_SLOTS.get())) {
        remaining.set(index, stack.copyWithCount(1));
      }
    }
    return remaining;
  }

  @Override
  public boolean canCraftInDimensions(int width, int height) {
    return width * height >= 2;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return RedweaveRecipes.LABEL_COPY.get();
  }
}
