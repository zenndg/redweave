package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.RedweaveMod;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The GameTests run as a dedicated server without Jade and The One Probe, so this proves the mod
 * opens without them (AGENTS.md, rules 20 and 39, and answer 18).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class WithoutOptionalModsGameTests {
  private static final String EMPTY = "empty";
  private static final String JADE_ID = "jade";
  private static final String THE_ONE_PROBE_ID = "theoneprobe";
  private static final String NOT_LOADED = "The mod should load on a dedicated server";
  private static final String JADE_LOADED = "These tests must run without Jade";
  private static final String THE_ONE_PROBE_LOADED = "These tests must run without The One Probe";

  private WithoutOptionalModsGameTests() {}

  @GameTest(template = EMPTY)
  public static void theModOpensWithoutTheOptionalMods(GameTestHelper helper) {
    ModList mods = ModList.get();
    helper.assertTrue(mods.isLoaded(RedweaveMod.MOD_ID), NOT_LOADED);
    helper.assertFalse(mods.isLoaded(JADE_ID), JADE_LOADED);
    helper.assertFalse(mods.isLoaded(THE_ONE_PROBE_ID), THE_ONE_PROBE_LOADED);
    helper.succeed();
  }
}
