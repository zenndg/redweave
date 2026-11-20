package io.github.zenndg.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.part.DetectorPart;
import io.github.zenndg.redweave.block.part.LeverPart;
import io.github.zenndg.redweave.domain.signal.OutputDelay;
import io.github.zenndg.redweave.domain.signal.Signal;
import io.github.zenndg.redweave.menu.DelaySteps;
import io.github.zenndg.redweave.menu.LeverAction;
import io.github.zenndg.redweave.menu.LeverMenu;
import io.github.zenndg.redweave.registry.RedweaveItems;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The output delay of the other modules in the world, through the Signal Lever as the emitters and
 * the Weather Detector as the modules with a rule: the light and the state at once, the output
 * after the delay, the old saves, the copy and the screen (modulos-de-sinal.md, 845 to 855).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ModuleDelayGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(0, 1, 1);
  private static final BlockPos FIRST = STONE.above();
  private static final BlockPos OTHER_STONE = new BlockPos(2, 1, 1);
  private static final BlockPos SECOND = OTHER_STONE.above();
  private static final OutputDelay ONE_SECOND = new OutputDelay(OutputDelay.STEP_TICKS);
  private static final int BEFORE_THE_DELAY = 10;
  private static final int MENU_ID = 1;
  private static final int TWO_SECONDS = 2;
  private static final int NO_DELAY = 0;
  private static final String NOT_AT_ONCE = "The light and the state should follow at once";
  private static final String STILL = "The output should wait for the delay";
  private static final String NOT_THROUGH = "The output should follow after the delay";
  private static final String NOT_DEFAULT = "An old save should open with no delay";
  private static final String NOT_PASTED = "The paste should give the delay, never what waits";
  private static final String NOT_STEPPED = "Only the owner should step the delay";

  private ModuleDelayGameTests() {}

  /** The lever turns on at once, and its output follows after the delay (845 and 850). */
  @GameTest(template = EMPTY)
  public static void aLeverTurnsOnAtOnceAndItsOutputWaits(GameTestHelper helper) {
    LeverPart lever = lever(helper, CopyScene.copier(helper, new ArrayList<>()), STONE);

    helper
        .startSequence()
        .thenIdle(1)
        .thenExecute(() -> lever.setDelay(ONE_SECOND))
        .thenExecute(() -> lever.setLever(lever.lever().toggle()))
        .thenIdle(BEFORE_THE_DELAY)
        .thenExecute(() -> helper.assertTrue(lever.lever().on(), NOT_AT_ONCE))
        .thenExecute(() -> helper.assertValueEqual(lever.delayed().output(), Signal.OFF, STILL))
        .thenWaitUntil(
            () -> helper.assertValueEqual(lever.delayed().output(), Signal.MAX, NOT_THROUGH))
        .thenSucceed();
  }

  /** A lever and a detector saved before the #173 open with no delay (answer 844, rule 15). */
  @GameTest(template = EMPTY)
  public static void anOldSaveOpensWithNoDelay(GameTestHelper helper) {
    LeverPart lever = LeverPart.load(Direction.DOWN, new CompoundTag());
    DetectorPart detector = DetectorPart.load(Direction.DOWN, new CompoundTag());

    helper.assertValueEqual(lever.delayed().delay(), OutputDelay.NONE, NOT_DEFAULT);
    helper.assertValueEqual(detector.delayed().delay(), OutputDelay.NONE, NOT_DEFAULT);
    helper.assertTrue(lever.delayed().line().isEmpty(), NOT_DEFAULT);
    helper.succeed();
  }

  /** The delay and the line come back from the save of a detector (answers 836 and 844). */
  @GameTest(template = EMPTY)
  public static void aDetectorKeepsItsDelayInTheSave(GameTestHelper helper) {
    DetectorPart placed =
        CopyScene.place(
            helper,
            CopyScene.copier(helper, new ArrayList<>()),
            RedweaveItems.WEATHER_DETECTOR.get(),
            STONE,
            DetectorPart.class);
    placed.setDelay(ONE_SECOND);

    DetectorPart loaded = DetectorPart.load(Direction.DOWN, placed.save());

    helper.assertValueEqual(loaded.delayed().delay(), ONE_SECOND, NOT_DEFAULT);
    helper.succeed();
  }

  /** The delay goes to the other lever, never what waits (answers 841 and 850). */
  @GameTest(template = EMPTY)
  public static void aLeverPastesItsDelay(GameTestHelper helper) {
    Player owner = CopyScene.copier(helper, new ArrayList<>());
    LeverPart from = lever(helper, owner, STONE);
    LeverPart to = lever(helper, owner, OTHER_STONE);

    helper
        .startSequence()
        .thenIdle(1)
        .thenExecute(() -> from.setDelay(ONE_SECOND))
        .thenExecute(() -> from.setLever(from.lever().toggle()))
        .thenIdle(1)
        .thenExecute(() -> CopyScene.copyThenPaste(helper, owner, FIRST, SECOND))
        .thenExecute(() -> helper.assertValueEqual(to.delayed().delay(), ONE_SECOND, NOT_PASTED))
        .thenExecute(() -> helper.assertTrue(to.delayed().line().isEmpty(), NOT_PASTED))
        .thenSucceed();
  }

  /** The owner steps the delay from the screen, and anyone else changes nothing (830, 850). */
  @GameTest(template = EMPTY)
  public static void onlyTheOwnerStepsTheDelay(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    lever(helper, owner, STONE);
    ServerPlayer viewer = viewerAs(helper, owner);
    ServerPlayer stranger = viewerAs(helper, helper.makeMockPlayer(GameType.SURVIVAL));
    LeverMenu ownerMenu = new LeverMenu(MENU_ID, viewer, ModuleParts.spotOf(helper, FIRST));
    LeverMenu strangerMenu = new LeverMenu(MENU_ID, stranger, ModuleParts.spotOf(helper, FIRST));
    LeverAction longer = new LeverAction(LeverAction.Kind.DELAY, DelaySteps.LONGER);

    strangerMenu.clickMenuButton(stranger, longer.button());
    helper.assertValueEqual(ownerMenu.delaySeconds(), NO_DELAY, NOT_STEPPED);
    ownerMenu.clickMenuButton(viewer, longer.button());
    ownerMenu.clickMenuButton(viewer, longer.button());

    helper.assertValueEqual(ownerMenu.delaySeconds(), TWO_SECONDS, NOT_STEPPED);
    helper.succeed();
  }

  private static LeverPart lever(GameTestHelper helper, Player owner, BlockPos stone) {
    return CopyScene.place(helper, owner, CopyScene.lever(), stone, LeverPart.class);
  }

  /** A server player with the profile of the mock player, standing by the lever. */
  private static ServerPlayer viewerAs(GameTestHelper helper, Player player) {
    ServerPlayer viewer =
        FakePlayerFactory.get(
            helper.getLevel(), new GameProfile(player.getUUID(), player.getScoreboardName()));
    viewer.setPos(Vec3.atCenterOf(helper.absolutePos(FIRST.above())));
    return viewer;
  }
}
