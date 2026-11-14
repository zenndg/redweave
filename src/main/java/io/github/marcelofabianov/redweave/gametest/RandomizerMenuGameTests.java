package io.github.marcelofabianov.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.SignalRandomizerBlockEntity;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.RandomizerMode;
import io.github.marcelofabianov.redweave.domain.module.RandomizerState;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.RandomizerAction;
import io.github.marcelofabianov.redweave.menu.RandomizerMenu;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
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
 * The menu of the Signal Randomizer Screen on the server: what each button does and who may press
 * it (telas.md, Tela do Signal Randomizer, and answers 378, 398, 414 and 487). It opens no screen.
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class RandomizerMenuGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos RANDOMIZER = STONE.above();
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final float LOOK_NORTH = 180;
  private static final int MENU_ID = 1;
  private static final int SECOND = 20;
  private static final int SECOND_AND_A_TENTH = 22;
  private static final String NOT_CHANGED = "The owner should start it, pick a mode and step it";
  private static final String NO_NETWORK = "A channel needs the network of the module";
  private static final String NOT_JOINED = "The owner should pick a network and a channel";
  private static final String STRANGER = "Another player should change nothing";

  private RandomizerMenuGameTests() {}

  /** The owner starts it, picks a mode and steps the interval (answers 398, 480 and 487). */
  @GameTest(template = EMPTY)
  public static void theOwnerRunsAndStepsTheRandomizer(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner);
    ServerPlayer viewer = viewerAs(helper, owner);
    RandomizerMenu menu = menuOf(helper, viewer);

    click(menu, viewer, RandomizerAction.toggle());
    click(menu, viewer, RandomizerAction.mode(RandomizerMode.ANALOG));
    click(menu, viewer, RandomizerAction.step(true, true));
    click(menu, viewer, RandomizerAction.step(true, false));

    helper.assertValueEqual(menu.state(), Optional.of(RandomizerState.RUNNING), NOT_CHANGED);
    helper.assertValueEqual(menu.mode(), Optional.of(RandomizerMode.ANALOG), NOT_CHANGED);
    helper.assertValueEqual(menu.intervalTicks(), SECOND + SECOND_AND_A_TENTH, NOT_CHANGED);
    helper.succeed();
  }

  /** Another player sees the screen but changes nothing (answer 378). */
  @GameTest(template = EMPTY)
  public static void aStrangerChangesNothing(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner);
    ServerPlayer stranger = viewerAs(helper, helper.makeMockPlayer(GameType.SURVIVAL));
    RandomizerMenu menu = menuOf(helper, stranger);

    click(menu, stranger, RandomizerAction.toggle());
    click(menu, stranger, RandomizerAction.mode(RandomizerMode.ANALOG));
    click(menu, stranger, RandomizerAction.step(true, true));

    helper.assertFalse(menu.viewerIsOwner(), STRANGER);
    helper.assertValueEqual(menu.state(), Optional.of(RandomizerState.STOPPED), STRANGER);
    helper.assertValueEqual(menu.mode(), Optional.of(RandomizerMode.BINARY), STRANGER);
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
    RandomizerMenu menu = menuOf(helper, viewer);
    RandomizerAction red =
        new RandomizerAction(RandomizerAction.Kind.OUTPUT, ChannelColor.RED.ordinal() + 1);

    click(menu, viewer, red);
    helper.assertValueEqual(menu.output(), Connection.EDGE, NO_NETWORK);
    menu.broadcastChanges();
    click(menu, viewer, RandomizerAction.choose(0));
    click(menu, viewer, red);

    SignalRandomizerBlockEntity block = helper.getBlockEntity(RANDOMIZER);
    helper.assertValueEqual(
        block.module().flatMap(SignalModule::network), Optional.of(network), NOT_JOINED);
    helper.assertValueEqual(menu.output(), Connection.on(ChannelColor.RED), NOT_JOINED);
    helper.succeed();
  }

  private static void place(GameTestHelper helper, Player owner) {
    helper.setBlock(STONE, Blocks.STONE);
    owner.setYHeadRot(LOOK_NORTH);
    GameTestActions.placeOn(
        helper, owner, new ItemStack(RedweaveItems.SIGNAL_RANDOMIZER.get()), STONE, Direction.UP);
  }

  /** A server player with the profile of the mock player, standing by the randomizer. */
  private static ServerPlayer viewerAs(GameTestHelper helper, Player player) {
    ServerPlayer viewer =
        FakePlayerFactory.get(
            helper.getLevel(), new GameProfile(player.getUUID(), player.getScoreboardName()));
    viewer.setPos(Vec3.atCenterOf(helper.absolutePos(RANDOMIZER.above())));
    return viewer;
  }

  private static RandomizerMenu menuOf(GameTestHelper helper, ServerPlayer viewer) {
    return new RandomizerMenu(MENU_ID, viewer, helper.absolutePos(RANDOMIZER));
  }

  private static void click(RandomizerMenu menu, ServerPlayer viewer, RandomizerAction action) {
    menu.clickMenuButton(viewer, action.button());
  }
}
