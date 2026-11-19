package io.github.zenndg.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.part.LockPart;
import io.github.zenndg.redweave.block.part.NodePart;
import io.github.zenndg.redweave.domain.module.Connection;
import io.github.zenndg.redweave.domain.module.LockPulse;
import io.github.zenndg.redweave.domain.network.ChannelSlot;
import io.github.zenndg.redweave.domain.network.Face;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.domain.signal.Signal;
import io.github.zenndg.redweave.item.ConfiguratorMode;
import io.github.zenndg.redweave.menu.LockAction;
import io.github.zenndg.redweave.menu.LockMenu;
import io.github.zenndg.redweave.registry.RedweaveItems;
import io.github.zenndg.redweave.server.RedweaveServer;
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
 * The Signal Lock Screen and its network on the server: the key only for its owner, the steps of
 * the key, the strength and the pulse, and its ends on channels (answers 378, 635 to 637 and 640).
 * It opens no screen.
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class LockMenuGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos LOCK = STONE.above();
  private static final BlockPos LAMP_IN_FRONT = LOCK.north();
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(2, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos NODE_LAMP = new BlockPos(0, 1, 2);
  private static final ChannelColor CHANNEL = ChannelColor.RED;
  private static final ChannelColor INPUT_CHANNEL = ChannelColor.BLUE;
  private static final int MENU_ID = 1;
  private static final int FOURTEEN = 14;
  private static final int THIRTEEN = 13;
  private static final int TWO_SECONDS = 40;
  private static final String NOT_STEPPED = "The owner should step the key, strength and pulse";
  private static final String KEY_SHOWN = "Another player should never get the key";
  private static final String STRANGER = "Another player should change nothing";
  private static final String NOT_JOINED = "The owner should put both ends on channels";
  private static final String NOT_OPENED = "The Toggle should open the lock with the network off";

  private LockMenuGameTests() {}

  /** The owner sees the key and steps it, the strength and the pulse (636, 640 and 398). */
  @GameTest(template = EMPTY)
  public static void theOwnerStepsTheKeyTheStrengthAndThePulse(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    LockScene.place(helper, owner, STONE);
    ServerPlayer viewer = viewerAs(helper, owner);
    LockMenu menu = menuOf(helper, viewer);

    click(menu, viewer, LockAction.key(false));
    click(menu, viewer, LockAction.strength(false));
    click(menu, viewer, LockAction.strength(false));
    click(menu, viewer, LockAction.pulse(true, true));

    helper.assertValueEqual(menu.key(), Optional.of(FOURTEEN), NOT_STEPPED);
    helper.assertValueEqual(menu.strength(), THIRTEEN, NOT_STEPPED);
    helper.assertValueEqual(menu.pulseTicks(), TWO_SECONDS, NOT_STEPPED);
    helper.succeed();
  }

  /** Another player gets no key and changes nothing (answers 378 and 637). */
  @GameTest(template = EMPTY)
  public static void aStrangerGetsNoKeyAndChangesNothing(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    LockPart lock = LockScene.place(helper, owner, STONE);
    ServerPlayer stranger = viewerAs(helper, helper.makeMockPlayer(GameType.SURVIVAL));
    LockMenu menu = menuOf(helper, stranger);

    click(menu, stranger, LockAction.key(false));
    click(menu, stranger, LockAction.strength(false));
    click(menu, stranger, LockAction.pulse(true, true));
    click(menu, stranger, LockAction.connect(LockAction.Kind.OUTPUT, Optional.of(0)));

    helper.assertFalse(menu.viewerIsOwner(), STRANGER);
    helper.assertValueEqual(menu.key(), Optional.empty(), KEY_SHOWN);
    helper.assertValueEqual(lock.lock().key(), Signal.MAX, STRANGER);
    helper.assertValueEqual(lock.lock().strength(), Signal.MAX, STRANGER);
    helper.assertValueEqual(lock.lock().pulse(), new LockPulse(LockPulse.PLACED_TICKS), STRANGER);
    helper.assertValueEqual(lock.output(), Connection.EDGE, STRANGER);
    helper.succeed();
  }

  /**
   * Its input and its output go on channels; the `Toggle` opens it, and a node of the network gives
   * it to its lamp while the block in front gets nothing (answers 635 and 640).
   */
  @GameTest(template = EMPTY)
  public static void bothEndsGoOnChannels(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    outputNodeOnALamp(helper, owner);
    LockPart lock = LockScene.place(helper, owner, STONE);
    helper.setBlock(LAMP_IN_FRONT, Blocks.REDSTONE_LAMP);
    onChannels(helper, owner);

    lock.toggle();

    helper.assertValueEqual(lock.input(), Connection.on(INPUT_CHANNEL), NOT_JOINED);
    helper.assertValueEqual(lock.output(), Connection.on(CHANNEL), NOT_JOINED);
    helper.succeedWhen(() -> {
      helper.assertBlockProperty(NODE_LAMP, RedstoneLampBlock.LIT, true);
      helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, false);
    });
  }

  /** The `Toggle` of the owner opens the lock even with its network off (answer 707). */
  @GameTest(template = EMPTY)
  public static void theToggleOpensItWithTheNetworkOffline(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    LockPart lock = LockScene.place(helper, owner, STONE);
    onChannels(helper, owner);
    RedweaveServer.running()
        .orElseThrow()
        .network(network)
        .orElseThrow()
        .terminal()
        .turnOff(new PlayerId(owner.getUUID()));

    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.TOGGLE);
    NodeScene.click(helper, owner, LOCK);

    helper.assertTrue(lock.lock().remaining() > 0, NOT_OPENED);
    helper.succeed();
  }

  /** The owner picks the network on the screen, then a channel for each end (answer 414). */
  private static void onChannels(GameTestHelper helper, Player owner) {
    ServerPlayer viewer = viewerAs(helper, owner);
    LockMenu menu = menuOf(helper, viewer);
    menu.broadcastChanges();
    click(menu, viewer, LockAction.choose(0));
    click(
        menu,
        viewer,
        LockAction.connect(LockAction.Kind.INPUT, Optional.of(INPUT_CHANNEL.ordinal())));
    click(menu, viewer, LockAction.connect(LockAction.Kind.OUTPUT, Optional.of(CHANNEL.ordinal())));
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
                io.github.zenndg.redweave.domain.network.Direction.OUTPUT, Face.UP, CHANNEL));
  }

  /** A server player with the profile of the player, standing by the lock. */
  private static ServerPlayer viewerAs(GameTestHelper helper, Player player) {
    ServerPlayer viewer =
        FakePlayerFactory.get(
            helper.getLevel(), new GameProfile(player.getUUID(), player.getScoreboardName()));
    viewer.setPos(Vec3.atCenterOf(helper.absolutePos(LOCK.above())));
    return viewer;
  }

  private static LockMenu menuOf(GameTestHelper helper, ServerPlayer viewer) {
    return new LockMenu(MENU_ID, viewer, ModuleParts.spotOf(helper, LOCK));
  }

  private static void click(LockMenu menu, ServerPlayer viewer, LockAction action) {
    menu.clickMenuButton(viewer, action.button());
  }
}
