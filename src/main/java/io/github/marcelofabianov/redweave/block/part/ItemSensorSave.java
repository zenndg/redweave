package io.github.marcelofabianov.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.marcelofabianov.redweave.domain.module.ItemSensor;
import java.util.Optional;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

/**
 * The NBT of a Signal Item Sensor besides its module, part of the save (rule 15): the expected
 * item, under `item`, as its ID, absent when none is picked; the expected count, under `count`; its
 * strength; and its output. An unknown item is dropped and an invalid count goes back to 1, with a
 * warning (rule 14).
 */
final class ItemSensorSave {
  static final String OUTPUT = "output";

  static final String ITEM = "item";
  static final String COUNT = "count";
  private static final String MODULE = "Signal Item Sensor";
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String DROPPED_ITEM = "Dropped an unknown item of a Signal Item Sensor: {}";
  private static final String BAD_COUNT = "Dropped an invalid count of a Signal Item Sensor: {}";

  private ItemSensorSave() {}

  static void write(CompoundTag tag, ItemSensor sensor) {
    sensor.item().ifPresent(item -> tag.putString(ITEM, item));
    tag.putInt(COUNT, sensor.count());
    StrengthSave.write(tag, sensor.strength());
  }

  static ItemSensor read(CompoundTag tag) {
    ItemSensor placed = ItemSensor.placed();
    int count = tag.getInt(COUNT);
    if (count < ItemSensor.FEWEST) {
      LOGGER.warn(BAD_COUNT, count);
      count = placed.count();
    }
    return new ItemSensor(itemOf(tag), count, StrengthSave.read(tag, MODULE));
  }

  /** The saved item, if it is still an item of the game; one gone is dropped with a warning. */
  private static Optional<String> itemOf(CompoundTag tag) {
    if (!tag.contains(ITEM, Tag.TAG_STRING)) {
      return Optional.empty();
    }
    String saved = tag.getString(ITEM);
    Optional<ResourceLocation> key =
        Optional.ofNullable(ResourceLocation.tryParse(saved))
            .filter(BuiltInRegistries.ITEM::containsKey)
            .filter(found -> !found.equals(BuiltInRegistries.ITEM.getDefaultKey()));
    if (key.isEmpty()) {
      LOGGER.warn(DROPPED_ITEM, saved);
    }
    return key.map(ResourceLocation::toString);
  }
}
