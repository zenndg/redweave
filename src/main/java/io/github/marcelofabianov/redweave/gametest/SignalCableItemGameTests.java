package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.datagen.RedweaveRecipes;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.item.SignalCableItem;
import io.github.marcelofabianov.redweave.registry.RedweaveDataComponents;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Signal Cable as an item: the color in the component `cable_color`, none for white, which
 * survives the save, stacks only with the same color, and the 15 dye recipes, which take only white
 * cables (answers 494, 561 to 563, 762 and 763).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SignalCableItemGameTests {
  private static final String EMPTY = "empty";
  private static final int GRID = 3;
  private static final int CENTER = 4;
  private static final int DYED = 8;
  private static final String COMPONENTS = "components";
  private static final String COLOR_KEY = RedweaveMod.MOD_ID + ":cable_color";
  private static final String UNKNOWN_COLOR = "violet";
  private static final String WHITE_HAS_COMPONENT = "A white cable should have no color component";
  private static final String NOT_LOADED = "The cable should load from its save";
  private static final String WRONG_COLOR = "The cable should keep its color";
  private static final String NO_RECIPE = "Cables around a dye should match a recipe";
  private static final String WRONG_RECIPE = "The recipe should be named after its color";
  private static final String WRONG_COUNT = "The recipe should give 8 cables";
  private static final String SHOULD_STACK = "Cables of one color should stack";
  private static final String SHOULD_NOT_STACK = "Cables of two colors should not stack";
  private static final String DYED_TAKES_DYE = "A dyed cable should not take another dye";
  private static final String WHITE_DYE_RECIPE = "White cables and a white dye should not match";

  private SignalCableItemGameTests() {}

  @GameTest(template = EMPTY)
  public static void aWhiteCableHasNoColor(GameTestHelper helper) {
    ItemStack white = SignalCableItem.stackOf(ChannelColor.WHITE, 1);

    helper.assertFalse(white.has(RedweaveDataComponents.CABLE_COLOR.get()), WHITE_HAS_COMPONENT);
    helper.assertValueEqual(ChannelColor.WHITE, SignalCableItem.colorOf(white), WRONG_COLOR);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void aColorSurvivesTheSave(GameTestHelper helper) {
    HolderLookup.Provider registries = helper.getLevel().registryAccess();
    ItemStack red = SignalCableItem.stackOf(ChannelColor.RED, 1);

    Optional<ItemStack> loaded = ItemStack.parse(registries, red.save(registries));

    helper.assertTrue(loaded.isPresent(), NOT_LOADED);
    helper.assertValueEqual(ChannelColor.RED, SignalCableItem.colorOf(loaded.get()), WRONG_COLOR);
    helper.succeed();
  }

  /** A saved white component, as from an edited stack, loads as a plain white cable (561). */
  @GameTest(template = EMPTY)
  public static void aSavedWhiteLoadsWithoutTheComponent(GameTestHelper helper) {
    HolderLookup.Provider registries = helper.getLevel().registryAccess();
    ItemStack white = new ItemStack(RedweaveItems.SIGNAL_CABLE.get());
    white.set(RedweaveDataComponents.CABLE_COLOR.get(), ChannelColor.WHITE);

    ItemStack loaded = ItemStack.parse(registries, white.save(registries)).orElseThrow();

    helper.assertFalse(loaded.has(RedweaveDataComponents.CABLE_COLOR.get()), WHITE_HAS_COMPONENT);
    helper.succeed();
  }

  /** An unknown color, as from a damaged stack, gives a white cable instead of losing it (14). */
  @GameTest(template = EMPTY)
  public static void anUnknownColorLoadsWhite(GameTestHelper helper) {
    HolderLookup.Provider registries = helper.getLevel().registryAccess();
    CompoundTag saved = (CompoundTag) SignalCableItem.stackOf(ChannelColor.RED, 1).save(registries);
    saved.getCompound(COMPONENTS).putString(COLOR_KEY, UNKNOWN_COLOR);

    Optional<ItemStack> loaded = ItemStack.parse(registries, saved);

    helper.assertTrue(loaded.isPresent(), NOT_LOADED);
    helper.assertFalse(
        loaded.get().has(RedweaveDataComponents.CABLE_COLOR.get()), WHITE_HAS_COMPONENT);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void onlyTheSameColorStacks(GameTestHelper helper) {
    ItemStack red = SignalCableItem.stackOf(ChannelColor.RED, 1);
    ItemStack white = new ItemStack(RedweaveItems.SIGNAL_CABLE.get());

    helper.assertTrue(
        ItemStack.isSameItemSameComponents(red, SignalCableItem.stackOf(ChannelColor.RED, 1)),
        SHOULD_STACK);
    helper.assertTrue(
        ItemStack.isSameItemSameComponents(white, SignalCableItem.stackOf(ChannelColor.WHITE, 1)),
        SHOULD_STACK);
    helper.assertFalse(
        ItemStack.isSameItemSameComponents(red, SignalCableItem.stackOf(ChannelColor.BLUE, 1)),
        SHOULD_NOT_STACK);
    helper.succeed();
  }

  /** Each dye but white turns 8 white cables into 8 of its color (answers 762 and 763). */
  @GameTest(template = EMPTY)
  public static void eachDyeColorsEightWhiteCables(GameTestHelper helper) {
    for (ChannelColor color : ChannelColor.values()) {
      if (color == ChannelColor.WHITE) {
        continue;
      }
      CraftingInput input = CraftingInput.of(GRID, GRID, grid(ChannelColor.WHITE, color));
      Optional<RecipeHolder<CraftingRecipe>> recipe = recipeFor(helper, input);
      helper.assertTrue(recipe.isPresent(), NO_RECIPE);
      helper.assertValueEqual(RedweaveRecipes.idOf(color), recipe.get().id(), WRONG_RECIPE);
      ItemStack result = recipe.get().value().assemble(input, helper.getLevel().registryAccess());
      helper.assertValueEqual(DYED, result.getCount(), WRONG_COUNT);
      helper.assertTrue(
          ItemStack.isSameItemSameComponents(result, SignalCableItem.stackOf(color, 1)),
          WRONG_COLOR);
    }
    helper.succeed();
  }

  /** A dyed cable does not take another dye, and the white dye has no recipe (762 and 763). */
  @GameTest(template = EMPTY)
  public static void onlyWhiteCablesTakeADye(GameTestHelper helper) {
    CraftingInput dyed = CraftingInput.of(GRID, GRID, grid(ChannelColor.BLUE, ChannelColor.RED));
    CraftingInput white =
        CraftingInput.of(GRID, GRID, grid(ChannelColor.WHITE, ChannelColor.WHITE));

    helper.assertTrue(recipeFor(helper, dyed).isEmpty(), DYED_TAKES_DYE);
    helper.assertTrue(recipeFor(helper, white).isEmpty(), WHITE_DYE_RECIPE);
    helper.succeed();
  }

  private static Optional<RecipeHolder<CraftingRecipe>> recipeFor(
      GameTestHelper helper, CraftingInput input) {
    return helper
        .getLevel()
        .getRecipeManager()
        .getRecipeFor(RecipeType.CRAFTING, input, helper.getLevel());
  }

  /** Eight cables of one color around a dye. */
  private static List<ItemStack> grid(ChannelColor around, ChannelColor dye) {
    List<ItemStack> grid = new ArrayList<>();
    for (int slot = 0; slot < GRID * GRID; slot++) {
      grid.add(SignalCableItem.stackOf(around, 1));
    }
    grid.set(CENTER, new ItemStack(DyeItem.byColor(DyeColor.byId(dye.ordinal()))));
    return grid;
  }
}
