package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.menu.LeverScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/** The texts of the Signal Lever in English (alavanca.md and answers 550 and 582). */
final class LeverEnglishTexts {
  private static final String SIGNAL_LEVER = "Signal Lever";
  private static final String ON = "On";
  private static final String OFF = "Off";
  private static final String STRENGTH = "Strength";
  private static final String TURNED_ON = "%s: On";
  private static final String TURNED_OFF = "%s: Off";

  private LeverEnglishTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveItems.SIGNAL_LEVER.get().getDescriptionId(), SIGNAL_LEVER);
    add.accept(LeverScreenTexts.ON, ON);
    add.accept(LeverScreenTexts.OFF, OFF);
    add.accept(LeverScreenTexts.STRENGTH, STRENGTH);
    add.accept(ModuleMessages.LEVER_ON, TURNED_ON);
    add.accept(ModuleMessages.LEVER_OFF, TURNED_OFF);
  }
}
