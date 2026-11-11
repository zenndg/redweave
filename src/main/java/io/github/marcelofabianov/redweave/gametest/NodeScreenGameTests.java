package io.github.marcelofabianov.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.SignalNodeBlock;
import io.github.marcelofabianov.redweave.block.SignalNodeBlockEntity;
import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.NodeAction;
import io.github.marcelofabianov.redweave.menu.NodeMenu;
import io.github.marcelofabianov.redweave.menu.NodeScreenCodes;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
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

/** The Node Screen on the server, and what the node shows in the world (telas.md, 86 and 87). */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NodeScreenGameTests {
  private static final String EMPTY = "empty";
  private static final String OWNER_NAME = "owner";
  private static final String STRANGER_NAME = "stranger";
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos NODE = STONE.above();
  private static final int MENU_ID = 1;
  private static final int FIRST_SLOT = 0;
  private static final double FAR_AWAY = 64;
  private static final String OVERWORLD = "minecraft:overworld";
  private static final int FAR_BLOCK = 10_000;
  private static final int SEA_LEVEL = 64;
  private static final BlockLocation BEYOND_THE_RANGE =
      new BlockLocation(OVERWORLD, FAR_BLOCK, SEA_LEVEL, FAR_BLOCK);
  private static final ChannelColor SECOND_CHANNEL = ChannelColor.ORANGE;
  private static final int SECOND_SLOT = SECOND_CHANNEL.ordinal();
  private static final ChannelColor DISABLED_CHANNEL = ChannelColor.WHITE;
  private static final List<ChannelColor> CORNER_CHANNELS =
      List.of(ChannelColor.ORANGE, ChannelColor.MAGENTA, ChannelColor.CYAN, ChannelColor.RED);
  private static final List<ChannelColor> ENABLED_CHANNELS =
      List.of(
          ChannelColor.BLACK,
          ChannelColor.RED,
          ChannelColor.CYAN,
          ChannelColor.MAGENTA,
          ChannelColor.ORANGE,
          DISABLED_CHANNEL);
  private static final String NOT_ENABLED = "Enable should give an input on the face of the node";
  private static final String CHANGED_ENABLED = "An enabled slot should be locked";
  private static final String NOT_CHANGED = "The owner should change the slot";
  private static final String NOT_DISABLED = "Disable should turn the slot off";
  private static final String NOT_KEPT_DISABLED = "A disabled slot should keep direction and face";
  private static final String NOT_KEPT_ENABLED = "Enabling again should bring direction and face";
  private static final String STRANGER_CHANGED = "Only the owner should change the slot";
  private static final String VALID_FAR = "The screen should close beyond the reach";
  private static final String INVALID_NEAR = "The screen should stay open within reach";
  private static final String NO_LIVE_DATA = "The menu should show the slots of the node";
  private static final String NO_RING = "A node out of range should show the ring";
  private static final String WRONG_CORNER = "The corners should show the first enabled channels";
  private static final String CHANNEL_NAME = "Main door";
  private static final String NO_CHANNEL_NAME = "The Node Screen should show the channel name";

  private NodeScreenGameTests() {}

  /** A slot is set up while disabled; enabled, it is locked (answer 237). */
  @GameTest(template = EMPTY)
  public static void theOwnerSetsUpADisabledSlotAndEnablesIt(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeId node = nodeOf(helper, owner);
    NodeMenu menu = menuOf(helper, owner.getUUID(), OWNER_NAME);
    ChannelSlot input = new ChannelSlot(Direction.INPUT, Face.UP, SECOND_CHANNEL);
    ChannelSlot output = new ChannelSlot(Direction.OUTPUT, Face.EAST, SECOND_CHANNEL);

    click(menu, helper, owner.getUUID(), NodeAction.configure(SECOND_SLOT, output));
    helper.assertTrue(
        slot(node, SECOND_SLOT).equals(Optional.of(output.enabled(false))), NOT_CHANGED);
    click(menu, helper, owner.getUUID(), NodeAction.enable(SECOND_SLOT));
    helper.assertTrue(slot(node, SECOND_SLOT).equals(Optional.of(output)), NOT_ENABLED);
    click(menu, helper, owner.getUUID(), NodeAction.configure(SECOND_SLOT, input));
    helper.assertTrue(slot(node, SECOND_SLOT).equals(Optional.of(output)), CHANGED_ENABLED);
    click(menu, helper, owner.getUUID(), NodeAction.disable(SECOND_SLOT));
    helper.assertFalse(slot(node, SECOND_SLOT).orElseThrow().active(), NOT_DISABLED);
    helper.succeed();
  }

  /** A disabled slot keeps its direction and face, and enabling it again brings them (228). */
  @GameTest(template = EMPTY)
  public static void aDisabledSlotKeepsItsDirectionAndFace(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeId node = nodeOf(helper, owner);
    NodeMenu menu = menuOf(helper, owner.getUUID(), OWNER_NAME);
    ChannelSlot output = new ChannelSlot(Direction.OUTPUT, Face.EAST, SECOND_CHANNEL);
    click(menu, helper, owner.getUUID(), NodeAction.configure(SECOND_SLOT, output));
    click(menu, helper, owner.getUUID(), NodeAction.enable(SECOND_SLOT));

    click(menu, helper, owner.getUUID(), NodeAction.disable(SECOND_SLOT));
    helper.assertTrue(
        slot(node, SECOND_SLOT).equals(Optional.of(output.enabled(false))), NOT_KEPT_DISABLED);
    click(menu, helper, owner.getUUID(), NodeAction.enable(SECOND_SLOT));
    helper.assertTrue(slot(node, SECOND_SLOT).equals(Optional.of(output)), NOT_KEPT_ENABLED);
    helper.succeed();
  }

  /** Others see the screen, but the server refuses what they ask (answers 22 and 85). */
  @GameTest(template = EMPTY)
  public static void aStrangerCannotChangeASlot(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeId node = nodeOf(helper, owner);
    UUID stranger = UUID.randomUUID();
    NodeMenu menu = menuOf(helper, stranger, STRANGER_NAME);

    click(menu, helper, stranger, NodeAction.enable(SECOND_SLOT));

    helper.assertTrue(slot(node, SECOND_SLOT).isEmpty(), STRANGER_CHANGED);
    helper.succeed();
  }

  /** The game only delivers a click while the menu is valid: within reach (answer 81). */
  @GameTest(template = EMPTY)
  public static void theScreenOnlyHoldsWithinReach(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    nodeOf(helper, owner);
    ServerPlayer viewer = playerNearTheNode(helper, owner.getUUID(), OWNER_NAME);
    NodeMenu menu = new NodeMenu(MENU_ID, viewer, helper.absolutePos(NODE));

    helper.assertTrue(menu.stillValid(viewer), INVALID_NEAR);
    viewer.setPos(viewer.position().add(FAR_AWAY, 0, FAR_AWAY));
    helper.assertFalse(menu.stillValid(viewer), VALID_FAR);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void theMenuShowsTheSlotsOfTheNode(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeId node = nodeOf(helper, owner);
    NodeScene.enableSlot(owner, node, FIRST_SLOT);
    NodeMenu menu = menuOf(helper, owner.getUUID(), OWNER_NAME);

    helper.assertTrue(menu.slot(FIRST_SLOT).isPresent(), NO_LIVE_DATA);
    helper.assertTrue(menu.slot(FIRST_SLOT).equals(slot(node, FIRST_SLOT)), NO_LIVE_DATA);
    helper.assertTrue(menu.viewerIsOwner(), NO_LIVE_DATA);
    helper.succeed();
  }

  /** A slot shows the name the owner gave its channel on the Controller Screen (221). */
  @GameTest(template = EMPTY)
  public static void theMenuShowsTheNameOfTheChannel(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeId node = nodeOf(helper, owner);
    NodeScene.enableSlot(owner, node, FIRST_SLOT);
    ChannelColor channel = slot(node, FIRST_SLOT).orElseThrow().channel();
    networkOf(node).renameChannel(new PlayerId(owner.getUUID()), channel, CHANNEL_NAME);

    NodeMenu menu = menuOf(helper, owner.getUUID(), OWNER_NAME);

    helper.assertTrue(menu.channelName(channel).equals(CHANNEL_NAME), NO_CHANNEL_NAME);
    helper.succeed();
  }

  /** A node beyond the reach of its controller shows the dashed red ring (answer 86). */
  @GameTest(template = EMPTY)
  public static void aNodeOutOfRangeShowsTheRing(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeId node = nodeOf(helper, owner);
    Network network = networkOf(node);
    PlayerId ownerId = new PlayerId(owner.getUUID());
    network.removeController(ownerId);
    network.placeController(ownerId, BEYOND_THE_RANGE);

    helper.succeedWhen(() -> helper.assertBlockProperty(NODE, SignalNodeBlock.OUT_OF_RANGE, true));
  }

  /**
   * The four corners of the plate show the first four enabled channels, in color order; a disabled
   * slot and the fifth enabled channel are left out (answers 87 and 229).
   */
  @GameTest(template = EMPTY)
  public static void theCornersShowTheFirstFourEnabledChannels(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeId node = nodeOf(helper, owner);
    PlayerId ownerId = new PlayerId(owner.getUUID());
    for (ChannelColor channel : ENABLED_CHANNELS) {
      NodeScene.enableSlot(owner, node, channel.ordinal());
    }
    networkOf(node).disableSlot(ownerId, node, DISABLED_CHANNEL.ordinal());

    helper.succeedWhen(() -> {
      SignalNodeBlockEntity entity = helper.getBlockEntity(NODE);
      for (int corner = 0; corner < CORNER_CHANNELS.size(); corner++) {
        helper.assertTrue(
            entity.corner(corner) == CORNER_CHANNELS.get(corner).ordinal(), WRONG_CORNER);
      }
    });
  }

  private static NodeId nodeOf(GameTestHelper helper, Player owner) {
    GameTestActions.placeOn(
        helper,
        owner,
        new ItemStack(RedweaveItems.NETWORK_CONTROLLER.get()),
        CONTROLLER_FLOOR,
        net.minecraft.core.Direction.UP);
    helper.setBlock(STONE, Blocks.STONE);
    GameTestActions.placeOn(
        helper,
        owner,
        new ItemStack(RedweaveItems.SIGNAL_NODE.get()),
        STONE,
        net.minecraft.core.Direction.UP);
    SignalNodeBlockEntity entity = helper.getBlockEntity(NODE);
    NodeScene.choose(helper, owner, NODE, NodeScene.FIRST_CHOICE);
    return entity.node().orElseThrow();
  }

  private static NodeMenu menuOf(GameTestHelper helper, UUID player, String name) {
    return new NodeMenu(MENU_ID, playerNearTheNode(helper, player, name), helper.absolutePos(NODE));
  }

  /** A server player with that UUID, standing next to the node. */
  private static ServerPlayer playerNearTheNode(GameTestHelper helper, UUID player, String name) {
    ServerPlayer viewer = FakePlayerFactory.get(helper.getLevel(), new GameProfile(player, name));
    viewer.setPos(Vec3.atCenterOf(helper.absolutePos(NODE.above())));
    return viewer;
  }

  private static void click(NodeMenu menu, GameTestHelper helper, UUID player, NodeAction action) {
    menu.clickMenuButton(
        playerNearTheNode(helper, player, OWNER_NAME), NodeScreenCodes.button(action));
  }

  private static Optional<ChannelSlot> slot(NodeId node, int index) {
    return networkOf(node).node(node).orElseThrow().slots().get(index);
  }

  private static Network networkOf(NodeId node) {
    return RedweaveServer.running().orElseThrow().networkOfNode(node).orElseThrow();
  }
}
