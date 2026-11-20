package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.registry.RedweaveItems;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * The crafting recipes of the items in the mod: every item has one, named after it, that takes at
 * least one redstone, and each grid decided in answers 745 to 761, as changed by answers 765 to
 * 769, crafts its item.
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ItemRecipeGameTests {
  private static final String EMPTY = "empty";
  private static final int GRID = 3;
  private static final int ONE = 1;
  private static final int NODES = 8;
  private static final int CABLES = 8;
  private static final Item NONE = Items.AIR;
  private static final Item DEEPSLATE = Items.POLISHED_DEEPSLATE;
  private static final Item COPPER = Items.COPPER_INGOT;
  private static final Item REDSTONE = Items.REDSTONE;
  private static final Item PEARL = Items.ENDER_PEARL;
  private static final Item PANE = Items.GLASS_PANE;
  private static final Item WOOL = Items.WHITE_WOOL;
  private static final String NO_RECIPE = "The item should have a recipe: ";
  private static final String NO_REDSTONE = "The recipe should take redstone: ";
  private static final String NO_MATCH = "The grid should match a recipe: ";
  private static final String WRONG_RECIPE = "The grid should match the recipe of ";
  private static final String WRONG_RESULT = "The grid should craft ";
  private static final String WRONG_COUNT = "The grid should give the decided count of ";

  private ItemRecipeGameTests() {}

  /** A grid of the crafting table and what it should give. */
  private record Craft(Supplier<? extends Item> result, int count, List<Item> grid) {}

  @GameTest(template = EMPTY)
  public static void everyItemHasARecipeWithRedstone(GameTestHelper helper) {
    for (DeferredHolder<Item, ? extends Item> entry : RedweaveItems.all()) {
      Optional<RecipeHolder<?>> recipe = helper.getLevel().getRecipeManager().byKey(entry.getId());
      helper.assertTrue(recipe.isPresent(), NO_RECIPE + entry.getId());
      boolean redstone =
          recipe.get().value().getIngredients().stream()
              .anyMatch(ingredient -> ingredient.test(new ItemStack(REDSTONE)));
      helper.assertTrue(redstone, NO_REDSTONE + entry.getId());
    }
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void eachGridCraftsItsItem(GameTestHelper helper) {
    crafts().forEach(craft -> assertCrafts(helper, craft));
    helper.succeed();
  }

  private static void assertCrafts(GameTestHelper helper, Craft craft) {
    Item item = craft.result().get();
    CraftingInput input = CraftingInput.of(GRID, GRID, stacks(craft.grid()));
    Optional<RecipeHolder<CraftingRecipe>> recipe =
        helper
            .getLevel()
            .getRecipeManager()
            .getRecipeFor(RecipeType.CRAFTING, input, helper.getLevel());
    helper.assertTrue(recipe.isPresent(), NO_MATCH + item);
    helper.assertValueEqual(idOf(item), recipe.get().id().getPath(), WRONG_RECIPE + item);
    ItemStack result = recipe.get().value().assemble(input, helper.getLevel().registryAccess());
    helper.assertTrue(
        ItemStack.isSameItemSameComponents(result, new ItemStack(item)), WRONG_RESULT + item);
    helper.assertValueEqual(craft.count(), result.getCount(), WRONG_COUNT + item);
  }

  private static List<Craft> crafts() {
    return Stream.of(networkCrafts(), toolCrafts(), moduleCrafts()).flatMap(List::stream).toList();
  }

  /** Answers 747 to 750, with the controller of answer 768 and the count of answer 765. */
  private static List<Craft> networkCrafts() {
    return List.of(
        new Craft(
            RedweaveItems.NETWORK_CONTROLLER,
            ONE,
            List.of(
                DEEPSLATE,
                REDSTONE,
                DEEPSLATE,
                COPPER,
                PEARL,
                COPPER,
                DEEPSLATE,
                Items.REDSTONE_BLOCK,
                DEEPSLATE)),
        new Craft(
            RedweaveItems.NETWORK_TERMINAL,
            ONE,
            List.of(
                DEEPSLATE, PANE, DEEPSLATE, COPPER, PEARL, COPPER, DEEPSLATE, REDSTONE, DEEPSLATE)),
        new Craft(
            RedweaveItems.SIGNAL_NODE,
            NODES,
            List.of(COPPER, REDSTONE, COPPER, DEEPSLATE, PEARL, DEEPSLATE, NONE, NONE, NONE)),
        new Craft(
            RedweaveItems.SIGNAL_CABLE,
            CABLES,
            List.of(WOOL, WOOL, WOOL, COPPER, REDSTONE, COPPER, WOOL, WOOL, WOOL)));
  }

  /** Answers 751 and 752, with the remote of answer 769. */
  private static List<Craft> toolCrafts() {
    return List.of(
        new Craft(
            RedweaveItems.CONFIGURATOR,
            ONE,
            List.of(NONE, NONE, REDSTONE, NONE, COPPER, NONE, COPPER, NONE, NONE)),
        new Craft(
            RedweaveItems.SIGNAL_REMOTE,
            ONE,
            List.of(COPPER, PANE, COPPER, COPPER, PEARL, COPPER, DEEPSLATE, REDSTONE, DEEPSLATE)));
  }

  /**
   * The shared shape of answers 753 and 766, with the item of each module from answers 754 to 761,
   * and the lamp of answer 767.
   */
  private static List<Craft> moduleCrafts() {
    return List.of(
        module(RedweaveItems.SIGNAL_TIMER, Items.CLOCK),
        module(RedweaveItems.SIGNAL_INVERTER, Items.REDSTONE_TORCH),
        module(RedweaveItems.WEATHER_DETECTOR, Items.LIGHTNING_ROD),
        module(RedweaveItems.SIGNAL_RANDOMIZER, Items.DROPPER),
        module(RedweaveItems.SIGNAL_LEVER, Items.LEVER),
        module(RedweaveItems.SIGNAL_BUTTON, Items.STONE_BUTTON),
        module(RedweaveItems.SIGNAL_PRESSURE_PLATE, Items.STONE_PRESSURE_PLATE),
        module(RedweaveItems.SIGNAL_ITEM_SENSOR, Items.COMPARATOR),
        module(RedweaveItems.SIGNAL_LOCK, Items.TRIPWIRE_HOOK),
        module(RedweaveItems.SIGNAL_ALARM, Items.NOTE_BLOCK),
        module(RedweaveItems.SIGNAL_FLIP_FLOP, Items.STICKY_PISTON),
        module(RedweaveItems.SIGNAL_KEYPAD, Items.POLISHED_BLACKSTONE_BUTTON),
        lamp());
  }

  private static Craft lamp() {
    return new Craft(
        RedweaveItems.SIGNAL_LAMP,
        ONE,
        List.of(
            NONE,
            Items.GLASS,
            NONE,
            COPPER,
            REDSTONE,
            COPPER,
            DEEPSLATE,
            Items.REDSTONE_LAMP,
            DEEPSLATE));
  }

  private static Craft module(Supplier<? extends Item> module, Item vanilla) {
    return new Craft(
        module,
        ONE,
        List.of(NONE, vanilla, NONE, COPPER, REDSTONE, COPPER, DEEPSLATE, node(), DEEPSLATE));
  }

  /** Read when the test runs, after the items are registered. */
  private static Item node() {
    return RedweaveItems.SIGNAL_NODE.get();
  }

  private static List<ItemStack> stacks(List<Item> grid) {
    return grid.stream().map(ItemStack::new).toList();
  }

  private static String idOf(Item item) {
    return BuiltInRegistries.ITEM.getKey(item).getPath();
  }
}
