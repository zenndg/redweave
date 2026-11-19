package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.registry.RedweaveCreativeTabs;
import io.github.zenndg.redweave.registry.RedweaveItems;
import java.util.Collection;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * Every finished item of the mod is in the Redweave creative tab, for testing, even before it has a
 * recipe (answer 601).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class CreativeTabGameTests {
  private static final String EMPTY = "empty";
  private static final String MISSING = "The creative tab should hold ";

  private CreativeTabGameTests() {}

  @GameTest(template = EMPTY)
  public static void everyItemIsInTheCreativeTab(GameTestHelper helper) {
    CreativeModeTab tab = RedweaveCreativeTabs.REDWEAVE.get();
    tab.buildContents(
        new CreativeModeTab.ItemDisplayParameters(
            helper.getLevel().enabledFeatures(), false, helper.getLevel().registryAccess()));
    Collection<ItemStack> shown = tab.getDisplayItems();

    RedweaveItems.all()
        .forEach(entry -> helper.assertTrue(holds(shown, entry.get()), MISSING + entry.getId()));
    helper.succeed();
  }

  private static boolean holds(Collection<ItemStack> shown, Item item) {
    return shown.stream().anyMatch(stack -> stack.is(item));
  }
}
