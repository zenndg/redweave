package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.DetectorMode;
import io.github.marcelofabianov.redweave.block.WeatherDetectorBlock;
import io.github.marcelofabianov.redweave.block.WeatherDetectorBlockEntity;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.WeatherMode;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.item.ConfiguratorMode;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Weather Detector in the world: _docs/funcionalidades/weather-detector.md, its placing, its
 * owner, its `Toggle` and its save (answers 462, 463, 467 and 472). The test server keeps a clear
 * sky, and the tests that change it run in a batch of their own.
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class WeatherDetectorGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos DETECTOR = STONE.above();
  private static final BlockPos LAMP_IN_FRONT = DETECTOR.north();
  private static final float LOOK_NORTH = 180;
  private static final String NAME = "Roof sensor";
  private static final String RED = "red";
  private static final String THUNDER = "thunder";
  private static final String DAMAGED = "snow";
  private static final UUID MODULE = UUID.fromString("9d0e1f2a-3b4c-4d5e-8f6a-7b8c9d0e1f2a");
  private static final UUID OWNER = UUID.fromString("0e1f2a3b-4c5d-4e6f-9a7b-8c9d0e1f2a3b");
  private static final UUID NETWORK = UUID.fromString("1f2a3b4c-5d6e-4f7a-8b8c-9d0e1f2a3b4c");
  private static final String NOT_OWNER = "Whoever places the detector should own it";
  private static final String NOT_RAIN = "A new detector should watch the rain on its edge";
  private static final String NOT_CLEAR = "Two Toggles should step the mode to Clear";
  private static final String BROKEN = "Only the owner should break the detector";
  private static final String NOT_KEPT = "The save of the detector should come back whole";
  private static final String NOT_SAFE = "A damaged mode should fall back to the rain";

  private WeatherDetectorGameTests() {}

  /** Placed under a clear sky, it watches the rain on its front edge and stays off (463). */
  @GameTest(template = EMPTY)
  public static void aPlacedDetectorWatchesTheRain(GameTestHelper helper) {
    Player player = helper.makeMockPlayer(GameType.SURVIVAL);
    helper.setBlock(LAMP_IN_FRONT, Blocks.REDSTONE_LAMP);
    place(helper, player);

    WeatherDetectorBlockEntity placed = helper.getBlockEntity(DETECTOR);
    helper.assertTrue(
        placed.module().orElseThrow().isOwner(new PlayerId(player.getUUID())), NOT_OWNER);
    helper.assertValueEqual(placed.detector().mode(), WeatherMode.RAIN, NOT_RAIN);
    helper.assertValueEqual(placed.output(), Connection.EDGE, NOT_RAIN);
    helper.succeedWhen(() -> {
      helper.assertBlockProperty(DETECTOR, WeatherDetectorBlock.MODE, DetectorMode.RAIN);
      helper.assertBlockProperty(DETECTOR, WeatherDetectorBlock.POWERED, false);
      helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, false);
    });
  }

  /** Two Toggles step the mode to Clear, which lights the lamp under a clear sky (462). */
  @GameTest(template = EMPTY)
  public static void theToggleStepsTheMode(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    helper.setBlock(LAMP_IN_FRONT, Blocks.REDSTONE_LAMP);
    place(helper, owner);
    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.TOGGLE);
    WeatherDetectorBlockEntity detector = helper.getBlockEntity(DETECTOR);

    NodeScene.click(helper, owner, DETECTOR);
    NodeScene.click(helper, owner, DETECTOR);

    helper.assertValueEqual(detector.detector().mode(), WeatherMode.CLEAR, NOT_CLEAR);
    helper.succeedWhen(() -> {
      helper.assertBlockProperty(DETECTOR, WeatherDetectorBlock.MODE, DetectorMode.CLEAR);
      helper.assertBlockProperty(DETECTOR, WeatherDetectorBlock.POWERED, true);
      helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, true);
    });
  }

  /** Another player cannot break the plate in survival; its owner can (answers 378 and 394). */
  @GameTest(template = EMPTY)
  public static void onlyTheOwnerBreaksTheDetector(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner);

    helper.assertFalse(GameTestActions.breakBy(helper, stranger, DETECTOR), BROKEN);
    helper.assertBlockPresent(RedweaveBlocks.WEATHER_DETECTOR.get(), DETECTOR);
    helper.assertTrue(GameTestActions.breakBy(helper, owner, DETECTOR), BROKEN);
    helper.succeed();
  }

  /** Its mode and its output come back whole (answer 467, rule 15). */
  @GameTest(template = EMPTY)
  public static void aDetectorKeepsItsSaveWhole(GameTestHelper helper) {
    CompoundTag saved = fullSave(THUNDER);

    WeatherDetectorBlockEntity loaded = load(helper, saved);

    CompoundTag written = loaded.saveWithoutMetadata(helper.getLevel().registryAccess());
    helper.assertValueEqual(written, saved, NOT_KEPT);
    helper.succeed();
  }

  /** A damaged mode falls back to the rain, with a warning, and the detector works (rule 14). */
  @GameTest(template = EMPTY)
  public static void aDamagedModeFallsBackToTheRain(GameTestHelper helper) {
    WeatherDetectorBlockEntity loaded = load(helper, fullSave(DAMAGED));

    helper.assertValueEqual(loaded.detector().mode(), WeatherMode.RAIN, NOT_SAFE);
    helper.succeed();
  }

  private static void place(GameTestHelper helper, Player player) {
    helper.setBlock(STONE, Blocks.STONE);
    player.setYHeadRot(LOOK_NORTH);
    GameTestActions.placeOn(
        helper, player, new ItemStack(RedweaveItems.WEATHER_DETECTOR.get()), STONE, Direction.UP);
  }

  /** A detector block entity at the plate, read from the tag as the game reads a chunk. */
  private static WeatherDetectorBlockEntity load(GameTestHelper helper, CompoundTag tag) {
    BlockState state = RedweaveBlocks.WEATHER_DETECTOR.get().defaultBlockState();
    WeatherDetectorBlockEntity loaded =
        new WeatherDetectorBlockEntity(helper.absolutePos(DETECTOR), state);
    loaded.loadWithComponents(tag, helper.getLevel().registryAccess());
    return loaded;
  }

  /** The save of a detector with a name, a network and its output on a channel. */
  private static CompoundTag fullSave(String mode) {
    CompoundTag tag = new CompoundTag();
    tag.putUUID(Keys.MODULE_ID, MODULE);
    tag.putUUID(Keys.OWNER, OWNER);
    tag.putString(Keys.NAME, NAME);
    tag.putUUID(Keys.NETWORK, NETWORK);
    tag.putString(Keys.MODE, mode);
    tag.putString(Keys.OUTPUT, RED);
    return tag;
  }

  /** The keys of answer 467, written here again, so a renamed key fails the test (rule 15). */
  private static final class Keys {
    static final String MODULE_ID = "module_id";
    static final String OWNER = "owner";
    static final String NAME = "name";
    static final String NETWORK = "network";
    static final String MODE = "mode";
    static final String OUTPUT = "output";

    private Keys() {}
  }
}
