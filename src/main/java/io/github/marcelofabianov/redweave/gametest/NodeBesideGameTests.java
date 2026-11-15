package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.part.CableReach;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * A Signal Node and what lies beside its space, on the same side: a Signal Cable or the game wire
 * there take what it emits and feed it as its block of support, and the cable reaches its plate; a
 * lamp beside it still gets nothing (answers 262, 574 and 575).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NodeBesideGameTests {
  private static final String EMPTY = "empty";
  private static final int FULL = 15;
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos SPACE = new BlockPos(1, 1, 1);
  private static final BlockPos FLOOR = SPACE.below();
  private static final BlockPos BESIDE = SPACE.east();
  private static final BlockPos BESIDE_FLOOR = BESIDE.below();
  private static final BlockPos BESIDE_SOURCE = BESIDE.north();
  private static final BlockPos LAMP = SPACE.south();
  private static final BlockPos READER_STONE = new BlockPos(0, 0, 0);
  private static final ChannelColor CHANNEL = ChannelColor.WHITE;
  private static final String NOT_READ = "The node should read what is beside it, on its side";
  private static final String NOT_PLATE = "The cable should reach the plate of the node";
  private static final String NOT_TURNED = "The game wire should turn to the node";

  private NodeBesideGameTests() {}

  /** A powered cable beside the space, on the floor, feeds a floor node as its block (574). */
  @GameTest(template = EMPTY)
  public static void aNodeReadsTheCableBesideIt(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeId node = NodeScene.placeNode(helper, owner, FLOOR, Direction.UP);
    NodeScene.enableSlot(owner, node, CHANNEL.ordinal());
    helper.setBlock(BESIDE_FLOOR, Blocks.REDSTONE_BLOCK);
    CableScene.place(helper, owner, BESIDE_FLOOR, Direction.UP, ChannelColor.RED);

    helper.succeedWhen(() -> {
      CableScene.assertFloor(helper, BESIDE, FULL);
      helper.assertValueEqual(
          NodeScene.networkOf(node).channelValue(CHANNEL), Signal.MAX, NOT_READ);
      helper.assertValueEqual(
          Optional.ofNullable(
              CableScene.at(helper, BESIDE, Direction.DOWN).arms().get(Direction.WEST)),
          Optional.of(CableReach.PLATE),
          NOT_PLATE);
    });
  }

  /** The cable beside a floor node takes what it emits; a lamp beside it stays off (262, 574). */
  @GameTest(template = EMPTY)
  public static void aNodeFeedsTheCableBesideItButNotALamp(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    emittingNode(helper, owner);
    helper.setBlock(BESIDE_FLOOR, Blocks.STONE);
    CableScene.place(helper, owner, BESIDE_FLOOR, Direction.UP, ChannelColor.RED);
    helper.setBlock(LAMP, Blocks.REDSTONE_LAMP);

    helper.succeedWhen(() -> {
      CableScene.assertFloor(helper, BESIDE, FULL);
      helper.assertBlockProperty(LAMP, RedstoneLampBlock.LIT, false);
    });
  }

  /** The game wire beside a floor node turns to it and feeds it as its block (answer 574). */
  @GameTest(template = EMPTY)
  public static void aNodeReadsTheWireBesideIt(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeId node = NodeScene.placeNode(helper, owner, FLOOR, Direction.UP);
    NodeScene.enableSlot(owner, node, CHANNEL.ordinal());
    helper.setBlock(BESIDE_FLOOR, Blocks.STONE);
    helper.setBlock(BESIDE, Blocks.REDSTONE_WIRE);
    helper.setBlock(BESIDE_SOURCE, Blocks.REDSTONE_BLOCK);

    helper.succeedWhen(() -> {
      helper.assertValueEqual(
          NodeScene.networkOf(node).channelValue(CHANNEL), Signal.MAX, NOT_READ);
      helper.assertValueEqual(
          helper.getBlockState(BESIDE).getValue(RedStoneWireBlock.WEST) != RedstoneSide.NONE,
          true,
          NOT_TURNED);
    });
  }

  /** The game wire beside a floor node takes what it emits (answer 574). */
  @GameTest(template = EMPTY)
  public static void aNodeFeedsTheWireBesideIt(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    emittingNode(helper, owner);
    helper.setBlock(BESIDE_FLOOR, Blocks.STONE);
    helper.setBlock(BESIDE, Blocks.REDSTONE_WIRE);

    helper.succeedWhen(() -> helper.assertBlockProperty(BESIDE, RedStoneWireBlock.POWER, FULL));
  }

  /**
   * A node on the floor of the space with an output on the channel, which a second node, on a block
   * of redstone away from the space, holds at 15.
   */
  private static void emittingNode(GameTestHelper helper, Player owner) {
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeId reader = NodeScene.placeNode(helper, owner, READER_STONE, Direction.UP);
    NodeScene.enableSlot(owner, reader, CHANNEL.ordinal());
    NodeId emitter = NodeScene.placeNode(helper, owner, FLOOR, Direction.UP);
    NodeScene.networkOf(emitter)
        .configureSlot(
            new PlayerId(owner.getUUID()),
            emitter,
            CHANNEL.ordinal(),
            new ChannelSlot(
                io.github.marcelofabianov.redweave.domain.network.Direction.OUTPUT,
                Face.UP,
                CHANNEL));
    helper.setBlock(READER_STONE, Blocks.REDSTONE_BLOCK);
  }
}
