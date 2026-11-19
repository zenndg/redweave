package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.part.FlipFlopPart;
import io.github.marcelofabianov.redweave.domain.module.FlipFlopInput;
import io.github.marcelofabianov.redweave.domain.signal.OutputDelay;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.ArrayList;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The save, the delay and the copy of the Signal Flip-Flop in the world: its keys, the input read
 * again on load with no edge, the output that waits while the state changes at once, and a paste
 * that keeps the state of the target (answers 851, 881, 886, 887 and 893).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FlipFlopSaveGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos LOADED = new BlockPos(1, 2, 1);
  private static final BlockPos BEHIND_LOADED = LOADED.south();
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos FLIP_FLOP = STONE.above();
  private static final BlockPos BEHIND = FLIP_FLOP.south();
  private static final BlockPos COPY_STONE = new BlockPos(0, 1, 1);
  private static final BlockPos COPY = COPY_STONE.above();
  private static final BlockPos OTHER_STONE = new BlockPos(2, 1, 1);
  private static final int SEVEN = 7;
  private static final int TOO_STRONG = 20;
  private static final int SETTLE = 10;
  private static final int ONE_SECOND_TICKS = 20;
  private static final OutputDelay ONE_SECOND = new OutputDelay(ONE_SECOND_TICKS);
  private static final String NAME = "Hall light";
  private static final String RED = "red";
  private static final String EDGE = "edge";
  private static final UUID MODULE = UUID.fromString("9d0e1f2a-3b4c-4d5e-8f6a-7b8c9d0e1f2a");
  private static final UUID OWNER = UUID.fromString("0e1f2a3b-4c5d-4e6f-9a7b-8c9d0e1f2a3b");
  private static final String NOT_KEPT = "The save of the flip-flop should come back whole";
  private static final String NOT_SAFE = "A damaged strength should fall back to 15";
  private static final String FLIPPED = "Loading with the input on should not flip it";
  private static final String NOT_UNREAD = "A save without input_on should leave the input unread";
  private static final String NO_KEY = "An unread input should not be saved";
  private static final String TOO_SOON = "The output should wait for the delay";
  private static final String NOT_AT_ONCE = "The state should change at once";
  private static final String NOT_PASTED = "The strength should be pasted";
  private static final String STATE_PASTED = "A paste should keep the state of the target";

  private FlipFlopSaveGameTests() {}

  /** Its connections, state, reading, strength and delay come back whole (886, rule 15). */
  @GameTest(template = EMPTY)
  public static void aFlipFlopKeepsItsSaveWhole(GameTestHelper helper) {
    CompoundTag saved = fullSave(true);
    saved.putBoolean(Keys.INPUT_ON, true);

    helper.assertValueEqual(FlipFlopPart.load(Direction.DOWN, saved).save(), saved, NOT_KEPT);
    helper.succeed();
  }

  /** A strength out of 1 to 15 goes back to 15 with a warning (rule 14). */
  @GameTest(template = EMPTY)
  public static void aDamagedStrengthFallsBackToFifteen(GameTestHelper helper) {
    CompoundTag saved = fullSave(false);
    saved.putInt(Keys.STRENGTH, TOO_STRONG);

    Signal strength = FlipFlopPart.load(Direction.DOWN, saved).flipFlop().strength();
    helper.assertValueEqual(strength, Signal.MAX, NOT_SAFE);
    helper.succeed();
  }

  /** Without `input_on`, the input is unread, and is saved only once read (answer 893). */
  @GameTest(template = EMPTY)
  public static void aSaveWithoutTheReadingLeavesTheInputUnread(GameTestHelper helper) {
    FlipFlopPart loaded = FlipFlopPart.load(Direction.DOWN, fullSave(false));

    helper.assertValueEqual(loaded.flipFlop().input(), FlipFlopInput.UNREAD, NOT_UNREAD);
    helper.assertFalse(loaded.save().contains(Keys.INPUT_ON), NO_KEY);
    helper.succeed();
  }

  /** Loaded on with its input on, the next tick is no edge, and it stays on (881 and 886). */
  @GameTest(template = EMPTY)
  public static void loadingWithTheInputOnDoesNotFlipIt(GameTestHelper helper) {
    helper.setBlock(BEHIND_LOADED, Blocks.REDSTONE_BLOCK);
    CompoundTag saved = edgeSave(true);
    saved.putBoolean(Keys.INPUT_ON, true);

    FlipFlopPart loaded = load(helper, saved);
    helper
        .startSequence()
        .thenIdle(SETTLE)
        .thenExecute(() -> helper.assertTrue(loaded.flipFlop().on(), FLIPPED))
        .thenSucceed();
  }

  /** Loaded without `input_on` and with its input on, it only reads it (answer 893). */
  @GameTest(template = EMPTY)
  public static void loadingWithoutTheReadingDoesNotFlipIt(GameTestHelper helper) {
    helper.setBlock(BEHIND_LOADED, Blocks.REDSTONE_BLOCK);

    FlipFlopPart loaded = load(helper, edgeSave(false));
    helper
        .startSequence()
        .thenIdle(SETTLE)
        .thenExecute(() -> helper.assertFalse(loaded.flipFlop().on(), FLIPPED))
        .thenSucceed();
  }

  /** With a delay, the state flips at once and only the output waits (answers 851 and 892). */
  @GameTest(template = EMPTY)
  public static void theOutputWaitsForTheDelay(GameTestHelper helper) {
    FlipFlopPart flipFlop =
        FlipFlopScene.place(helper, helper.makeMockPlayer(GameType.SURVIVAL), STONE);
    flipFlop.setDelay(ONE_SECOND);

    helper
        .startSequence()
        .thenIdle(SETTLE)
        .thenExecute(() -> helper.setBlock(BEHIND, Blocks.REDSTONE_BLOCK))
        .thenWaitUntil(() -> helper.assertTrue(flipFlop.flipFlop().on(), NOT_AT_ONCE))
        .thenExecute(() -> helper.assertFalse(flipFlop.powered(), TOO_SOON))
        .thenWaitUntil(() -> ModuleParts.assertPowered(helper, FLIP_FLOP, true))
        .thenSucceed();
  }

  /** A paste takes the strength and keeps the state of the target (answer 887). */
  @GameTest(template = EMPTY)
  public static void aPasteKeepsTheStateOfTheTarget(GameTestHelper helper) {
    Player owner = CopyScene.copier(helper, new ArrayList<>());
    FlipFlopPart from = CopyScene.place(helper, owner, item(), COPY_STONE, FlipFlopPart.class);
    FlipFlopPart to = CopyScene.place(helper, owner, item(), OTHER_STONE, FlipFlopPart.class);
    from.setFlipFlop(from.flipFlop().withStrength(SEVEN).toggle());

    CopyScene.copyThenPaste(helper, owner, COPY, OTHER_STONE.above());

    helper.assertValueEqual(to.flipFlop().strength(), new Signal(SEVEN), NOT_PASTED);
    helper.assertFalse(to.flipFlop().on(), STATE_PASTED);
    helper.succeed();
  }

  /** The flip-flop in a space on the stone, loaded as the game loads a chunk (answer 425). */
  private static FlipFlopPart load(GameTestHelper helper, CompoundTag data) {
    helper.setBlock(STONE, Blocks.STONE);
    return ModuleParts.loadInWorld(helper, LOADED, item(), data, FlipFlopPart.class);
  }

  private static Item item() {
    return RedweaveItems.SIGNAL_FLIP_FLOP.get();
  }

  /** A flip-flop on its edges, looking north so its input is behind, south (answer 878). */
  private static CompoundTag edgeSave(boolean on) {
    CompoundTag tag = fullSave(on);
    tag.putString(Keys.INPUT, EDGE);
    tag.putString(Keys.OUTPUT, EDGE);
    tag.remove(Keys.NAME);
    return tag;
  }

  /** The save of a named flip-flop, its output on a channel and a strength and a delay (886). */
  private static CompoundTag fullSave(boolean on) {
    CompoundTag tag = new CompoundTag();
    tag.putUUID(Keys.MODULE_ID, MODULE);
    tag.putUUID(Keys.OWNER, OWNER);
    tag.putString(Keys.NAME, NAME);
    tag.putString(Keys.INPUT, EDGE);
    tag.putString(Keys.OUTPUT, RED);
    tag.putBoolean(Keys.ON, on);
    tag.putInt(Keys.STRENGTH, SEVEN);
    tag.putInt(Keys.DELAY, ONE_SECOND_TICKS);
    tag.put(Keys.LINE, new ListTag());
    tag.putString(Keys.FRONT, Direction.NORTH.getSerializedName());
    tag.putBoolean(Keys.POWERED, false);
    return tag;
  }

  /** The keys of answer 886, written here again, so a renamed key fails the test (rule 15). */
  private static final class Keys {
    static final String FRONT = "front";
    static final String POWERED = "powered";
    static final String MODULE_ID = "module_id";
    static final String OWNER = "owner";
    static final String NAME = "name";
    static final String INPUT = "input";
    static final String OUTPUT = "output";
    static final String ON = "on";
    static final String INPUT_ON = "input_on";
    static final String STRENGTH = "strength";
    static final String DELAY = "output_delay_ticks";
    static final String LINE = "delay_line";

    private Keys() {}
  }
}
