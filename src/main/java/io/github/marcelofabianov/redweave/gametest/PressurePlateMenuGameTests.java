package io.github.marcelofabianov.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.part.NodePart;
import io.github.marcelofabianov.redweave.block.part.PressurePlatePart;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.PlateMode;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.item.ConfiguratorMode;
import io.github.marcelofabianov.redweave.menu.PressurePlateAction;
import io.github.marcelofabianov.redweave.menu.PressurePlateMenu;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
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
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Signal Pressure Plate Screen and its network on the server: the strength, the mode, the
 * output on a channel and the `Toggle` cycling the modes with the network `Offline` (answers 378,
 * 552, 603, 606 and 621). It opens no screen.
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class PressurePlateMenuGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos PLATE = STONE.above();
  private static final BlockPos LAMP_IN_FRONT = PLATE.north();
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(2, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos NODE_LAMP = new BlockPos(0, 1, 2);
  private static final ChannelColor CHANNEL = ChannelColor.RED;
  private static final int MENU_ID = 1;
  private static final int THIRTEEN = 13;
  private static final String NOT_WEAKER = "The owner should step the strength down";
  private static final String NOT_PICKED = "The owner should pick the mode";
  private static final String STRANGER = "Another player should change nothing";
  private static final String NOT_CYCLED = "The Toggle should cycle the modes, even offline";
  private static final String NOT_JOINED = "The owner should pick a network and a channel";

  private PressurePlateMenuGameTests() {}

  /** The owner steps the strength and picks a mode (answers 552 and 621). */
  @GameTest(template = EMPTY)
  public static void theOwnerStepsTheStrengthAndPicksTheMode(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    PressurePlateScene.place(helper, owner, STONE);
    ServerPlayer viewer = viewerAs(helper, owner);
    PressurePlateMenu menu = menuOf(helper, viewer);

    click(menu, viewer, PressurePlateAction.step(false));
    click(menu, viewer, PressurePlateAction.step(false));
    click(menu, viewer, PressurePlateAction.mode(PlateMode.HOSTILE));

    helper.assertValueEqual(menu.strength(), THIRTEEN, NOT_WEAKER);
    helper.assertValueEqual(menu.mode(), Optional.of(PlateMode.HOSTILE), NOT_PICKED);
    helper.succeed();
  }

  /** Another player sees the screen but changes nothing (answer 378). */
  @GameTest(template = EMPTY)
  public static void aStrangerChangesNothing(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    PressurePlateScene.place(helper, owner, STONE);
    ServerPlayer stranger = viewerAs(helper, helper.makeMockPlayer(GameType.SURVIVAL));
    PressurePlateMenu menu = menuOf(helper, stranger);

    click(menu, stranger, PressurePlateAction.step(false));
    click(menu, stranger, PressurePlateAction.mode(PlateMode.ITEMS));
    click(menu, stranger, PressurePlateAction.output(Optional.of(CHANNEL.ordinal())));

    helper.assertFalse(menu.viewerIsOwner(), STRANGER);
    helper.assertValueEqual(menu.strength(), Signal.MAX.strength(), STRANGER);
    helper.assertValueEqual(menu.mode(), Optional.of(PlateMode.PLAYERS), STRANGER);
    helper.assertValueEqual(menu.output(), Connection.EDGE, STRANGER);
    helper.succeed();
  }

  /**
   * On a channel, a node of the network gives the plate to its lamp, and the block in front gets
   * nothing (answers 548 and 549).
   */
  @GameTest(template = EMPTY)
  public static void aPlateOnAChannelReachesANode(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    outputNodeOnALamp(helper, owner);
    PressurePlatePart plate = PressurePlateScene.place(helper, owner, STONE);
    helper.setBlock(LAMP_IN_FRONT, Blocks.REDSTONE_LAMP);
    onChannel(helper, owner);

    PressurePlateScene.stepOn(helper, owner, PLATE);

    helper.assertValueEqual(plate.output(), Connection.on(CHANNEL), NOT_JOINED);
    helper.succeedWhen(() -> {
      helper.assertBlockProperty(NODE_LAMP, RedstoneLampBlock.LIT, true);
      helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, false);
    });
  }

  /** With the network `Offline`, the `Toggle` still cycles the modes (603, 606 and 621). */
  @GameTest(template = EMPTY)
  public static void theToggleCyclesTheModesWithTheNetworkOffline(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    PressurePlatePart plate = PressurePlateScene.place(helper, owner, STONE);
    onChannel(helper, owner);
    RedweaveServer.running()
        .orElseThrow()
        .network(network)
        .orElseThrow()
        .terminal()
        .turnOff(new PlayerId(owner.getUUID()));

    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.TOGGLE);
    NodeScene.click(helper, owner, PLATE);
    helper.assertValueEqual(plate.plate().mode(), PlateMode.PASSIVE, NOT_CYCLED);
    NodeScene.click(helper, owner, PLATE);
    NodeScene.click(helper, owner, PLATE);
    helper.assertValueEqual(plate.plate().mode(), PlateMode.ITEMS, NOT_CYCLED);
    NodeScene.click(helper, owner, PLATE);
    helper.assertValueEqual(plate.plate().mode(), PlateMode.PLAYERS, NOT_CYCLED);
    helper.succeed();
  }

  /** The owner picks the network on the screen, then the channel for the output (414). */
  private static void onChannel(GameTestHelper helper, Player owner) {
    ServerPlayer viewer = viewerAs(helper, owner);
    PressurePlateMenu menu = menuOf(helper, viewer);
    menu.broadcastChanges();
    click(menu, viewer, PressurePlateAction.choose(0));
    click(menu, viewer, PressurePlateAction.output(Optional.of(CHANNEL.ordinal())));
  }

  /** A node on top of a lamp, in the network, which feeds the lamp from the channel (71). */
  private static void outputNodeOnALamp(GameTestHelper helper, Player owner) {
    helper.setBlock(NODE_LAMP, Blocks.REDSTONE_LAMP);
    GameTestActions.placeOn(
        helper, owner, new ItemStack(RedweaveItems.SIGNAL_NODE.get()), NODE_LAMP, Direction.UP);
    NodeScene.choose(helper, owner, NODE_LAMP.above(), NodeScene.FIRST_CHOICE);
    NodePart node = NodeParts.only(helper, NODE_LAMP.above());
    NodeId id = node.node().orElseThrow();
    NodeScene.networkOf(id)
        .configureSlot(
            new PlayerId(owner.getUUID()),
            id,
            CHANNEL.ordinal(),
            new ChannelSlot(
                io.github.marcelofabianov.redweave.domain.network.Direction.OUTPUT,
                Face.UP,
                CHANNEL));
  }

  /** A server player with the profile of the player, standing by the plate. */
  private static ServerPlayer viewerAs(GameTestHelper helper, Player player) {
    ServerPlayer viewer =
        FakePlayerFactory.get(
            helper.getLevel(), new GameProfile(player.getUUID(), player.getScoreboardName()));
    viewer.setPos(Vec3.atCenterOf(helper.absolutePos(PLATE.above())));
    return viewer;
  }

  private static PressurePlateMenu menuOf(GameTestHelper helper, ServerPlayer viewer) {
    return new PressurePlateMenu(MENU_ID, viewer, ModuleParts.spotOf(helper, PLATE));
  }

  private static void click(
      PressurePlateMenu menu, ServerPlayer viewer, PressurePlateAction action) {
    menu.clickMenuButton(viewer, action.button());
  }
}
