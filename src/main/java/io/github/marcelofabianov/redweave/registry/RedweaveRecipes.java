package io.github.marcelofabianov.redweave.registry;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.item.LabelClearRecipe;
import io.github.marcelofabianov.redweave.item.LabelCopyRecipe;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * The recipe types of the mod. The recipes themselves are JSON in the datapack, so a pack can
 * override them with KubeJS (rule 4 and answer 123).
 */
public final class RedweaveRecipes {
  private static final String LABEL_COPY_ID = "label_copy";
  private static final String LABEL_CLEAR_ID = "label_clear";

  private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
      DeferredRegister.create(Registries.RECIPE_SERIALIZER, RedweaveMod.MOD_ID);

  public static final Supplier<RecipeSerializer<LabelCopyRecipe>> LABEL_COPY =
      SERIALIZERS.register(
          LABEL_COPY_ID, () -> new SimpleCraftingRecipeSerializer<>(LabelCopyRecipe::new));

  public static final Supplier<RecipeSerializer<LabelClearRecipe>> LABEL_CLEAR =
      SERIALIZERS.register(
          LABEL_CLEAR_ID, () -> new SimpleCraftingRecipeSerializer<>(LabelClearRecipe::new));

  private RedweaveRecipes() {}

  public static void register(IEventBus modBus) {
    SERIALIZERS.register(modBus);
  }
}
