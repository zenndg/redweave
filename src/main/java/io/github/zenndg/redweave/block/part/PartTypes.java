package io.github.zenndg.redweave.block.part;

import io.github.zenndg.redweave.registry.RedweaveItems;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * The kinds of part by the ID of their item, which is the `type` in the save (answer 544): how each
 * one is read from its `data`, or born from an empty one when its item is placed.
 */
public final class PartTypes {
  private static final Map<ResourceLocation, BiFunction<Direction, CompoundTag, Part>> TYPES =
      Map.ofEntries(
          Map.entry(RedweaveItems.SIGNAL_NODE.getId(), NodePart::load),
          Map.entry(RedweaveItems.SIGNAL_TIMER.getId(), TimerPart::load),
          Map.entry(RedweaveItems.SIGNAL_INVERTER.getId(), InverterPart::load),
          Map.entry(RedweaveItems.WEATHER_DETECTOR.getId(), DetectorPart::load),
          Map.entry(RedweaveItems.SIGNAL_RANDOMIZER.getId(), RandomizerPart::load),
          Map.entry(RedweaveItems.SIGNAL_LEVER.getId(), LeverPart::load),
          Map.entry(RedweaveItems.SIGNAL_BUTTON.getId(), ButtonPart::load),
          Map.entry(RedweaveItems.SIGNAL_PRESSURE_PLATE.getId(), PressurePlatePart::load),
          Map.entry(RedweaveItems.SIGNAL_ITEM_SENSOR.getId(), ItemSensorPart::load),
          Map.entry(RedweaveItems.SIGNAL_LOCK.getId(), LockPart::load),
          Map.entry(RedweaveItems.SIGNAL_LAMP.getId(), LampPart::load),
          Map.entry(RedweaveItems.SIGNAL_ALARM.getId(), AlarmPart::load),
          Map.entry(RedweaveItems.SIGNAL_FLIP_FLOP.getId(), FlipFlopPart::load),
          Map.entry(RedweaveItems.SIGNAL_CABLE.getId(), CablePart::load));

  private PartTypes() {}

  /** The part of the type on the side, from its NBT, or none for an unknown type. */
  public static Optional<Part> read(ResourceLocation type, Direction face, CompoundTag data) {
    return Optional.ofNullable(TYPES.get(type)).map(factory -> factory.apply(face, data));
  }

  /** A new part of the item on the side, or none if the item is not a part. */
  public static Optional<Part> placed(Item item, Direction face) {
    return read(BuiltInRegistries.ITEM.getKey(item), face, new CompoundTag());
  }

  /** The type of the part in the save: the ID of its item. */
  public static ResourceLocation typeOf(Part part) {
    return BuiltInRegistries.ITEM.getKey(part.item());
  }
}
