package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.registry.RedweaveDataComponents;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.Optional;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;

/**
 * A crafting grid with exactly one written Label, some empty Labels and nothing else, the shape
 * both recipes of the Label read (answers 116 and 117).
 */
record LabelGrid(ItemStack written, int empty) {
  static Optional<LabelGrid> of(CraftingInput input) {
    ItemStack written = ItemStack.EMPTY;
    int empty = 0;
    for (int index = 0; index < input.size(); index++) {
      ItemStack stack = input.getItem(index);
      if (stack.isEmpty()) {
        continue;
      }
      if (!stack.is(RedweaveItems.LABEL.get())) {
        return Optional.empty();
      }
      if (!stack.has(RedweaveDataComponents.NODE_SLOTS.get())) {
        empty++;
      } else if (written.isEmpty()) {
        written = stack;
      } else {
        return Optional.empty();
      }
    }
    return written.isEmpty() ? Optional.empty() : Optional.of(new LabelGrid(written, empty));
  }
}
