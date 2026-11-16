package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.block.part.PartMessages;
import io.github.marcelofabianov.redweave.menu.ItemSensorScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/**
 * The texts of the Signal Item Sensor in English (sensor-de-itens.md and answers 691, 694, 696 and
 * 697). `Strength` is that of the Signal Lever, and `On` and `Off` those of the Signal Button.
 */
final class ItemSensorEnglishTexts {
  private static final String SIGNAL_ITEM_SENSOR = "Signal Item Sensor";
  private static final String ITEM = "Item";
  private static final String COUNT = "Count";
  private static final String NONE = "None";
  private static final String HINT = "Click with an item on the cursor; empty, it clears.";
  private static final String NEEDS_CONTAINER = "This needs a container.";
  private static final String NOTHING_TO_TOGGLE = "Signal Item Sensor has nothing to toggle.";

  private ItemSensorEnglishTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveItems.SIGNAL_ITEM_SENSOR.get().getDescriptionId(), SIGNAL_ITEM_SENSOR);
    add.accept(ItemSensorScreenTexts.ITEM, ITEM);
    add.accept(ItemSensorScreenTexts.COUNT, COUNT);
    add.accept(ItemSensorScreenTexts.NONE, NONE);
    add.accept(ItemSensorScreenTexts.GHOST_HINT, HINT);
    add.accept(PartMessages.NEEDS_CONTAINER, NEEDS_CONTAINER);
    add.accept(ModuleMessages.ITEM_SENSOR_NOTHING_TO_TOGGLE, NOTHING_TO_TOGGLE);
  }
}
