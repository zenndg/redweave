package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.part.RandomizerPart;
import io.github.zenndg.redweave.domain.module.Connection;
import io.github.zenndg.redweave.domain.module.RandomizerMode;
import io.github.zenndg.redweave.domain.module.RandomizerState;
import io.github.zenndg.redweave.domain.module.SignalRandomizer;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.item.ConfiguratorMode;
import io.github.zenndg.redweave.registry.RedweaveItems;
import java.util.Optional;
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
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Signal Randomizer in the world: _docs/funcionalidades/signal-randomizer.md, its placing, its
 * owner, its `Toggle` and its save (answers 480, 481, 482 and 489). The draws of the world are
 * random, so the tests check the state; the values are proven in the JUnit tests.
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SignalRandomizerGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos RANDOMIZER = STONE.above();
  private static final float LOOK_NORTH = 180;
  private static final int INTERVAL = 40;
  private static final int ELAPSED = 7;
  private static final int VALUE = 9;
  private static final int TOO_SHORT = 1;
  private static final String NAME = "Lucky lamp";
  private static final String ANALOG = "analog";
  private static final String RED = "red";
  private static final String DAMAGED = "dice";
  private static final UUID MODULE = UUID.fromString("2a3b4c5d-6e7f-4a8b-9c0d-1e2f3a4b5c6d");
  private static final UUID OWNER = UUID.fromString("3b4c5d6e-7f8a-4b9c-8d1e-2f3a4b5c6d7e");
  private static final UUID NETWORK = UUID.fromString("4c5d6e7f-8a9b-4c0d-9e2f-3a4b5c6d7e8f");
  private static final String NOT_OWNER = "Whoever places the randomizer should own it";
  private static final String NOT_STOPPED = "A new randomizer should be stopped and binary";
  private static final String NOT_RUNNING = "The Toggle should start the randomizer";
  private static final String NOT_STOPPED_AGAIN = "A second Toggle should stop it";
  private static final String BROKEN = "Only the owner should break the randomizer";
  private static final String NOT_KEPT = "The save of the randomizer should come back whole";
  private static final String NOT_SAFE = "A damaged mode should fall back to binary";
  private static final String NOT_DROPPED = "A damaged interval should drop the randomizer";
  private static final RandomizerState RUNNING = RandomizerState.RUNNING;
  private static final RandomizerState STOPPED = RandomizerState.STOPPED;

  private SignalRandomizerGameTests() {}

  /** Placed, it is stopped, binary, on its front edge, with its LED off (answer 482). */
  @GameTest(template = EMPTY)
  public static void aPlacedRandomizerIsStopped(GameTestHelper helper) {
    Player player = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, player);

    RandomizerPart placed = ModuleParts.only(helper, RANDOMIZER, RandomizerPart.class);
    SignalRandomizer randomizer = placed.randomizer().orElseThrow();
    helper.assertTrue(
        placed.module().orElseThrow().isOwner(new PlayerId(player.getUUID())), NOT_OWNER);
    helper.assertValueEqual(randomizer.state(), RandomizerState.STOPPED, NOT_STOPPED);
    helper.assertValueEqual(randomizer.mode(), RandomizerMode.BINARY, NOT_STOPPED);
    helper.assertValueEqual(placed.output(), Connection.EDGE, NOT_STOPPED);
    helper.succeedWhen(() -> {
      ModuleParts.assertRandomizerRunning(helper, RANDOMIZER, false);
      ModuleParts.assertPowered(helper, RANDOMIZER, false);
    });
  }

  /** The Toggle starts it, which lights its LED, and stops it again (answers 480 and 488). */
  @GameTest(template = EMPTY)
  public static void theToggleStartsAndStopsTheRandomizer(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner);
    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.TOGGLE);
    RandomizerPart block = ModuleParts.only(helper, RANDOMIZER, RandomizerPart.class);

    NodeScene.click(helper, owner, RANDOMIZER);

    helper
        .startSequence()
        .thenExecute(() -> helper.assertValueEqual(state(block), RUNNING, NOT_RUNNING))
        .thenWaitUntil(() -> ModuleParts.assertRandomizerRunning(helper, RANDOMIZER, true))
        .thenExecute(() -> NodeScene.click(helper, owner, RANDOMIZER))
        .thenExecute(() -> helper.assertValueEqual(state(block), STOPPED, NOT_STOPPED_AGAIN))
        .thenWaitUntil(() -> {
          ModuleParts.assertRandomizerRunning(helper, RANDOMIZER, false);
          ModuleParts.assertPowered(helper, RANDOMIZER, false);
        })
        .thenSucceed();
  }

  /** Another player cannot break the plate in survival; its owner can (answers 378 and 394). */
  @GameTest(template = EMPTY)
  public static void onlyTheOwnerBreaksTheRandomizer(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner);

    helper.assertFalse(GameTestActions.breakBy(helper, stranger, RANDOMIZER), BROKEN);
    ModuleParts.assertPresent(helper, RANDOMIZER);
    helper.assertTrue(GameTestActions.breakBy(helper, owner, RANDOMIZER), BROKEN);
    helper.succeed();
  }

  /** Its mode, its progress and its value come back whole, to go on from there (481). */
  @GameTest(template = EMPTY)
  public static void aRandomizerKeepsItsSaveWhole(GameTestHelper helper) {
    CompoundTag saved = fullSave(ANALOG, INTERVAL);

    RandomizerPart loaded = load(saved);

    CompoundTag written = loaded.save();
    helper.assertValueEqual(written, saved, NOT_KEPT);
    helper.succeed();
  }

  /** A damaged mode falls back to binary, with a warning (rule 14). */
  @GameTest(template = EMPTY)
  public static void aDamagedModeFallsBackToBinary(GameTestHelper helper) {
    RandomizerPart loaded = load(fullSave(DAMAGED, INTERVAL));

    helper.assertValueEqual(
        loaded.randomizer().map(SignalRandomizer::mode),
        Optional.of(RandomizerMode.BINARY),
        NOT_SAFE);
    helper.succeed();
  }

  /** A damaged interval drops the randomizer, and the block starts a new one on load (14). */
  @GameTest(template = EMPTY)
  public static void aDamagedIntervalDropsTheRandomizer(GameTestHelper helper) {
    RandomizerPart loaded = load(fullSave(ANALOG, TOO_SHORT));

    helper.assertTrue(loaded.randomizer().isEmpty(), NOT_DROPPED);
    helper.succeed();
  }

  private static RandomizerState state(RandomizerPart block) {
    return block.randomizer().orElseThrow().state();
  }

  private static void place(GameTestHelper helper, Player player) {
    helper.setBlock(STONE, Blocks.STONE);
    player.setYHeadRot(LOOK_NORTH);
    GameTestActions.placeOn(
        helper, player, new ItemStack(RedweaveItems.SIGNAL_RANDOMIZER.get()), STONE, Direction.UP);
  }

  /** The part on the floor, read from its tag as the space reads it (answer 543). */
  private static RandomizerPart load(CompoundTag tag) {
    return RandomizerPart.load(Direction.DOWN, tag);
  }

  /** The save of a running randomizer with a name, a network and its output on a channel. */
  private static CompoundTag fullSave(String mode, int interval) {
    CompoundTag tag = new CompoundTag();
    tag.putUUID(Keys.MODULE_ID, MODULE);
    tag.putUUID(Keys.OWNER, OWNER);
    tag.putString(Keys.NAME, NAME);
    tag.putUUID(Keys.NETWORK, NETWORK);
    tag.putString(Keys.MODE, mode);
    tag.putBoolean(Keys.RUNNING, true);
    tag.putInt(Keys.INTERVAL, interval);
    tag.putInt(Keys.ELAPSED, ELAPSED);
    tag.putInt(Keys.VALUE, VALUE);
    tag.putString(Keys.OUTPUT, RED);
    tag.putString(Keys.FRONT, Direction.NORTH.getSerializedName());
    tag.putBoolean(Keys.POWERED, true);
    return DelayKeys.addTo(tag);
  }

  /** The keys of answer 481, written here again, so a renamed key fails the test (rule 15). */
  private static final class Keys {
    static final String FRONT = "front";
    static final String POWERED = "powered";
    static final String MODULE_ID = "module_id";
    static final String OWNER = "owner";
    static final String NAME = "name";
    static final String NETWORK = "network";
    static final String MODE = "mode";
    static final String RUNNING = "running";
    static final String INTERVAL = "interval_ticks";
    static final String ELAPSED = "elapsed_ticks";
    static final String VALUE = "value";
    static final String OUTPUT = "output";

    private Keys() {}
  }
}
