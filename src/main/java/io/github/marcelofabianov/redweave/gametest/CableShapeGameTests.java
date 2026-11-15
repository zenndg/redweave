package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.part.CablePart;
import io.github.marcelofabianov.redweave.block.part.CableReach;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The shape and the arms of the Signal Cable: the outline follows what is drawn, the center and the
 * arms, and each arm reaches as the curves and the steps ask (answers 568, 571 and 572).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class CableShapeGameTests {
  private static final String EMPTY = "empty";
  private static final double PIXEL = 1.0 / 16;
  private static final double HEIGHT = 3 * PIXEL;
  private static final double NEAR = 6 * PIXEL;
  private static final double FAR = 10 * PIXEL;
  private static final double CORNER = 2 * PIXEL;
  private static final BlockPos SPACE = new BlockPos(1, 1, 1);
  private static final BlockPos FLOOR = SPACE.below();
  private static final BlockPos EAST = SPACE.east();
  private static final String NOT_CROSS = "A lone cable should be shaped as a cross";
  private static final String NOT_STRAIGHT = "A straight cable should be shaped as its line";
  private static final String NOT_STEP = "A step should climb the side of the block";
  private static final String NOT_INNER = "An inner curve should meet with one short arm";
  private static final String NOT_OUTER = "An outer curve should reach the corner from one side";

  private CableShapeGameTests() {}

  /** A lone cable points every way, as a cross, with no box on the corners (answers 568, 571). */
  @GameTest(template = EMPTY)
  public static void aLoneCableIsShapedAsACross(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    helper.setBlock(FLOOR, Blocks.STONE);
    CableScene.place(helper, owner, FLOOR, Direction.UP, ChannelColor.RED);

    VoxelShape shape = CableScene.at(helper, SPACE, Direction.DOWN).shape();
    VoxelShape corner = Shapes.box(0, 0, 0, CORNER, CORNER, CORNER);
    helper.assertValueEqual(shape.bounds(), new AABB(0, 0, 0, 1, HEIGHT, 1), NOT_CROSS);
    helper.assertFalse(Shapes.joinIsNotEmpty(shape, corner, BooleanOp.AND), NOT_CROSS);
    helper.succeed();
  }

  /** Two cables in a row point at each other, and the shape is their line (answer 571). */
  @GameTest(template = EMPTY)
  public static void aStraightCableIsShapedAsItsLine(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    CableScene.floorRow(helper, owner, FLOOR, 2, ChannelColor.RED);

    helper.succeedWhen(() -> {
      CablePart cable = CableScene.at(helper, SPACE, Direction.DOWN);
      helper.assertValueEqual(
          cable.shape().bounds(), new AABB(0, 0, NEAR, 1, HEIGHT, FAR), NOT_STRAIGHT);
    });
  }

  /** The lower cable of a step climbs the side of the block, and the upper one only meets it. */
  @GameTest(template = EMPTY)
  public static void theLowerCableOfAStepClimbsTheBlock(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    helper.setBlock(FLOOR, Blocks.STONE);
    helper.setBlock(EAST, Blocks.STONE);
    CableScene.place(helper, owner, FLOOR, Direction.UP, ChannelColor.RED);
    CableScene.place(helper, owner, EAST, Direction.UP, ChannelColor.RED);

    helper.succeedWhen(() -> {
      CablePart lower = CableScene.at(helper, SPACE, Direction.DOWN);
      CablePart upper = CableScene.at(helper, EAST.above(), Direction.DOWN);
      assertReach(helper, lower, Direction.EAST, CableReach.STEP, NOT_STEP);
      assertReach(helper, upper, Direction.WEST, CableReach.EDGE, NOT_STEP);
      helper.assertValueEqual(lower.shape().bounds().maxY, 1.0, NOT_STEP);
    });
  }

  /** On an inner curve, the cable of the floor stops short, and the one on the wall goes on. */
  @GameTest(template = EMPTY)
  public static void anInnerCurveMeetsWithOneShortArm(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    helper.setBlock(FLOOR, Blocks.STONE);
    helper.setBlock(EAST, Blocks.STONE);
    CableScene.place(helper, owner, FLOOR, Direction.UP, ChannelColor.RED);
    CableScene.place(helper, owner, EAST, Direction.WEST, ChannelColor.RED);

    helper.succeedWhen(() -> {
      CablePart floor = CableScene.at(helper, SPACE, Direction.DOWN);
      CablePart wall = CableScene.at(helper, SPACE, Direction.EAST);
      assertReach(helper, floor, Direction.EAST, CableReach.SHORT, NOT_INNER);
      assertReach(helper, wall, Direction.DOWN, CableReach.EDGE, NOT_INNER);
    });
  }

  /** On an outer curve, the cable of the floor reaches the corner, and the one below meets it. */
  @GameTest(template = EMPTY)
  public static void anOuterCurveReachesTheCornerFromOneSide(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    helper.setBlock(FLOOR, Blocks.STONE);
    CableScene.place(helper, owner, FLOOR, Direction.UP, ChannelColor.RED);
    CableScene.place(helper, owner, FLOOR, Direction.EAST, ChannelColor.RED);

    helper.succeedWhen(() -> {
      CablePart floor = CableScene.at(helper, SPACE, Direction.DOWN);
      CablePart side = CableScene.at(helper, FLOOR.east(), Direction.WEST);
      assertReach(helper, floor, Direction.EAST, CableReach.CORNER, NOT_OUTER);
      assertReach(helper, side, Direction.UP, CableReach.EDGE, NOT_OUTER);
    });
  }

  private static void assertReach(
      GameTestHelper helper,
      CablePart cable,
      Direction direction,
      CableReach reach,
      String message) {
    Map<Direction, CableReach> arms = cable.arms();
    helper.assertValueEqual(Optional.ofNullable(arms.get(direction)), Optional.of(reach), message);
  }
}
