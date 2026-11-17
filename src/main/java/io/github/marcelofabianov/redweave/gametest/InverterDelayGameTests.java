package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.part.InverterPart;
import io.github.marcelofabianov.redweave.domain.module.DelayLine;
import io.github.marcelofabianov.redweave.domain.module.InverterSettings;
import io.github.marcelofabianov.redweave.domain.module.OutputDelay;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The strength and the output delay of the Signal Inverter in the world: the delay added to the
 * torch, the line saved and loaded, the old saves and the copy of the Configurator
 * (signal-inverter.md, answers 830 to 844).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class InverterDelayGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos INVERTER = STONE.above();
  private static final BlockPos BEHIND = INVERTER.south();
  private static final BlockPos COPY_STONE = new BlockPos(0, 1, 1);
  private static final BlockPos COPY = COPY_STONE.above();
  private static final BlockPos COPY_BEHIND = COPY.south();
  private static final BlockPos OTHER_STONE = new BlockPos(2, 1, 1);
  private static final BlockPos OTHER = OTHER_STONE.above();
  private static final float LOOK_NORTH = 180;
  private static final OutputDelay ONE_SECOND = new OutputDelay(OutputDelay.STEP_TICKS);
  private static final int SEVEN = 7;
  private static final int BEFORE_THE_DELAY = 15;
  private static final int WAITING = 12;
  private static final int HALF_SECOND = 10;
  private static final int DAMAGED_DELAY = 30;
  private static final int NEGATIVE = -4;
  private static final String STILL = "The output should wait for the delay";
  private static final String NOT_THROUGH = "The output should follow after the delay";
  private static final String WRONG_STRENGTH = "The inverter should give its strength";
  private static final String NOT_DEFAULT = "An old save should open with 15 and no delay";
  private static final String NOT_KEPT = "The line should come back from the save";
  private static final String NOT_FITTED = "An invalid delay should fall to whole seconds";
  private static final String NOT_DROPPED = "An invalid change should be dropped";
  private static final String NOT_PASTED = "The paste should give the strength and the delay";
  private static final String LINE_PASTED = "The paste should never give the line";

  private InverterDelayGameTests() {}

  /** A signal behind turns the output off only after the torch and the delay (830 and 833). */
  @GameTest(template = EMPTY)
  public static void theDelayHoldsTheOutputBack(GameTestHelper helper) {
    InverterPart inverter = place(helper, helper.makeMockPlayer(GameType.SURVIVAL), STONE);

    helper
        .startSequence()
        .thenWaitUntil(() -> ModuleParts.assertPowered(helper, INVERTER, true))
        .thenExecute(() -> inverter.setSettings(inverter.inverter().withDelay(ONE_SECOND)))
        .thenExecute(() -> helper.setBlock(BEHIND, Blocks.REDSTONE_BLOCK))
        .thenIdle(BEFORE_THE_DELAY)
        .thenExecute(() -> ModuleParts.assertPowered(helper, INVERTER, true))
        .thenExecute(() -> helper.assertTrue(!inverter.inverter().line().isEmpty(), STILL))
        .thenWaitUntil(() -> ModuleParts.assertPowered(helper, INVERTER, false))
        .thenExecute(() -> helper.assertTrue(inverter.inverter().line().isEmpty(), NOT_THROUGH))
        .thenSucceed();
  }

  /** On, it gives the strength its owner chose (answer 834). */
  @GameTest(template = EMPTY)
  public static void theInverterGivesItsStrength(GameTestHelper helper) {
    InverterPart inverter = place(helper, helper.makeMockPlayer(GameType.SURVIVAL), STONE);
    inverter.setSettings(inverter.inverter().withStrength(SEVEN));

    helper.succeedWhen(() ->
        helper.assertValueEqual(inverter.inverter().output(), new Signal(SEVEN), WRONG_STRENGTH));
  }

  /** A save from before the delay opens with 15, no delay and nothing waiting (844, rule 15). */
  @GameTest(template = EMPTY)
  public static void anOldSaveOpensWithFifteenAndNoDelay(GameTestHelper helper) {
    InverterPart loaded = InverterPart.load(Direction.DOWN, oldSave());

    helper.assertValueEqual(loaded.inverter().settings(), InverterSettings.DEFAULT, NOT_DEFAULT);
    helper.assertTrue(loaded.inverter().line().isEmpty(), NOT_DEFAULT);
    helper.succeed();
  }

  /** The changes waiting come back from the save, in order (answers 836 and 838). */
  @GameTest(template = EMPTY)
  public static void theLineComesBackFromTheSave(GameTestHelper helper) {
    CompoundTag tag = oldSave();
    tag.putInt(Keys.DELAY, ONE_SECOND.ticks());
    tag.put(Keys.LINE, line(change(true, WAITING)));

    InverterPart loaded = InverterPart.load(Direction.DOWN, tag);

    helper.assertValueEqual(loaded.inverter().settings().delay(), ONE_SECOND, NOT_KEPT);
    helper.assertValueEqual(
        loaded.inverter().line().pending(),
        List.of(new DelayLine.Pending(true, WAITING)),
        NOT_KEPT);
    helper.succeed();
  }

  /** A delay out of whole seconds and a change with negative ticks are fixed (rule 14). */
  @GameTest(template = EMPTY)
  public static void anInvalidDelayAndChangeAreFixed(GameTestHelper helper) {
    CompoundTag tag = oldSave();
    tag.putInt(Keys.DELAY, DAMAGED_DELAY);
    tag.put(Keys.LINE, line(change(true, NEGATIVE)));

    InverterPart loaded = InverterPart.load(Direction.DOWN, tag);

    helper.assertValueEqual(loaded.inverter().settings().delay(), ONE_SECOND, NOT_FITTED);
    helper.assertTrue(loaded.inverter().line().isEmpty(), NOT_DROPPED);
    helper.assertValueEqual(
        InverterPart.load(Direction.DOWN, withDelay(HALF_SECOND)).inverter().settings().delay(),
        OutputDelay.NONE,
        NOT_FITTED);
    helper.succeed();
  }

  /** The strength and the delay go to the other inverter, never what waits (answer 841). */
  @GameTest(template = EMPTY)
  public static void anInverterPastesItsStrengthAndDelay(GameTestHelper helper) {
    Player owner = CopyScene.copier(helper, new ArrayList<>());
    InverterPart from = place(helper, owner, COPY_STONE);
    InverterPart to = place(helper, owner, OTHER_STONE);
    from.setSettings(from.inverter().withStrength(SEVEN).withDelay(ONE_SECOND));

    helper
        .startSequence()
        .thenWaitUntil(() -> ModuleParts.assertPowered(helper, COPY, true))
        .thenExecute(() -> helper.setBlock(COPY_BEHIND, Blocks.REDSTONE_BLOCK))
        .thenWaitUntil(() -> helper.assertTrue(!from.inverter().line().isEmpty(), STILL))
        .thenExecute(() -> CopyScene.copyThenPaste(helper, owner, COPY, OTHER))
        .thenExecute(() ->
            helper.assertValueEqual(
                to.inverter().settings(), from.inverter().settings(), NOT_PASTED))
        .thenExecute(() -> helper.assertTrue(to.inverter().line().isEmpty(), LINE_PASTED))
        .thenSucceed();
  }

  /** Placed looking north, so its input is on the south edge (answer 441). */
  private static InverterPart place(GameTestHelper helper, Player owner, BlockPos stone) {
    owner.setYHeadRot(LOOK_NORTH);
    return CopyScene.place(
        helper, owner, RedweaveItems.SIGNAL_INVERTER.get(), stone, InverterPart.class);
  }

  /** The save of an inverter from before the #171, with no strength, delay nor line. */
  private static CompoundTag oldSave() {
    CompoundTag tag = new CompoundTag();
    tag.putString(Keys.INPUT, Keys.EDGE);
    tag.putString(Keys.OUTPUT, Keys.EDGE);
    tag.putInt(Keys.BURNOUT, 0);
    return tag;
  }

  private static CompoundTag withDelay(int ticks) {
    CompoundTag tag = oldSave();
    tag.putInt(Keys.DELAY, ticks);
    return tag;
  }

  private static ListTag line(CompoundTag change) {
    ListTag line = new ListTag();
    line.add(change);
    return line;
  }

  private static CompoundTag change(boolean on, int ticks) {
    CompoundTag change = new CompoundTag();
    change.putBoolean(Keys.ON, on);
    change.putInt(Keys.TICKS, ticks);
    return change;
  }

  /** The keys of answers 450 and 844, written here again, so a renamed key fails (rule 15). */
  private static final class Keys {
    static final String INPUT = "input";
    static final String OUTPUT = "output";
    static final String EDGE = "edge";
    static final String BURNOUT = "burnout_ticks";
    static final String DELAY = "output_delay_ticks";
    static final String LINE = "delay_line";
    static final String ON = "on";
    static final String TICKS = "ticks";

    private Keys() {}
  }
}
