package io.github.zenndg.redweave.datagen;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.item.SignalCableItem;
import io.github.zenndg.redweave.registry.RedweaveItems;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

/**
 * The recipes of the mod, as datapack JSON a pack can override with KubeJS (rule 4). Every item is
 * made of vanilla materials or items of the mod, with at least one redstone, and each recipe is
 * named after its item (answers 745 to 761, changed by 765 to 769). The 15 dyes of the Signal Cable
 * take only white cables (762 to 764).
 */
public final class RedweaveRecipes extends RecipeProvider {
  private static final int ONE = 1;
  private static final int NODES = 8;
  private static final int CABLES = 8;

  private static final char DEEPSLATE = 'D';
  private static final char COPPER = 'C';
  private static final char REDSTONE = 'R';
  private static final char BASE = 'B';
  private static final char PANE = 'G';
  private static final char PEARL = 'P';
  private static final char WOOL = 'W';
  private static final char VANILLA = 'X';
  private static final char NODE = 'N';
  private static final char LAMP = 'L';
  private static final char GLASS = 'S';
  private static final char CABLE = '#';
  private static final char DYE = 'D';

  private static final String CONTROLLER_TOP = "DRD";
  private static final String CONTROLLER_BOTTOM = "DBD";
  private static final String TERMINAL_TOP = "DGD";
  private static final String PEARL_ROW = "CPC";
  private static final String REDSTONE_BOTTOM = "DRD";
  private static final String COPPER_REDSTONE = "CRC";
  private static final String NODE_BOTTOM = "DPD";
  private static final String WOOL_ROW = "WWW";
  private static final String PROBE_TIP = "  R";
  private static final String PROBE_MIDDLE = " C ";
  private static final String PROBE_HANDLE = "C  ";
  private static final String REMOTE_TOP = "CGC";
  private static final String MODULE_TOP = " X ";
  private static final String MODULE_BOTTOM = "DND";
  private static final String LAMP_TOP = " S ";
  private static final String LAMP_BOTTOM = "DLD";
  private static final String RING = "###";
  private static final String AROUND = "#D#";

  private static final String DYE_GROUP = "signal_cable";
  private static final String HAS_REDSTONE = "has_redstone";
  private static final String HAS_SIGNAL_CABLE = "has_signal_cable";
  private static final String SUFFIX = "_signal_cable";

  RedweaveRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
    super(output, registries);
  }

  @Override
  protected void buildRecipes(RecipeOutput output) {
    networkRecipes(output);
    nodeAndCableRecipes(output);
    toolRecipes(output);
    moduleRecipes(output);
    dyeRecipes(output);
  }

  /** Answers 747 and 748, with the pearl of answer 768. */
  private void networkRecipes(RecipeOutput output) {
    shaped(RedweaveItems.NETWORK_CONTROLLER.get(), ONE)
        .pattern(CONTROLLER_TOP)
        .pattern(PEARL_ROW)
        .pattern(CONTROLLER_BOTTOM)
        .define(DEEPSLATE, Items.POLISHED_DEEPSLATE)
        .define(REDSTONE, Items.REDSTONE)
        .define(COPPER, Items.COPPER_INGOT)
        .define(PEARL, Items.ENDER_PEARL)
        .define(BASE, Items.REDSTONE_BLOCK)
        .save(output);
    shaped(RedweaveItems.NETWORK_TERMINAL.get(), ONE)
        .pattern(TERMINAL_TOP)
        .pattern(PEARL_ROW)
        .pattern(REDSTONE_BOTTOM)
        .define(DEEPSLATE, Items.POLISHED_DEEPSLATE)
        .define(PANE, Items.GLASS_PANE)
        .define(COPPER, Items.COPPER_INGOT)
        .define(PEARL, Items.ENDER_PEARL)
        .define(REDSTONE, Items.REDSTONE)
        .save(output);
  }

  /**
   * Answers 749 and 750, with the count of answer 765: the base recipe gives white cables, with no
   * color component.
   */
  private void nodeAndCableRecipes(RecipeOutput output) {
    shaped(RedweaveItems.SIGNAL_NODE.get(), NODES)
        .pattern(COPPER_REDSTONE)
        .pattern(NODE_BOTTOM)
        .define(COPPER, Items.COPPER_INGOT)
        .define(REDSTONE, Items.REDSTONE)
        .define(DEEPSLATE, Items.POLISHED_DEEPSLATE)
        .define(PEARL, Items.ENDER_PEARL)
        .save(output);
    shaped(RedweaveItems.SIGNAL_CABLE.get(), CABLES)
        .pattern(WOOL_ROW)
        .pattern(COPPER_REDSTONE)
        .pattern(WOOL_ROW)
        .define(WOOL, Items.WHITE_WOOL)
        .define(COPPER, Items.COPPER_INGOT)
        .define(REDSTONE, Items.REDSTONE)
        .save(output);
  }

  /** Answers 751 and 752, with the bottom row of answer 769. */
  private void toolRecipes(RecipeOutput output) {
    shaped(RedweaveItems.CONFIGURATOR.get(), ONE)
        .pattern(PROBE_TIP)
        .pattern(PROBE_MIDDLE)
        .pattern(PROBE_HANDLE)
        .define(REDSTONE, Items.REDSTONE)
        .define(COPPER, Items.COPPER_INGOT)
        .save(output);
    shaped(RedweaveItems.SIGNAL_REMOTE.get(), ONE)
        .pattern(REMOTE_TOP)
        .pattern(PEARL_ROW)
        .pattern(REDSTONE_BOTTOM)
        .define(COPPER, Items.COPPER_INGOT)
        .define(PANE, Items.GLASS_PANE)
        .define(PEARL, Items.ENDER_PEARL)
        .define(REDSTONE, Items.REDSTONE)
        .define(DEEPSLATE, Items.POLISHED_DEEPSLATE)
        .save(output);
  }

  /**
   * The shared shape of answers 753 and 766, with the vanilla item of each module (754 to 761 and
   * 885), and the lamp of answer 767, which takes the redstone lamp in place of the node.
   */
  private void moduleRecipes(RecipeOutput output) {
    Map.<ItemLike, ItemLike>ofEntries(
            Map.entry(RedweaveItems.SIGNAL_TIMER.get(), Items.CLOCK),
            Map.entry(RedweaveItems.SIGNAL_INVERTER.get(), Items.REDSTONE_TORCH),
            Map.entry(RedweaveItems.WEATHER_DETECTOR.get(), Items.LIGHTNING_ROD),
            Map.entry(RedweaveItems.SIGNAL_RANDOMIZER.get(), Items.DROPPER),
            Map.entry(RedweaveItems.SIGNAL_LEVER.get(), Items.LEVER),
            Map.entry(RedweaveItems.SIGNAL_BUTTON.get(), Items.STONE_BUTTON),
            Map.entry(RedweaveItems.SIGNAL_PRESSURE_PLATE.get(), Items.STONE_PRESSURE_PLATE),
            Map.entry(RedweaveItems.SIGNAL_ITEM_SENSOR.get(), Items.COMPARATOR),
            Map.entry(RedweaveItems.SIGNAL_LOCK.get(), Items.TRIPWIRE_HOOK),
            Map.entry(RedweaveItems.SIGNAL_ALARM.get(), Items.NOTE_BLOCK),
            Map.entry(RedweaveItems.SIGNAL_FLIP_FLOP.get(), Items.STICKY_PISTON))
        .forEach((module, vanilla) -> module(output, module, vanilla));
    lamp(output);
  }

  private void module(RecipeOutput output, ItemLike module, ItemLike vanilla) {
    shaped(module, ONE)
        .pattern(MODULE_TOP)
        .pattern(COPPER_REDSTONE)
        .pattern(MODULE_BOTTOM)
        .define(VANILLA, vanilla)
        .define(COPPER, Items.COPPER_INGOT)
        .define(REDSTONE, Items.REDSTONE)
        .define(DEEPSLATE, Items.POLISHED_DEEPSLATE)
        .define(NODE, RedweaveItems.SIGNAL_NODE.get())
        .save(output);
  }

  private void lamp(RecipeOutput output) {
    shaped(RedweaveItems.SIGNAL_LAMP.get(), ONE)
        .pattern(LAMP_TOP)
        .pattern(COPPER_REDSTONE)
        .pattern(LAMP_BOTTOM)
        .define(GLASS, Items.GLASS)
        .define(COPPER, Items.COPPER_INGOT)
        .define(REDSTONE, Items.REDSTONE)
        .define(DEEPSLATE, Items.POLISHED_DEEPSLATE)
        .define(LAMP, Items.REDSTONE_LAMP)
        .save(output);
  }

  /**
   * Eight white cables around a dye give 8 cables of its color, as the stained glass of the game.
   * The strict ingredient takes only a cable with no components, so a dyed cable keeps its color,
   * and there is no white dye recipe (answers 762 and 763).
   */
  private void dyeRecipes(RecipeOutput output) {
    for (ChannelColor color : ChannelColor.values()) {
      if (color == ChannelColor.WHITE) {
        continue;
      }
      ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, SignalCableItem.stackOf(color, CABLES))
          .pattern(RING)
          .pattern(AROUND)
          .pattern(RING)
          .define(
              CABLE,
              DataComponentIngredient.of(
                  true, DataComponentMap.EMPTY, RedweaveItems.SIGNAL_CABLE.get()))
          .define(DYE, DyeItem.byColor(DyeColor.byId(color.ordinal())))
          .group(DYE_GROUP)
          .unlockedBy(HAS_SIGNAL_CABLE, has(RedweaveItems.SIGNAL_CABLE.get()))
          .save(output, idOf(color));
    }
  }

  private static ShapedRecipeBuilder shaped(ItemLike result, int count) {
    return ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, result, count)
        .unlockedBy(HAS_REDSTONE, has(Items.REDSTONE));
  }

  /** `redweave:<color>_signal_cable`, as `red_stained_glass` in the game (answer 563). */
  public static ResourceLocation idOf(ChannelColor color) {
    return ResourceLocation.fromNamespaceAndPath(RedweaveMod.MOD_ID, color.key() + SUFFIX);
  }
}
