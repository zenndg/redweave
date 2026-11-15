package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.menu.LeverScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/** The texts of the Signal Lever in Portuguese (alavanca.md and answers 550 and 582). */
final class LeverPortugueseTexts {
  private static final String SIGNAL_LEVER = "Alavanca de Sinal";
  private static final String ON = "Ligada";
  private static final String OFF = "Desligada";
  private static final String STRENGTH = "Força";
  private static final String TURNED_ON = "%s: Ligada";
  private static final String TURNED_OFF = "%s: Desligada";

  private LeverPortugueseTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveItems.SIGNAL_LEVER.get().getDescriptionId(), SIGNAL_LEVER);
    add.accept(LeverScreenTexts.ON, ON);
    add.accept(LeverScreenTexts.OFF, OFF);
    add.accept(LeverScreenTexts.STRENGTH, STRENGTH);
    add.accept(ModuleMessages.LEVER_ON, TURNED_ON);
    add.accept(ModuleMessages.LEVER_OFF, TURNED_OFF);
  }
}
