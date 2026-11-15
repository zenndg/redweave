package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.part.CablePart;
import io.github.marcelofabianov.redweave.block.part.CableReach;
import io.github.marcelofabianov.redweave.block.part.InverterPart;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * A Signal Cable beside a Signal Module on the same side: it exchanges signal only by the back or
 * the front edge, and only then its arm reaches the plate, 3 px past the edge, with the aim
 * following it (answers 566, 571 and 575).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class CablePlateGameTests {
  private static final String EMPTY = "empty";
  private static final int FULL = 15;
  private static final int TURNS = 4;
  private static final double PAST_EDGE = -3.0 / 16;
  private static final BlockPos SPACE = new BlockPos(1, 1, 1);
  private static final BlockPos FLOOR = SPACE.below();
  private static final BlockPos BESIDE = SPACE.east();
  private static final BlockPos BESIDE_FLOOR = BESIDE.below();
  private static final String NOT_PLATE = "The cable should reach the plate past its edge";
  private static final String PLATE = "The cable should not reach a side edge of the module";
  private static final String NOT_AIMED = "The aim should follow the arm to the plate";
  private static final String NOT_TURNED = "The inverter should turn to the front";

  private CablePlateGameTests() {}

  /** The front edge of an inverter feeds the cable beside it, which reaches its plate (575). */
  @GameTest(template = EMPTY)
  public static void aModuleFrontFeedsTheCableBesideIt(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    helper.setBlock(BESIDE_FLOOR, Blocks.STONE);
    CableScene.place(helper, owner, BESIDE_FLOOR, Direction.UP, ChannelColor.RED);
    InverterPart inverter = floorInverter(helper, owner, Direction.EAST);

    helper.succeedWhen(() -> {
      CablePart cable = CableScene.at(helper, BESIDE, Direction.DOWN);
      helper.assertValueEqual(inverter.front(), Direction.EAST, NOT_TURNED);
      CableScene.assertFloor(helper, BESIDE, FULL);
      assertWest(helper, cable, CableReach.PLATE, NOT_PLATE);
      helper.assertValueEqual(cable.shape().bounds().minX, PAST_EDGE, NOT_AIMED);
    });
  }

  /** The cable beside feeds the back edge of an inverter, and reaches its plate (575). */
  @GameTest(template = EMPTY)
  public static void theCableBesideFeedsAModuleBackEdge(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    helper.setBlock(BESIDE_FLOOR, Blocks.REDSTONE_BLOCK);
    CableScene.place(helper, owner, BESIDE_FLOOR, Direction.UP, ChannelColor.RED);
    floorInverter(helper, owner, Direction.WEST);

    helper.succeedWhen(() -> {
      CableScene.assertFloor(helper, BESIDE, FULL);
      ModuleParts.assertFront(helper, SPACE, Direction.WEST);
      ModuleParts.assertPowered(helper, SPACE, false);
      assertWest(
          helper, CableScene.at(helper, BESIDE, Direction.DOWN), CableReach.PLATE, NOT_PLATE);
    });
  }

  /** Toward a side edge of the module, which exchanges nothing, the arm stops at the edge. */
  @GameTest(template = EMPTY)
  public static void theArmStopsAtASideEdgeOfAModule(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    helper.setBlock(BESIDE_FLOOR, Blocks.STONE);
    CableScene.place(helper, owner, BESIDE_FLOOR, Direction.UP, ChannelColor.RED);
    floorInverter(helper, owner, Direction.NORTH);

    helper.succeedWhen(() -> {
      ModuleParts.assertFront(helper, SPACE, Direction.NORTH);
      CableScene.assertFloor(helper, BESIDE, 0);
      assertWest(helper, CableScene.at(helper, BESIDE, Direction.DOWN), CableReach.EDGE, PLATE);
    });
  }

  /** An inverter on the floor of the space, turned until its front points the direction. */
  private static InverterPart floorInverter(GameTestHelper helper, Player owner, Direction front) {
    helper.setBlock(FLOOR, Blocks.STONE);
    GameTestActions.placeOn(
        helper, owner, new ItemStack(RedweaveItems.SIGNAL_INVERTER.get()), FLOOR, Direction.UP);
    InverterPart inverter = ModuleParts.only(helper, SPACE, InverterPart.class);
    for (int turn = 0; turn < TURNS && inverter.front() != front; turn++) {
      inverter.turn();
    }
    return inverter;
  }

  private static void assertWest(
      GameTestHelper helper, CablePart cable, CableReach reach, String message) {
    helper.assertValueEqual(
        Optional.ofNullable(cable.arms().get(Direction.WEST)), Optional.of(reach), message);
  }
}
