package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.part.LampPart;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The hitbox of the Signal Lamp follows its model: a copper base of 6 × 12 px and a glass dome on
 * it, 5 px high, long along its front, on any face and turned (lampada.md, answers 820, 824, 828
 * and 829).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class LampShapeGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final double PIXEL = 1.0 / 16;
  private static final double HIGH = 5 * PIXEL;
  private static final double LONG = 12 * PIXEL;
  private static final double WIDE = 6 * PIXEL;
  private static final double FULL = 1;
  private static final double EPSILON = 1e-6;
  private static final float LOOK_NORTH = 180;
  private static final String WRONG_HEIGHT = "The lamp should be 5 px high";
  private static final String WRONG_LENGTH = "The lamp should be 12 px long along its front";
  private static final String WRONG_WIDTH = "The lamp should be 6 px wide";
  private static final String NOT_ON_FACE = "The lamp should rest on its face";

  private LampShapeGameTests() {}

  @GameTest(template = EMPTY)
  public static void aLampOnTheFloorIsFiveHighAndLongAlongItsFront(GameTestHelper helper) {
    AABB box = LampScene.place(helper, owner(helper), STONE).shape().bounds();

    assertSize(helper, box.getYsize(), HIGH, WRONG_HEIGHT);
    assertSize(helper, box.getZsize(), LONG, WRONG_LENGTH);
    assertSize(helper, box.getXsize(), WIDE, WRONG_WIDTH);
    assertSize(helper, box.minY, 0, NOT_ON_FACE);
    helper.succeed();
  }

  /** The `Rotate` of the Configurator turns it a quarter, long across (answer 820). */
  @GameTest(template = EMPTY)
  public static void aTurnedLampIsLongAcross(GameTestHelper helper) {
    LampPart lamp = LampScene.place(helper, owner(helper), STONE);
    lamp.turn(false);

    AABB box = lamp.shape().bounds();

    assertSize(helper, box.getXsize(), LONG, WRONG_LENGTH);
    assertSize(helper, box.getZsize(), WIDE, WRONG_WIDTH);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void aLampOnAWallStandsFiveOutOfIt(GameTestHelper helper) {
    Player owner = owner(helper);
    helper.setBlock(STONE, Blocks.STONE);
    owner.setYRot(LOOK_NORTH);
    GameTestActions.placeOn(
        helper, owner, new ItemStack(RedweaveItems.SIGNAL_LAMP.get()), STONE, Direction.NORTH);

    AABB box = ModuleParts.only(helper, STONE.north(), LampPart.class).shape().bounds();

    assertSize(helper, box.getZsize(), HIGH, WRONG_HEIGHT);
    assertSize(helper, box.maxZ, FULL, NOT_ON_FACE);
    helper.succeed();
  }

  private static Player owner(GameTestHelper helper) {
    return helper.makeMockPlayer(GameType.SURVIVAL);
  }

  private static void assertSize(GameTestHelper helper, double actual, double wanted, String why) {
    helper.assertTrue(Math.abs(actual - wanted) < EPSILON, why);
  }
}
