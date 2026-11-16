package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.block.part.ItemSensorPart;
import io.github.marcelofabianov.redweave.domain.module.ItemSensor;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

/**
 * The Signal Item Sensor of a GameTest: a container, the sensor placed on top of it by the player,
 * who owns it, with its front north, and the items put in the container.
 */
final class ItemSensorScene {
  private static final float LOOK_NORTH = 180;

  /** The most a test expects, well under the config. */
  static final int MOST = 64;

  private ItemSensorScene() {}

  /** A sensor on top of the container, by the player, who owns it, with its front north. */
  static ItemSensorPart place(GameTestHelper helper, Player owner, BlockPos box, Block container) {
    helper.setBlock(box, container);
    owner.setYRot(LOOK_NORTH);
    owner.setYHeadRot(LOOK_NORTH);
    GameTestActions.placeOn(
        helper, owner, new ItemStack(RedweaveItems.SIGNAL_ITEM_SENSOR.get()), box, Direction.UP);
    return ModuleParts.only(helper, box.above(), ItemSensorPart.class);
  }

  /** The sensor now expects the count of the item, as its owner picks on the screen. */
  static void expect(ItemSensorPart sensor, Item item, int count) {
    String key = BuiltInRegistries.ITEM.getKey(item).toString();
    sensor.setSensor(ItemSensor.placed().withItem(Optional.of(key)).withCount(count, MOST));
  }

  /** Puts the items in the slot of the container. */
  static void put(GameTestHelper helper, BlockPos box, int slot, Item item, int count) {
    Container container = (Container) helper.getBlockEntity(box);
    container.setItem(slot, new ItemStack(item, count));
  }
}
