package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.part.ItemSensorPart;
import io.github.zenndg.redweave.registry.RedweaveItems;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Signal Item Sensor in the world: _docs/funcionalidades/sensor-de-itens.md. It only goes on a
 * container, counts the expected item in every slot, gives its front edge only with exactly the
 * expected count, and keeps what it expects in its save (answers 692, 694, 696 and 697).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ItemSensorGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos BOX = new BlockPos(1, 1, 1);
  private static final BlockPos SENSOR = BOX.above();
  private static final BlockPos LAMP_IN_FRONT = SENSOR.north();
  private static final int TEN = 10;
  private static final int FOUR = 4;
  private static final int FIVE = 5;
  private static final int THREE = 3;
  private static final int SLOT_THREE = 3;
  private static final int READ = 15;
  private static final String SAVED_ITEM = "item";
  private static final String UNKNOWN = "redweave:no_such_item";
  private static final String ON_STONE = "No sensor goes on a block without a container";
  private static final String WRONG_COUNT = "The sensor should count the item in every slot";
  private static final String NOT_ON = "Exactly the expected count should turn the sensor on";
  private static final String NOT_SAVED = "The sensor should keep what it expects in its save";
  private static final String NOT_DROPPED = "An unknown item should be dropped from the save";
  private static final String DARK_ON_CLIENT = "The client should see the green light";

  private ItemSensorGameTests() {}

  /** One less and one more leave it off; exactly the count lights the lamp in front (692). */
  @GameTest(template = EMPTY)
  public static void onlyTheExactCountInAChestTurnsItOn(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    ItemSensorPart sensor = ItemSensorScene.place(helper, owner, BOX, Blocks.CHEST);
    helper.setBlock(LAMP_IN_FRONT, Blocks.REDSTONE_LAMP);
    ItemSensorScene.expect(sensor, Items.DIAMOND, TEN);
    ItemSensorScene.put(helper, BOX, 0, Items.DIAMOND, FOUR);
    ItemSensorScene.put(helper, BOX, 1, Items.DIAMOND, FIVE);
    ItemSensorScene.put(helper, BOX, 2, Items.STONE, TEN);

    helper
        .startSequence()
        .thenExecuteAfter(
            READ,
            () -> {
              helper.assertValueEqual(sensor.counted(), FOUR + FIVE, WRONG_COUNT);
              helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, false);
              ItemSensorScene.put(helper, BOX, SLOT_THREE, Items.DIAMOND, 1);
            })
        .thenExecuteAfter(
            READ,
            () -> {
              helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, true);
              ItemSensorPart client = ItemSensorPart.load(sensor.face(), sensor.save());
              helper.assertValueEqual(client.look(), sensor.look(), DARK_ON_CLIENT);
              ItemSensorScene.put(helper, BOX, SLOT_THREE, Items.DIAMOND, 2);
            })
        .thenExecuteAfter(
            READ, () -> helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, false))
        .thenSucceed();
  }

  /** A barrel is a container too, with the count read through its capability (694). */
  @GameTest(template = EMPTY)
  public static void aBarrelIsReadToo(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    ItemSensorPart sensor = ItemSensorScene.place(helper, owner, BOX, Blocks.BARREL);
    ItemSensorScene.expect(sensor, Items.REDSTONE, THREE);
    ItemSensorScene.put(helper, BOX, 0, Items.REDSTONE, THREE);

    helper.succeedWhen(() -> helper.assertTrue(sensor.sensor().on(sensor.counted()), NOT_ON));
  }

  /** On a block with no container, the sensor is refused and the item stays (answer 694). */
  @GameTest(template = EMPTY)
  public static void aBlockWithNoContainerRefusesIt(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    helper.setBlock(BOX, Blocks.STONE);

    GameTestActions.placeOn(
        helper, owner, new ItemStack(RedweaveItems.SIGNAL_ITEM_SENSOR.get()), BOX, Direction.UP);

    helper.assertBlockPresent(Blocks.AIR, SENSOR);
    helper.assertTrue(owner.getMainHandItem().is(RedweaveItems.SIGNAL_ITEM_SENSOR.get()), ON_STONE);
    helper.succeed();
  }

  /** The item, the count and the output survive the save; an unknown item is dropped (rule 14). */
  @GameTest(template = EMPTY)
  public static void theSaveKeepsWhatItExpects(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    ItemSensorPart sensor = ItemSensorScene.place(helper, owner, BOX, Blocks.CHEST);
    ItemSensorScene.expect(sensor, Items.DIAMOND, TEN);
    CompoundTag saved = sensor.save();

    ItemSensorPart loaded = ItemSensorPart.load(Direction.DOWN, saved);
    helper.assertValueEqual(loaded.sensor(), sensor.sensor(), NOT_SAVED);
    helper.assertValueEqual(loaded.output(), sensor.output(), NOT_SAVED);

    saved.putString(SAVED_ITEM, UNKNOWN);
    helper.assertValueEqual(
        ItemSensorPart.load(Direction.DOWN, saved).sensor().item(), Optional.empty(), NOT_DROPPED);
    helper.succeed();
  }
}
