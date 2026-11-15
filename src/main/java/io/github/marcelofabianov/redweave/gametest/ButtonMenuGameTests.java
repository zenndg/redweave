package io.github.marcelofabianov.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.part.ButtonPart;
import io.github.marcelofabianov.redweave.block.part.NodePart;
import io.github.marcelofabianov.redweave.domain.module.ButtonTime;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.item.ConfiguratorMode;
import io.github.marcelofabianov.redweave.menu.ButtonAction;
import io.github.marcelofabianov.redweave.menu.ButtonMenu;
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
 * The Signal Button Screen and its network on the server: the strength, the on time, the output on
 * a channel and the `Toggle` with the network `Offline` (answers 378, 552, 556, 578, 588 and 593).
 * It opens no screen.
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ButtonMenuGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos BUTTON = STONE.above();
  private static final BlockPos LAMP_IN_FRONT = BUTTON.north();
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(2, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos NODE_LAMP = new BlockPos(0, 1, 2);
  private static final ChannelColor CHANNEL = ChannelColor.RED;
  private static final int MENU_ID = 1;
  private static final int THIRTEEN = 13;
  private static final int TWO_SECONDS = 40;
  private static final int A_TENTH_LESS = 38;
  private static final String NOT_WEAKER = "The owner should step the strength down";
  private static final String NOT_LONGER = "The owner should step the on time";
  private static final String STRANGER = "Another player should change nothing";
  private static final String NOT_ON = "The Toggle should press the button, even offline";
  private static final String NOT_JOINED = "The owner should pick a network and a channel";

  private ButtonMenuGameTests() {}

  /** The owner steps the strength and the on time; Shift + `+` adds 1 s (552, 556 and 398). */
  @GameTest(template = EMPTY)
  public static void theOwnerStepsTheStrengthAndTheOnTime(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    ButtonScene.place(helper, owner, STONE);
    ServerPlayer viewer = viewerAs(helper, owner);
    ButtonMenu menu = menuOf(helper, viewer);

    click(menu, viewer, ButtonAction.strength(false));
    click(menu, viewer, ButtonAction.strength(false));
    click(menu, viewer, ButtonAction.time(true, true));

    helper.assertValueEqual(menu.strength(), THIRTEEN, NOT_WEAKER);
    helper.assertValueEqual(menu.timeTicks(), TWO_SECONDS, NOT_LONGER);
    click(menu, viewer, ButtonAction.time(false, false));
    helper.assertValueEqual(menu.timeTicks(), A_TENTH_LESS, NOT_LONGER);
    helper.succeed();
  }

  /** Another player sees the screen but changes nothing (answer 378). */
  @GameTest(template = EMPTY)
  public static void aStrangerChangesNothing(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    ButtonScene.place(helper, owner, STONE);
    ServerPlayer stranger = viewerAs(helper, helper.makeMockPlayer(GameType.SURVIVAL));
    ButtonMenu menu = menuOf(helper, stranger);

    click(menu, stranger, ButtonAction.strength(false));
    click(menu, stranger, ButtonAction.time(true, true));
    click(menu, stranger, ButtonAction.output(Optional.of(CHANNEL.ordinal())));

    helper.assertFalse(menu.viewerIsOwner(), STRANGER);
    helper.assertValueEqual(menu.strength(), Signal.MAX.strength(), STRANGER);
    helper.assertValueEqual(menu.timeTicks(), ButtonTime.PLACED_TICKS, STRANGER);
    helper.assertValueEqual(menu.output(), Connection.EDGE, STRANGER);
    helper.succeed();
  }

  /**
   * On a channel, a node of the network gives the button to its lamp, and the block in front gets
   * nothing (answers 548 and 549).
   */
  @GameTest(template = EMPTY)
  public static void aButtonOnAChannelReachesANode(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    outputNodeOnALamp(helper, owner);
    ButtonPart button = ButtonScene.place(helper, owner, STONE);
    helper.setBlock(LAMP_IN_FRONT, Blocks.REDSTONE_LAMP);
    onChannel(helper, owner);

    LeverScene.click(helper, owner, ItemStack.EMPTY, BUTTON);

    helper.assertValueEqual(button.output(), Connection.on(CHANNEL), NOT_JOINED);
    helper.succeedWhen(() -> {
      helper.assertBlockProperty(NODE_LAMP, RedstoneLampBlock.LIT, true);
      helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, false);
    });
  }

  /** With the network `Offline`, the `Toggle` still presses the button (578, 588 and 593). */
  @GameTest(template = EMPTY)
  public static void theTogglePressesTheButtonWithTheNetworkOffline(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    ButtonPart button = ButtonScene.place(helper, owner, STONE);
    onChannel(helper, owner);
    RedweaveServer.running()
        .orElseThrow()
        .network(network)
        .orElseThrow()
        .terminal()
        .turnOff(new PlayerId(owner.getUUID()));

    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.TOGGLE);
    NodeScene.click(helper, owner, BUTTON);

    helper.assertTrue(button.button().on(), NOT_ON);
    helper.succeed();
  }

  /** The owner picks the network on the screen, then the channel for the output (414). */
  private static void onChannel(GameTestHelper helper, Player owner) {
    ServerPlayer viewer = viewerAs(helper, owner);
    ButtonMenu menu = menuOf(helper, viewer);
    menu.broadcastChanges();
    click(menu, viewer, ButtonAction.choose(0));
    click(menu, viewer, ButtonAction.output(Optional.of(CHANNEL.ordinal())));
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

  /** A server player with the profile of the player, standing by the button. */
  private static ServerPlayer viewerAs(GameTestHelper helper, Player player) {
    ServerPlayer viewer =
        FakePlayerFactory.get(
            helper.getLevel(), new GameProfile(player.getUUID(), player.getScoreboardName()));
    viewer.setPos(Vec3.atCenterOf(helper.absolutePos(BUTTON.above())));
    return viewer;
  }

  private static ButtonMenu menuOf(GameTestHelper helper, ServerPlayer viewer) {
    return new ButtonMenu(MENU_ID, viewer, ModuleParts.spotOf(helper, BUTTON));
  }

  private static void click(ButtonMenu menu, ServerPlayer viewer, ButtonAction action) {
    menu.clickMenuButton(viewer, action.button());
  }
}
