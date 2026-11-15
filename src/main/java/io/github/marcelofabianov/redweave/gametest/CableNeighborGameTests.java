package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.part.InverterPart;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Signal Cable and the other parts of its space, by the inner curve: a node reads the cable as
 * its block of support and the cable reads what the node emits, and a module meets the cable only
 * by the edge on the cable's side (answer 566).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class CableNeighborGameTests {
  private static final String EMPTY = "empty";
  private static final int FULL = 15;
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos SPACE = new BlockPos(1, 1, 1);
  private static final BlockPos FLOOR = SPACE.below();
  private static final BlockPos WALL = SPACE.north();
  private static final BlockPos LEVER_STONE = new BlockPos(2, 0, 2);
  private static final BlockPos FLOOR_LEVER = LEVER_STONE.above();
  private static final ChannelColor CHANNEL = ChannelColor.WHITE;
  private static final float LOOK_DOWN = 90;
  private static final float LOOK_UP = -90;
  private static final String NOT_READ = "The node should read the cable of its space";

  private CableNeighborGameTests() {}

  /** A node on the floor reads the cable on the wall of its space as its block (566). */
  @GameTest(template = EMPTY)
  public static void aNodeReadsTheCableOfItsSpace(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeId node = NodeScene.placeNode(helper, owner, FLOOR, Direction.UP);
    NodeScene.enableSlot(owner, node, CHANNEL.ordinal());
    wallCable(helper, owner);
    helper.setBlock(WALL, Blocks.REDSTONE_BLOCK);

    helper.succeedWhen(() -> {
      CableScene.assertStrength(helper, SPACE, Direction.NORTH, FULL);
      helper.assertValueEqual(
          NodeScene.networkOf(node).channelValue(CHANNEL), Signal.MAX, NOT_READ);
    });
  }

  /** The cable on the wall reads what the node on the floor of its space emits (566). */
  @GameTest(template = EMPTY)
  public static void aCableReadsTheNodeOfItsSpace(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeId reader = NodeScene.placeNode(helper, owner, LEVER_STONE, Direction.WEST);
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
    wallCable(helper, owner);
    helper.setBlock(
        FLOOR_LEVER, Blocks.LEVER.defaultBlockState().setValue(LeverBlock.FACE, AttachFace.FLOOR));
    helper.pullLever(FLOOR_LEVER);

    helper.succeedWhen(() -> CableScene.assertStrength(helper, SPACE, Direction.NORTH, FULL));
  }

  /** An inverter on the wall with its front down feeds the floor cable of its space (566). */
  @GameTest(template = EMPTY)
  public static void aModuleFrontFeedsTheCable(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    helper.setBlock(FLOOR, Blocks.STONE);
    CableScene.place(helper, owner, FLOOR, Direction.UP, ChannelColor.RED);
    placeInverter(helper, owner, LOOK_DOWN);

    helper.succeedWhen(() -> CableScene.assertFloor(helper, SPACE, FULL));
  }

  /** A powered floor cable feeds the back edge of an inverter whose front is up (566). */
  @GameTest(template = EMPTY)
  public static void theCableFeedsAModuleBackEdge(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    helper.setBlock(FLOOR, Blocks.STONE);
    CableScene.place(helper, owner, FLOOR, Direction.UP, ChannelColor.RED);
    placeInverter(helper, owner, LOOK_UP);
    helper.setBlock(FLOOR, Blocks.REDSTONE_BLOCK);

    helper.succeedWhen(() -> {
      CableScene.assertFloor(helper, SPACE, FULL);
      ModuleParts.assertFront(helper, SPACE, Direction.UP);
      ModuleParts.assertPowered(helper, SPACE, false);
    });
  }

  private static void wallCable(GameTestHelper helper, Player owner) {
    helper.setBlock(WALL, Blocks.STONE);
    CableScene.place(helper, owner, WALL, Direction.SOUTH, ChannelColor.RED);
  }

  private static void placeInverter(GameTestHelper helper, Player owner, float look) {
    helper.setBlock(WALL, Blocks.STONE);
    owner.setXRot(look);
    GameTestActions.placeOn(
        helper, owner, new ItemStack(RedweaveItems.SIGNAL_INVERTER.get()), WALL, Direction.SOUTH);
    ModuleParts.only(helper, SPACE, InverterPart.class);
  }
}
