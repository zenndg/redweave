package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.block.part.PartMessages;
import io.github.marcelofabianov.redweave.menu.ItemSensorScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/**
 * The texts of the Signal Item Sensor in Portuguese (sensor-de-itens.md and answers 691, 694, 696
 * and 697), with the keys of the English ones (rule 1).
 */
final class ItemSensorPortugueseTexts {
  private static final String SIGNAL_ITEM_SENSOR = "Sensor de Itens de Sinal";
  private static final String ITEM = "Item";
  private static final String COUNT = "Quantidade";
  private static final String NONE = "Nenhum";
  private static final String HINT = "Clique com um item no cursor; com o cursor vazio, limpa.";
  private static final String NEEDS_CONTAINER = "Isto precisa de um baú ou outro inventário.";
  private static final String NOTHING = "O Sensor de Itens de Sinal não tem o que alternar.";

  private ItemSensorPortugueseTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveItems.SIGNAL_ITEM_SENSOR.get().getDescriptionId(), SIGNAL_ITEM_SENSOR);
    add.accept(ItemSensorScreenTexts.ITEM, ITEM);
    add.accept(ItemSensorScreenTexts.COUNT, COUNT);
    add.accept(ItemSensorScreenTexts.NONE, NONE);
    add.accept(ItemSensorScreenTexts.GHOST_HINT, HINT);
    add.accept(PartMessages.NEEDS_CONTAINER, NEEDS_CONTAINER);
    add.accept(ModuleMessages.ITEM_SENSOR_NOTHING_TO_TOGGLE, NOTHING);
  }
}
