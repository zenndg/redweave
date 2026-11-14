package io.github.marcelofabianov.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.WeatherDetectorBlockEntity;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.DetectorState;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.module.Weather;
import io.github.marcelofabianov.redweave.domain.module.WeatherMode;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.DetectorAction;
import io.github.marcelofabianov.redweave.menu.DetectorMenu;
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
 * The menu of the Weather Detector Screen on the server: what each button does and who may press it
 * (telas.md, Tela do Weather Detector, and answers 378, 414 and 470). It opens no screen.
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class DetectorMenuGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos DETECTOR = STONE.above();
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final float LOOK_NORTH = 180;
  private static final int MENU_ID = 1;
  private static final String NOT_PICKED = "The owner should pick the mode";
  private static final String NO_NETWORK = "A channel needs the network of the module";
  private static final String NOT_JOINED = "The owner should pick a network and a channel";
  private static final String STRANGER = "Another player should change nothing";
  private static final String NOT_SHOWN = "The menu should show the state and the weather";

  private DetectorMenuGameTests() {}

  /** The owner picks a mode on the screen (answer 470). */
  @GameTest(template = EMPTY)
  public static void theOwnerPicksTheMode(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner);
    ServerPlayer viewer = viewerAs(helper, owner);
    DetectorMenu menu = menuOf(helper, viewer);

    click(menu, viewer, DetectorAction.mode(WeatherMode.THUNDER));

    helper.assertValueEqual(menu.mode(), Optional.of(WeatherMode.THUNDER), NOT_PICKED);
    helper.succeed();
  }

  /** A channel waits for a network; once picked, the output takes the channel (414). */
  @GameTest(template = EMPTY)
  public static void aChannelNeedsTheNetworkPickedFirst(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    place(helper, owner);
    ServerPlayer viewer = viewerAs(helper, owner);
    DetectorMenu menu = menuOf(helper, viewer);
    DetectorAction red =
        new DetectorAction(DetectorAction.Kind.OUTPUT, ChannelColor.RED.ordinal() + 1);

    click(menu, viewer, red);
    helper.assertValueEqual(menu.output(), Connection.EDGE, NO_NETWORK);
    menu.broadcastChanges();
    click(menu, viewer, DetectorAction.choose(0));
    click(menu, viewer, red);

    WeatherDetectorBlockEntity block = helper.getBlockEntity(DETECTOR);
    helper.assertValueEqual(
        block.module().flatMap(SignalModule::network), Optional.of(network), NOT_JOINED);
    helper.assertValueEqual(menu.output(), Connection.on(ChannelColor.RED), NOT_JOINED);
    helper.succeed();
  }

  /** Another player sees the screen but changes nothing (answer 378). */
  @GameTest(template = EMPTY)
  public static void aStrangerChangesNothing(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner);
    ServerPlayer stranger = viewerAs(helper, helper.makeMockPlayer(GameType.SURVIVAL));
    DetectorMenu menu = menuOf(helper, stranger);

    click(menu, stranger, DetectorAction.mode(WeatherMode.CLEAR));

    helper.assertFalse(menu.viewerIsOwner(), STRANGER);
    helper.assertValueEqual(menu.mode(), Optional.of(WeatherMode.RAIN), STRANGER);
    helper.succeed();
  }

  /** Under the clear sky of the test server, a detector of the rain shows `Off` (465 and 470). */
  @GameTest(template = EMPTY)
  public static void theMenuShowsTheStateAndTheWeather(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner);
    DetectorMenu menu = menuOf(helper, viewerAs(helper, owner));

    helper.succeedWhen(() -> {
      helper.assertValueEqual(menu.state(), Optional.of(DetectorState.OFF), NOT_SHOWN);
      helper.assertValueEqual(menu.weather(), Optional.of(Weather.CLEAR), NOT_SHOWN);
    });
  }

  private static void place(GameTestHelper helper, Player owner) {
    helper.setBlock(STONE, Blocks.STONE);
    owner.setYHeadRot(LOOK_NORTH);
    GameTestActions.placeOn(
        helper, owner, new ItemStack(RedweaveItems.WEATHER_DETECTOR.get()), STONE, Direction.UP);
  }

  /** A server player with the profile of the mock player, standing by the detector. */
  private static ServerPlayer viewerAs(GameTestHelper helper, Player player) {
    ServerPlayer viewer =
        FakePlayerFactory.get(
            helper.getLevel(), new GameProfile(player.getUUID(), player.getScoreboardName()));
    viewer.setPos(Vec3.atCenterOf(helper.absolutePos(DETECTOR.above())));
    return viewer;
  }

  private static DetectorMenu menuOf(GameTestHelper helper, ServerPlayer viewer) {
    return new DetectorMenu(MENU_ID, viewer, helper.absolutePos(DETECTOR));
  }

  private static void click(DetectorMenu menu, ServerPlayer viewer, DetectorAction action) {
    menu.clickMenuButton(viewer, action.button());
  }
}
