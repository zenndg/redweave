package io.github.zenndg.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.part.TimerPart;
import io.github.zenndg.redweave.domain.module.Connection;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.module.TimerState;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.menu.TimerAction;
import io.github.zenndg.redweave.menu.TimerMenu;
import io.github.zenndg.redweave.menu.TimerScreenCodes;
import io.github.zenndg.redweave.network.ModuleRenamed;
import io.github.zenndg.redweave.registry.RedweaveItems;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The menu of the Signal Timer Screen on the server: what each button does and who may press it
 * (telas.md, Tela do Signal Timer, and answers 378, 398, 400 and 414). It opens no screen.
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class TimerMenuGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos TIMER = STONE.above();
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final int MENU_ID = 1;
  private static final int SECOND = 20;
  private static final int SECOND_AND_A_TENTH = 22;
  private static final int FAR = 64;
  private static final String NAME = "Door clock";
  private static final String NOT_RUNNING = "The owner should start the timer";
  private static final String STEPPED = "A step should move the interval, Shift by 1 s";
  private static final String STRANGER = "Another player should change nothing";
  private static final String NO_NETWORK = "A channel needs the network of the module";
  private static final String NOT_JOINED = "The owner should pick a network and a channel";
  private static final String NOT_RENAMED = "The owner should rename the module";
  private static final String REACH = "The screen should close out of reach";

  private TimerMenuGameTests() {}

  /** The owner starts the timer and steps the interval and the pulse (answers 398 and 404). */
  @GameTest(template = EMPTY)
  public static void theOwnerRunsAndStepsTheTimer(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner);
    ServerPlayer viewer = viewerAs(helper, owner);
    TimerMenu menu = menuOf(helper, viewer);

    click(menu, viewer, TimerAction.toggle());
    click(menu, viewer, TimerAction.step(TimerAction.Kind.INTERVAL, true, true));
    click(menu, viewer, TimerAction.step(TimerAction.Kind.INTERVAL, true, false));

    helper.assertValueEqual(menu.state(), Optional.of(TimerState.RUNNING), NOT_RUNNING);
    helper.assertValueEqual(menu.intervalTicks(), SECOND + SECOND_AND_A_TENTH, STEPPED);
    helper.succeed();
  }

  /** Another player sees the screen but changes nothing (answer 378). */
  @GameTest(template = EMPTY)
  public static void aStrangerChangesNothing(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner);
    ServerPlayer stranger = viewerAs(helper, helper.makeMockPlayer(GameType.SURVIVAL));
    TimerMenu menu = menuOf(helper, stranger);

    click(menu, stranger, TimerAction.toggle());
    click(menu, stranger, TimerAction.step(TimerAction.Kind.INTERVAL, true, true));

    helper.assertFalse(menu.viewerIsOwner(), STRANGER);
    helper.assertValueEqual(menu.state(), Optional.of(TimerState.STOPPED), STRANGER);
    helper.assertValueEqual(menu.intervalTicks(), SECOND, STRANGER);
    helper.succeed();
  }

  /** A channel waits for a network; once picked, the output takes the channel (414). */
  @GameTest(template = EMPTY)
  public static void aChannelNeedsTheNetworkPickedFirst(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    place(helper, owner);
    ServerPlayer viewer = viewerAs(helper, owner);
    TimerMenu menu = menuOf(helper, viewer);
    TimerAction red = new TimerAction(TimerAction.Kind.OUTPUT, ChannelColor.RED.ordinal() + 1);

    click(menu, viewer, red);
    helper.assertValueEqual(menu.output(), Connection.EDGE, NO_NETWORK);
    menu.broadcastChanges();
    click(menu, viewer, TimerAction.choose(0));
    click(menu, viewer, red);

    TimerPart block = ModuleParts.only(helper, TIMER, TimerPart.class);
    helper.assertValueEqual(
        block.module().flatMap(SignalModule::network), Optional.of(network), NOT_JOINED);
    helper.assertValueEqual(menu.output(), Connection.on(ChannelColor.RED), NOT_JOINED);
    helper.succeed();
  }

  /** The owner renames the module from its open screen (answers 389 and 404). */
  @GameTest(template = EMPTY)
  public static void theOwnerRenamesTheModule(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner);
    ServerPlayer viewer = viewerAs(helper, owner);
    viewer.containerMenu = menuOf(helper, viewer);

    ModuleRenamed.receive(new ModuleRenamed(ModuleParts.spotOf(helper, TIMER), NAME), viewer);

    TimerPart block = ModuleParts.only(helper, TIMER, TimerPart.class);
    helper.assertValueEqual(
        block.module().flatMap(SignalModule::name), Optional.of(NAME), NOT_RENAMED);
    helper.succeed();
  }

  /** Out of reach, the game closes the screen, so no click reaches the server (rule 17). */
  @GameTest(template = EMPTY)
  public static void theScreenClosesOutOfReach(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner);
    ServerPlayer viewer = viewerAs(helper, owner);
    TimerMenu menu = menuOf(helper, viewer);

    helper.assertTrue(menu.stillValid(viewer), REACH);
    viewer.setPos(viewer.position().add(FAR, 0, FAR));
    helper.assertFalse(menu.stillValid(viewer), REACH);
    helper.succeed();
  }

  private static void place(GameTestHelper helper, Player owner) {
    helper.setBlock(STONE, Blocks.STONE);
    GameTestActions.placeOn(
        helper, owner, new ItemStack(RedweaveItems.SIGNAL_TIMER.get()), STONE, Direction.UP);
  }

  /** A server player with the profile of the mock player, standing by the timer. */
  private static ServerPlayer viewerAs(GameTestHelper helper, Player player) {
    ServerPlayer viewer =
        FakePlayerFactory.get(
            helper.getLevel(), new GameProfile(player.getUUID(), player.getScoreboardName()));
    viewer.setPos(Vec3.atCenterOf(helper.absolutePos(TIMER.above())));
    return viewer;
  }

  private static TimerMenu menuOf(GameTestHelper helper, ServerPlayer viewer) {
    return new TimerMenu(MENU_ID, viewer, ModuleParts.spotOf(helper, TIMER));
  }

  private static void click(TimerMenu menu, ServerPlayer viewer, TimerAction action) {
    menu.clickMenuButton(viewer, TimerScreenCodes.button(action));
  }
}
