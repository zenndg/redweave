package io.github.marcelofabianov.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.part.InverterPart;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.InverterState;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.menu.InverterAction;
import io.github.marcelofabianov.redweave.menu.InverterMenu;
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
 * The menu of the Signal Inverter Screen on the server: what each button does and who may press it
 * (telas.md, Tela do Signal Inverter, and answers 378, 414 and 451). It opens no screen.
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class InverterMenuGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos INVERTER = STONE.above();
  private static final BlockPos BEHIND = INVERTER.south();
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final float LOOK_NORTH = 180;
  private static final int MENU_ID = 1;
  private static final int TWO_SECONDS = 2;
  private static final int NO_DELAY = 0;
  private static final String NO_NETWORK = "A channel needs the network of the module";
  private static final String NOT_JOINED = "The owner should pick a network and a channel";
  private static final String NOT_BACK = "The owner should move the output back to its edge";
  private static final String STRANGER = "Another player should change nothing";
  private static final String NOT_STEPPED = "The owner should step the strength and the delay";
  private static final String NOT_SHOWN = "The menu should show the state and the input";

  private InverterMenuGameTests() {}

  /** A channel waits for a network; once picked, the input takes the channel (414). */
  @GameTest(template = EMPTY)
  public static void aChannelNeedsTheNetworkPickedFirst(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    place(helper, owner);
    ServerPlayer viewer = viewerAs(helper, owner);
    InverterMenu menu = menuOf(helper, viewer);
    InverterAction red =
        new InverterAction(InverterAction.Kind.INPUT, ChannelColor.RED.ordinal() + 1);

    click(menu, viewer, red);
    helper.assertValueEqual(menu.input(), Connection.EDGE, NO_NETWORK);
    menu.broadcastChanges();
    click(menu, viewer, InverterAction.choose(0));
    click(menu, viewer, red);

    InverterPart block = ModuleParts.only(helper, INVERTER, InverterPart.class);
    helper.assertValueEqual(
        block.module().flatMap(SignalModule::network), Optional.of(network), NOT_JOINED);
    helper.assertValueEqual(menu.input(), Connection.on(ChannelColor.RED), NOT_JOINED);
    helper.succeed();
  }

  /** The owner puts the output on a channel and back on its edge (answers 441 and 451). */
  @GameTest(template = EMPTY)
  public static void theOwnerMovesTheOutputBackToItsEdge(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    place(helper, owner);
    ServerPlayer viewer = viewerAs(helper, owner);
    InverterMenu menu = menuOf(helper, viewer);
    menu.broadcastChanges();
    click(menu, viewer, InverterAction.choose(0));

    click(
        menu,
        viewer,
        new InverterAction(InverterAction.Kind.OUTPUT, ChannelColor.BLUE.ordinal() + 1));
    helper.assertValueEqual(menu.output(), Connection.on(ChannelColor.BLUE), NOT_JOINED);
    click(menu, viewer, new InverterAction(InverterAction.Kind.OUTPUT, InverterAction.EDGE));

    helper.assertValueEqual(menu.output(), Connection.EDGE, NOT_BACK);
    helper.succeed();
  }

  /** The owner steps the strength and the delay from the screen (answers 830 and 834). */
  @GameTest(template = EMPTY)
  public static void theOwnerStepsTheStrengthAndTheDelay(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner);
    ServerPlayer viewer = viewerAs(helper, owner);
    InverterMenu menu = menuOf(helper, viewer);

    click(menu, viewer, InverterAction.strength(false));
    click(menu, viewer, InverterAction.delay(true));
    click(menu, viewer, InverterAction.delay(true));

    helper.assertValueEqual(menu.strength(), Signal.MAX.strength() - 1, NOT_STEPPED);
    helper.assertValueEqual(menu.delaySeconds(), TWO_SECONDS, NOT_STEPPED);
    helper.succeed();
  }

  /** Another player sees the screen but changes nothing (answer 378). */
  @GameTest(template = EMPTY)
  public static void aStrangerChangesNothing(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    place(helper, owner);
    ServerPlayer stranger = viewerAs(helper, helper.makeMockPlayer(GameType.SURVIVAL));
    InverterMenu menu = menuOf(helper, stranger);

    menu.broadcastChanges();
    click(menu, stranger, InverterAction.choose(0));
    click(menu, stranger, new InverterAction(InverterAction.Kind.OUTPUT, 1));
    click(menu, stranger, InverterAction.strength(false));
    click(menu, stranger, InverterAction.delay(true));

    InverterPart block = ModuleParts.only(helper, INVERTER, InverterPart.class);
    helper.assertFalse(menu.viewerIsOwner(), STRANGER);
    helper.assertValueEqual(menu.output(), Connection.EDGE, STRANGER);
    helper.assertValueEqual(menu.strength(), Signal.MAX.strength(), STRANGER);
    helper.assertValueEqual(menu.delaySeconds(), NO_DELAY, STRANGER);
    helper.assertValueEqual(
        block.module().flatMap(SignalModule::network), Optional.empty(), STRANGER);
    helper.succeed();
  }

  /** The menu shows B off and A on with a signal behind, for the diagram (answers 448, 451). */
  @GameTest(template = EMPTY)
  public static void theMenuShowsTheStateAndTheInput(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner);
    helper.setBlock(BEHIND, Blocks.REDSTONE_BLOCK);
    InverterMenu menu = menuOf(helper, viewerAs(helper, owner));

    helper.succeedWhen(() -> {
      helper.assertValueEqual(menu.state(), Optional.of(InverterState.OFF), NOT_SHOWN);
      helper.assertTrue(menu.inputOn(), NOT_SHOWN);
    });
  }

  private static void place(GameTestHelper helper, Player owner) {
    helper.setBlock(STONE, Blocks.STONE);
    owner.setYHeadRot(LOOK_NORTH);
    GameTestActions.placeOn(
        helper, owner, new ItemStack(RedweaveItems.SIGNAL_INVERTER.get()), STONE, Direction.UP);
  }

  /** A server player with the profile of the mock player, standing by the inverter. */
  private static ServerPlayer viewerAs(GameTestHelper helper, Player player) {
    ServerPlayer viewer =
        FakePlayerFactory.get(
            helper.getLevel(), new GameProfile(player.getUUID(), player.getScoreboardName()));
    viewer.setPos(Vec3.atCenterOf(helper.absolutePos(INVERTER.above())));
    return viewer;
  }

  private static InverterMenu menuOf(GameTestHelper helper, ServerPlayer viewer) {
    return new InverterMenu(MENU_ID, viewer, ModuleParts.spotOf(helper, INVERTER));
  }

  private static void click(InverterMenu menu, ServerPlayer viewer, InverterAction action) {
    menu.clickMenuButton(viewer, action.button());
  }
}
