package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Signal Cable in the world: it loses 1 a space and nothing on an inner curve, joins around
 * edges and up steps, keeps colors and owners apart and meets the game wire with one less (answers
 * 495, 497, 499, 528, 541 and 565). The fade down to 0 after 15 spaces is in CableRunTest.
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SignalCableGameTests {
  private static final String EMPTY = "empty";
  private static final int FULL = 15;
  private static final int ONE_LESS = FULL - 1;
  private static final int TWO_LESS = FULL - 2;
  private static final BlockPos LEVER = new BlockPos(0, 1, 1);
  private static final BlockPos FIRST = LEVER.east();
  private static final BlockPos SECOND = FIRST.east();
  private static final BlockPos SOUTH = FIRST.south();
  private static final BlockPos SECOND_SOUTH = SECOND.south();

  private SignalCableGameTests() {}

  /** From 15 by the lever, the cable loses 1 a space (answer 499). */
  @GameTest(template = EMPTY)
  public static void aCableLosesOnePerSpace(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    CableScene.floorRow(helper, owner, FIRST.below(), 2, ChannelColor.RED);
    CableScene.leverOn(helper, LEVER);

    helper.succeedWhen(() -> {
      CableScene.assertFloor(helper, FIRST, FULL);
      CableScene.assertFloor(helper, SECOND, ONE_LESS);
    });
  }

  /**
   * The cable on the east wall of a space takes the strength of the floor cable of that space
   * whole, and the next one up the wall loses 1 (answer 541).
   */
  @GameTest(template = EMPTY)
  public static void anInnerCurveLosesNothing(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    CableScene.floorRow(helper, owner, FIRST.below(), 1, ChannelColor.RED);
    CableScene.floorRow(helper, owner, SOUTH.below(), 1, ChannelColor.RED);
    onEastWall(helper, owner, SOUTH);
    onEastWall(helper, owner, SOUTH.above());
    CableScene.leverOn(helper, LEVER);

    helper.succeedWhen(() -> {
      CableScene.assertFloor(helper, SOUTH, ONE_LESS);
      CableScene.assertStrength(helper, SOUTH, Direction.EAST, ONE_LESS);
      CableScene.assertStrength(helper, SOUTH.above(), Direction.EAST, TWO_LESS);
    });
  }

  /** Over the edge of a block, the cable goes on down its side (answer 495). */
  @GameTest(template = EMPTY)
  public static void aCableTurnsAroundAnEdge(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    helper.setBlock(FIRST, Blocks.STONE);
    CableScene.place(helper, owner, FIRST, Direction.UP, ChannelColor.RED);
    CableScene.place(helper, owner, FIRST, Direction.EAST, ChannelColor.RED);
    CableScene.leverOn(helper, LEVER.above());

    helper.succeedWhen(() -> {
      CableScene.assertFloor(helper, FIRST.above(), FULL);
      CableScene.assertStrength(helper, SECOND, Direction.WEST, ONE_LESS);
    });
  }

  /** Up a step, the cable joins the one on top of the next block (answer 495). */
  @GameTest(template = EMPTY)
  public static void aCableClimbsAStep(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    CableScene.floorRow(helper, owner, FIRST.below(), 1, ChannelColor.RED);
    helper.setBlock(SECOND.below(), Blocks.STONE);
    helper.setBlock(SECOND, Blocks.STONE);
    CableScene.place(helper, owner, SECOND, Direction.UP, ChannelColor.RED);
    CableScene.leverOn(helper, LEVER);

    helper.succeedWhen(() -> {
      CableScene.assertFloor(helper, FIRST, FULL);
      CableScene.assertFloor(helper, SECOND.above(), ONE_LESS);
    });
  }

  /** A cable of another color beside it stays off (answer 497). */
  @GameTest(template = EMPTY)
  public static void anotherColorStaysOff(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    CableScene.floorRow(helper, owner, FIRST.below(), 1, ChannelColor.RED);
    CableScene.floorRow(helper, owner, SECOND.below(), 1, ChannelColor.BLUE);
    CableScene.leverOn(helper, LEVER);

    helper.succeedWhen(() -> {
      CableScene.assertFloor(helper, FIRST, FULL);
      CableScene.assertFloor(helper, SECOND, 0);
    });
  }

  /** A cable of another owner beside it stays off (answer 528). */
  @GameTest(template = EMPTY)
  public static void anotherOwnerStaysOff(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);
    CableScene.floorRow(helper, owner, FIRST.below(), 1, ChannelColor.RED);
    CableScene.floorRow(helper, stranger, SECOND.below(), 1, ChannelColor.RED);
    CableScene.leverOn(helper, LEVER);

    helper.succeedWhen(() -> {
      CableScene.assertFloor(helper, FIRST, FULL);
      CableScene.assertFloor(helper, SECOND, 0);
    });
  }

  /** The game wire takes one less from the cable, and gives one less back (answer 565). */
  @GameTest(template = EMPTY)
  public static void theGameWireMeetsTheCableWithOneLess(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    CableScene.floorRow(helper, owner, FIRST.below(), 1, ChannelColor.RED);
    helper.setBlock(SECOND.below(), Blocks.STONE);
    helper.setBlock(SECOND, Blocks.REDSTONE_WIRE);
    CableScene.floorRow(helper, owner, SECOND_SOUTH.below(), 1, ChannelColor.RED);
    CableScene.leverOn(helper, LEVER);

    helper.succeedWhen(() -> {
      helper.assertBlockPresent(Blocks.REDSTONE_WIRE, SECOND);
      helper.assertBlockProperty(SECOND, RedStoneWireBlock.POWER, ONE_LESS);
      CableScene.assertFloor(helper, SECOND_SOUTH, TWO_LESS);
    });
  }

  /** Breaking a cable of the run leaves the cable after it off. */
  @GameTest(template = EMPTY)
  public static void breakingACableTurnsTheRestOff(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    CableScene.floorRow(helper, owner, FIRST.below(), 2, ChannelColor.RED);
    CableScene.floorRow(helper, owner, SECOND_SOUTH.below(), 1, ChannelColor.RED);
    CableScene.leverOn(helper, LEVER);

    helper
        .startSequence()
        .thenWaitUntil(() -> CableScene.assertFloor(helper, SECOND_SOUTH, TWO_LESS))
        .thenExecute(() -> GameTestActions.breakBy(helper, owner, SECOND))
        .thenWaitUntil(() -> CableScene.assertFloor(helper, SECOND_SOUTH, 0))
        .thenExecute(() -> helper.assertItemEntityPresent(RedweaveItems.SIGNAL_CABLE.get()))
        .thenSucceed();
  }

  private static void onEastWall(GameTestHelper helper, Player owner, BlockPos space) {
    BlockPos wall = space.east();
    helper.setBlock(wall, Blocks.STONE);
    CableScene.place(helper, owner, wall, Direction.WEST, ChannelColor.RED);
  }
}
