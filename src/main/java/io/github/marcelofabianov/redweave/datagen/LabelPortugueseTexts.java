package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.item.LabelMessages;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/** The texts of the Label in Portuguese, with the same keys as English (rule 1). */
final class LabelPortugueseTexts {
  private static final String NAME = "Etiqueta";
  private static final String WRITTEN = "%s slots copiados para uma etiqueta";
  private static final String SLOT = "%s · %s · %s";
  private static final String EMPTY = "Vazia";
  private static final String HINT_APPLY = "Clique num nó para aplicar.";
  private static final String HINT_WRITE = "Shift + clique num nó com um Configurador para gravar.";

  private LabelPortugueseTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveItems.LABEL.get().getDescriptionId(), NAME);
    add.accept(LabelMessages.WRITTEN, WRITTEN);
    add.accept(LabelMessages.SLOT, SLOT);
    add.accept(LabelMessages.EMPTY, EMPTY);
    add.accept(LabelMessages.HINT_APPLY, HINT_APPLY);
    add.accept(LabelMessages.HINT_WRITE, HINT_WRITE);
  }
}
