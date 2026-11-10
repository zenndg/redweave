package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.item.LabelMessages;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/** The texts of the Label in English, the base language (answers 119 and 122). */
final class LabelEnglishTexts {
  private static final String NAME = "Label";
  private static final String WRITTEN = "Copied %s slots to a Label";
  private static final String SLOT = "%s · %s · %s";
  private static final String EMPTY = "Empty";
  private static final String HINT_APPLY = "Click a node to apply.";
  private static final String HINT_WRITE = "Shift + click a node with a Configurator to write it.";

  private LabelEnglishTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveItems.LABEL.get().getDescriptionId(), NAME);
    add.accept(LabelMessages.WRITTEN, WRITTEN);
    add.accept(LabelMessages.SLOT, SLOT);
    add.accept(LabelMessages.EMPTY, EMPTY);
    add.accept(LabelMessages.HINT_APPLY, HINT_APPLY);
    add.accept(LabelMessages.HINT_WRITE, HINT_WRITE);
  }
}
