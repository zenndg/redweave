package io.github.marcelofabianov.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.part.ItemSensorPart;
import io.github.marcelofabianov.redweave.block.part.NodePart;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.menu.ItemSensorAction;
import io.github.marcelofabianov.redweave.menu.ItemSensorMenu;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Signal Item Sensor Screen and its network on the server: the ghost slot taking the item on
 * the cursor without consuming it, the count by one and by ten, the strength, the output on a
 * channel, and a stranger who changes nothing (answers 378 and 696). It opens no screen.
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ItemSensorMenuGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos BOX = new BlockPos(1, 1, 1);
  private static final BlockPos SENSOR = BOX.above();
  private static final BlockPos LAMP_IN_FRONT = SENSOR.north();
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(2, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos NODE_LAMP = new BlockPos(0, 1, 2);
  private static final ChannelColor CHANNEL = ChannelColor.RED;
  private static final int MENU_ID = 1;
  private static final int TWELVE = 12;
  private static final int THREE = 3;
  private static final String NOT_PICKED = "The ghost slot should take the item on the cursor";
  private static final String CONSUMED = "The item on the cursor should stay there";
  private static final String NOT_CLEARED = "An empty cursor should clear the item";
  private static final String NOT_STEPPED = "The owner should step the count by one and by ten";
  private static final String NOT_WEAKER = "The owner should step the strength down";
  private static final String STRANGER = "Another player should change nothing";
  private static final String NOT_JOINED = "The owner should pick a network and a channel";

  private ItemSensorMenuGameTests() {}

  /** The ghost slot takes the item on the cursor and leaves it there; empty, it clears (696). */
  @GameTest(template = EMPTY)
  public static void theGhostSlotTakesTheItemOnTheCursor(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    ItemSensorScene.place(helper, owner, BOX, Blocks.CHEST);
    ServerPlayer viewer = viewerAs(helper, owner);
    ItemSensorMenu menu = menuOf(helper, viewer);

    menu.setCarried(new ItemStack(Items.DIAMOND));
    click(menu, viewer, ItemSensorAction.ghost());
    helper.assertValueEqual(menu.item(), Optional.of(Items.DIAMOND), NOT_PICKED);
    helper.assertTrue(menu.getCarried().is(Items.DIAMOND), CONSUMED);

    menu.setCarried(ItemStack.EMPTY);
    click(menu, viewer, ItemSensorAction.ghost());
    helper.assertValueEqual(menu.item(), Optional.empty(), NOT_CLEARED);
    helper.succeed();
  }

  /** The count steps by one, and by ten with Shift; the strength steps by one (696). */
  @GameTest(template = EMPTY)
  public static void theOwnerStepsTheCountAndTheStrength(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    ItemSensorScene.place(helper, owner, BOX, Blocks.CHEST);
    ServerPlayer viewer = viewerAs(helper, owner);
    ItemSensorMenu menu = menuOf(helper, viewer);

    click(menu, viewer, ItemSensorAction.count(true, true));
    click(menu, viewer, ItemSensorAction.count(true, false));
    click(menu, viewer, ItemSensorAction.count(true, false));
    click(menu, viewer, ItemSensorAction.count(true, true));
    click(menu, viewer, ItemSensorAction.count(false, true));
    click(menu, viewer, ItemSensorAction.strength(false));
    click(menu, viewer, ItemSensorAction.strength(false));
    click(menu, viewer, ItemSensorAction.strength(false));

    helper.assertValueEqual(menu.count(), TWELVE + 1, NOT_STEPPED);
    helper.assertValueEqual(menu.strength(), TWELVE, NOT_WEAKER);
    helper.succeed();
  }

  /** Another player sees the screen but changes nothing (answer 378). */
  @GameTest(template = EMPTY)
  public static void aStrangerChangesNothing(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    ItemSensorScene.place(helper, owner, BOX, Blocks.CHEST);
    ServerPlayer stranger = viewerAs(helper, helper.makeMockPlayer(GameType.SURVIVAL));
    ItemSensorMenu menu = menuOf(helper, stranger);

    menu.setCarried(new ItemStack(Items.DIAMOND));
    click(menu, stranger, ItemSensorAction.ghost());
    click(menu, stranger, ItemSensorAction.count(true, true));
    click(menu, stranger, ItemSensorAction.strength(false));
    click(menu, stranger, ItemSensorAction.output(Optional.of(CHANNEL.ordinal())));

    helper.assertFalse(menu.viewerIsOwner(), STRANGER);
    helper.assertValueEqual(menu.item(), Optional.empty(), STRANGER);
    helper.assertValueEqual(menu.count(), 1, STRANGER);
    helper.assertValueEqual(menu.strength(), Signal.MAX.strength(), STRANGER);
    helper.assertValueEqual(menu.output(), Connection.EDGE, STRANGER);
    helper.succeed();
  }

  /**
   * On a channel, a node of the network gives the sensor to its lamp, and the block in front gets
   * nothing (answer 696).
   */
  @GameTest(template = EMPTY)
  public static void aSensorOnAChannelReachesANode(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    outputNodeOnALamp(helper, owner);
    ItemSensorPart sensor = ItemSensorScene.place(helper, owner, BOX, Blocks.CHEST);
    helper.setBlock(LAMP_IN_FRONT, Blocks.REDSTONE_LAMP);
    onChannel(helper, owner);
    ItemSensorScene.expect(sensor, Items.DIAMOND, THREE);
    ItemSensorScene.put(helper, BOX, 0, Items.DIAMOND, THREE);

    helper.assertValueEqual(sensor.output(), Connection.on(CHANNEL), NOT_JOINED);
    helper.succeedWhen(() -> {
      helper.assertBlockProperty(NODE_LAMP, RedstoneLampBlock.LIT, true);
      helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, false);
    });
  }

  /** The owner picks the network on the screen, then the channel for the output (414). */
  private static void onChannel(GameTestHelper helper, Player owner) {
    ServerPlayer viewer = viewerAs(helper, owner);
    ItemSensorMenu menu = menuOf(helper, viewer);
    menu.broadcastChanges();
    click(menu, viewer, ItemSensorAction.choose(0));
    click(menu, viewer, ItemSensorAction.output(Optional.of(CHANNEL.ordinal())));
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

  /** A server player with the profile of the player, standing by the sensor. */
  private static ServerPlayer viewerAs(GameTestHelper helper, Player player) {
    ServerPlayer viewer =
        FakePlayerFactory.get(
            helper.getLevel(), new GameProfile(player.getUUID(), player.getScoreboardName()));
    viewer.setPos(Vec3.atCenterOf(helper.absolutePos(SENSOR.above())));
    return viewer;
  }

  private static ItemSensorMenu menuOf(GameTestHelper helper, ServerPlayer viewer) {
    return new ItemSensorMenu(
        MENU_ID, viewer.getInventory(), viewer, ModuleParts.spotOf(helper, SENSOR));
  }

  private static void click(ItemSensorMenu menu, ServerPlayer viewer, ItemSensorAction action) {
    menu.clickMenuButton(viewer, action.button());
  }
}
