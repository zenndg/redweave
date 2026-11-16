package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.part.PressurePlatePart;
import io.github.marcelofabianov.redweave.domain.module.PlateMode;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * What turns the Signal Pressure Plate on: _docs/funcionalidades/placa-de-pressao.md. Players,
 * passive mobs, hostile mobs and items, each only in its mode, never an arrow, its edges while on,
 * and the release when nothing of its mode is left (answers 553, 605, 607 and 621).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SignalPressurePlateGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos PLATE = STONE.above();
  private static final BlockPos LAMP_IN_FRONT = PLATE.north();
  private static final BlockPos LAMP_BY_SUPPORT = STONE.east();
  private static final Vec3 FAR_AWAY = new Vec3(0, 100, 0);
  private static final String NOT_ON = "Something of its mode should turn the plate on";
  private static final String WRONG_MODE = "Something of another mode should leave it off";
  private static final String ARROW = "An arrow should never turn the plate on";
  private static final String NOT_RELEASED = "The plate should turn off when nothing is left";

  private SignalPressurePlateGameTests() {}

  /** A player turns it on, and it feeds the block in front and its block of support (553). */
  @GameTest(template = EMPTY)
  public static void aPlayerTurnsItOnAndItFeedsItsEdges(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    PressurePlatePart plate = PressurePlateScene.place(helper, owner, STONE);
    helper.setBlock(LAMP_IN_FRONT, Blocks.REDSTONE_LAMP);
    helper.setBlock(LAMP_BY_SUPPORT, Blocks.REDSTONE_LAMP);

    PressurePlateScene.stepOn(helper, owner, PLATE);

    helper.assertTrue(plate.plate().on(), NOT_ON);
    helper.succeedWhen(() -> {
      helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, true);
      helper.assertBlockProperty(LAMP_BY_SUPPORT, RedstoneLampBlock.LIT, true);
    });
  }

  /** A cow and a villager are passive: they turn on `Passive`, never `Hostile` (621). */
  @GameTest(template = EMPTY)
  public static void passiveMobsTurnOnOnlyPassive(GameTestHelper helper) {
    PressurePlatePart plate = placedIn(helper, PlateMode.HOSTILE);
    Entity cow = PressurePlateScene.standing(helper, EntityType.COW, PLATE);
    Entity villager = PressurePlateScene.standing(helper, EntityType.VILLAGER, PLATE);

    PressurePlateScene.stepOn(helper, cow, PLATE);
    PressurePlateScene.stepOn(helper, villager, PLATE);
    helper.assertFalse(plate.plate().on(), WRONG_MODE);

    plate.setPlate(plate.plate().withMode(PlateMode.PASSIVE));
    PressurePlateScene.stepOn(helper, villager, PLATE);
    helper.assertTrue(plate.plate().on(), NOT_ON);
    helper.succeed();
  }

  /** A zombie is hostile: it turns on `Hostile`, never `Passive` (621). */
  @GameTest(template = EMPTY)
  public static void aHostileMobTurnsOnOnlyHostile(GameTestHelper helper) {
    PressurePlatePart plate = placedIn(helper, PlateMode.PASSIVE);
    Entity zombie = PressurePlateScene.standing(helper, EntityType.ZOMBIE, PLATE);

    PressurePlateScene.stepOn(helper, zombie, PLATE);
    helper.assertFalse(plate.plate().on(), WRONG_MODE);

    plate.setPlate(plate.plate().withMode(PlateMode.HOSTILE));
    PressurePlateScene.stepOn(helper, zombie, PLATE);
    helper.assertTrue(plate.plate().on(), NOT_ON);
    helper.succeed();
  }

  /** A player turns on only `Players`, never `Items` (621). */
  @GameTest(template = EMPTY)
  public static void aPlayerDoesNotTurnOnItems(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    PressurePlatePart plate = PressurePlateScene.place(helper, owner, STONE);
    plate.setPlate(plate.plate().withMode(PlateMode.ITEMS));

    PressurePlateScene.stepOn(helper, owner, PLATE);

    helper.assertFalse(plate.plate().on(), WRONG_MODE);
    helper.succeed();
  }

  /**
   * An item dropped on the plate turns `Items` on by itself, as the game tells the space of what
   * falls into it (answers 607 and 621).
   */
  @GameTest(template = EMPTY)
  public static void aDroppedItemTurnsOnItems(GameTestHelper helper) {
    PressurePlatePart plate = placedIn(helper, PlateMode.ITEMS);

    Vec3 center = Vec3.atBottomCenterOf(helper.absolutePos(PLATE));
    ItemEntity item =
        new ItemEntity(helper.getLevel(), center.x, center.y, center.z, new ItemStack(Items.STONE));
    item.setDeltaMovement(Vec3.ZERO);
    helper.getLevel().addFreshEntity(item);

    helper.succeedWhen(() -> helper.assertTrue(plate.plate().on(), NOT_ON));
  }

  /** An arrow on the plate turns no mode on (answer 621). */
  @GameTest(template = EMPTY)
  public static void anArrowNeverTurnsItOn(GameTestHelper helper) {
    PressurePlatePart plate = placedIn(helper, PlateMode.PLAYERS);
    Entity arrow = helper.spawn(EntityType.ARROW, PLATE);

    for (PlateMode mode : PlateMode.values()) {
      plate.setPlate(plate.plate().withMode(mode));
      PressurePlateScene.stepOn(helper, arrow, PLATE);
      helper.assertFalse(plate.plate().on(), ARROW);
    }
    helper.succeed();
  }

  /** With nothing of its mode left, the next look turns it off (answer 607). */
  @GameTest(template = EMPTY)
  public static void itTurnsOffWhenNothingIsLeft(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    PressurePlatePart plate = PressurePlateScene.place(helper, owner, STONE);
    PressurePlateScene.stepOn(helper, owner, PLATE);
    helper.assertTrue(plate.plate().on(), NOT_ON);

    owner.setPos(FAR_AWAY);

    helper.succeedWhen(() -> helper.assertFalse(plate.plate().on(), NOT_RELEASED));
  }

  private static PressurePlatePart placedIn(GameTestHelper helper, PlateMode mode) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    PressurePlatePart plate = PressurePlateScene.place(helper, owner, STONE);
    owner.setPos(Vec3.atCenterOf(helper.absolutePos(PLATE.above())).add(FAR_AWAY));
    plate.setPlate(plate.plate().withMode(mode));
    return plate;
  }
}
