package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.part.TimerPart;
import io.github.zenndg.redweave.config.NeoForgeTimerLimits;
import io.github.zenndg.redweave.domain.module.Connection;
import io.github.zenndg.redweave.domain.module.SignalTimer;
import io.github.zenndg.redweave.domain.module.TimerCycle;
import io.github.zenndg.redweave.domain.network.PlayerId;
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

/** The Signal Timer in the world: _docs/funcionalidades/signal-timer.md (answer 406). */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SignalTimerGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos ON_TOP = STONE.above();
  private static final float LOOK_NORTH = 180;
  private static final float LOOK_DOWN = 90;
  private static final int INTERVAL = 40;
  private static final int PULSE = 6;
  private static final int ELAPSED = 7;
  private static final String NAME = "Clock";
  private static final String RED = "red";
  private static final String EDGE = "edge";
  private static final String UNKNOWN = "plaid";
  private static final UUID MODULE = UUID.fromString("2c3d4e5f-6a7b-4c8d-9e0f-1a2b3c4d5e6f");
  private static final UUID OWNER = UUID.fromString("3d4e5f6a-7b8c-4d9e-8f10-2b3c4d5e6f70");
  private static final UUID NETWORK = UUID.fromString("4e5f6a7b-8c9d-4eaf-9021-3c4d5e6f7081");
  private static final String NOT_OWNER = "Whoever places the timer should own it";
  private static final String NOT_STOPPED = "A new timer should be stopped, 1 s with a 0.5 s pulse";
  private static final String NOT_EDGES = "A new timer should use its edges";
  private static final String NOT_KEPT = "The save of the timer should come back whole";
  private static final String NOT_FITTED = "A saved interval outside the config should fit it";
  private static final String NOT_SAFE = "A damaged save should fall back to a stopped timer";

  private SignalTimerGameTests() {}

  /**
   * On the floor, the front points where the player looks, as a repeater (answer 399). The game
   * reads the look of a living entity from its head.
   */
  @GameTest(template = EMPTY)
  public static void aTimerOnTheFloorPointsWhereItsPlacerLooks(GameTestHelper helper) {
    Player player = helper.makeMockPlayer(GameType.SURVIVAL);
    player.setYHeadRot(LOOK_NORTH);
    place(helper, player, Direction.UP);

    ModuleParts.assertFacing(helper, ON_TOP, Direction.UP);
    ModuleParts.assertFront(helper, ON_TOP, Direction.NORTH);
    helper.succeed();
  }

  /** On a wall, the front can point down, which a rotation on the floor cannot give (428). */
  @GameTest(template = EMPTY)
  public static void aTimerOnAWallPointsDownWhenItsPlacerLooksDown(GameTestHelper helper) {
    Player player = helper.makeMockPlayer(GameType.SURVIVAL);
    player.setXRot(LOOK_DOWN);
    place(helper, player, Direction.EAST);

    ModuleParts.assertFacing(helper, STONE.east(), Direction.EAST);
    ModuleParts.assertFront(helper, STONE.east(), Direction.DOWN);
    helper.succeed();
  }

  /** A new timer belongs to its placer, stopped, on its edges (answers 390 and 416). */
  @GameTest(template = EMPTY)
  public static void aPlacedTimerIsStoppedAndBelongsToItsPlacer(GameTestHelper helper) {
    Player player = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, player, Direction.UP);
    TimerPart placed = ModuleParts.only(helper, ON_TOP, TimerPart.class);

    PlayerId owner = new PlayerId(player.getUUID());
    helper.assertTrue(placed.module().orElseThrow().isOwner(owner), NOT_OWNER);
    SignalTimer stopped = SignalTimer.stopped(TimerCycle.initial(new NeoForgeTimerLimits()));
    helper.assertValueEqual(placed.timer().orElseThrow(), stopped, NOT_STOPPED);
    helper.assertTrue(placed.output().isEdge() && placed.pause().isEdge(), NOT_EDGES);
    ModuleParts.assertTimerRunning(helper, ON_TOP, false);
    helper.succeed();
  }

  /** Every key of answer 411 is read and written back as it was (rule 15). */
  @GameTest(template = EMPTY)
  public static void aTimerKeepsItsSaveWhole(GameTestHelper helper) {
    CompoundTag saved = fullSave(INTERVAL);

    TimerPart loaded = load(saved);

    CompoundTag written = loaded.save();
    helper.assertValueEqual(written, saved, NOT_KEPT);
    helper.succeed();
  }

  /**
   * The interval goes to the nearest limit and the cycle restarts, on load (answer 425): the timer
   * ticks again from 0, far from where it was saved.
   */
  @GameTest(template = EMPTY)
  public static void aSavedIntervalOutsideTheConfigFitsIt(GameTestHelper helper) {
    int longest = new NeoForgeTimerLimits().maxIntervalTicks();
    helper.setBlock(STONE, Blocks.STONE);

    CompoundTag saved = fullSave(longest + INTERVAL);
    saved.putInt(Keys.ELAPSED, longest / 2);
    TimerPart loaded = inWorld(helper, saved);

    helper.succeedWhen(() -> {
      SignalTimer timer = loaded.timer().orElseThrow();
      helper.assertValueEqual(timer.cycle().intervalTicks(), longest, NOT_FITTED);
      helper.assertTrue(timer.elapsedTicks() < INTERVAL, NOT_FITTED);
    });
  }

  /** A damaged part is dropped and the rest kept: no cycle starts a stopped timer (rule 14). */
  @GameTest(template = EMPTY)
  public static void aDamagedSaveFallsBackToAStoppedTimer(GameTestHelper helper) {
    helper.setBlock(STONE, Blocks.STONE);
    CompoundTag damaged = fullSave(0);
    damaged.putString(Keys.OUTPUT, UNKNOWN);

    TimerPart loaded = inWorld(helper, damaged);

    helper.succeedWhen(() -> {
      SignalTimer stopped = SignalTimer.stopped(TimerCycle.initial(new NeoForgeTimerLimits()));
      helper.assertValueEqual(loaded.timer(), Optional.of(stopped), NOT_SAFE);
      helper.assertValueEqual(loaded.output(), Connection.EDGE, NOT_SAFE);
      helper.assertTrue(loaded.module().isPresent(), NOT_SAFE);
    });
  }

  private static void place(GameTestHelper helper, Player player, Direction face) {
    helper.setBlock(STONE, Blocks.STONE);
    GameTestActions.placeOn(
        helper, player, new ItemStack(RedweaveItems.SIGNAL_TIMER.get()), STONE, face);
  }

  /** The timer in a space on the stone, loaded as the game loads a chunk (answer 425). */
  private static TimerPart inWorld(GameTestHelper helper, CompoundTag tag) {
    return ModuleParts.loadInWorld(
        helper, ON_TOP, RedweaveItems.SIGNAL_TIMER.get(), tag, TimerPart.class);
  }

  /** The part on the floor, read from its tag as the space reads it (answer 543). */
  private static TimerPart load(CompoundTag tag) {
    return TimerPart.load(Direction.DOWN, tag);
  }

  /** The save of a running timer with a name, a network and its output on a channel. */
  private static CompoundTag fullSave(int interval) {
    CompoundTag tag = new CompoundTag();
    tag.putUUID(Keys.MODULE_ID, MODULE);
    tag.putUUID(Keys.OWNER, OWNER);
    tag.putString(Keys.NAME, NAME);
    tag.putUUID(Keys.NETWORK, NETWORK);
    tag.putBoolean(Keys.RUNNING, true);
    tag.putInt(Keys.INTERVAL, interval);
    tag.putInt(Keys.PULSE, PULSE);
    tag.putInt(Keys.ELAPSED, ELAPSED);
    tag.putString(Keys.OUTPUT, RED);
    tag.putString(Keys.PAUSE, EDGE);
    tag.putString(Keys.FRONT, Direction.NORTH.getSerializedName());
    tag.putBoolean(Keys.POWERED, true);
    return tag;
  }

  /** The keys of answer 411, written here again, so a renamed key fails the test (rule 15). */
  private static final class Keys {
    static final String FRONT = "front";
    static final String POWERED = "powered";
    static final String MODULE_ID = "module_id";
    static final String OWNER = "owner";
    static final String NAME = "name";
    static final String NETWORK = "network";
    static final String RUNNING = "running";
    static final String INTERVAL = "interval_ticks";
    static final String PULSE = "pulse_ticks";
    static final String ELAPSED = "elapsed_ticks";
    static final String OUTPUT = "output";
    static final String PAUSE = "pause";

    private Keys() {}
  }
}
