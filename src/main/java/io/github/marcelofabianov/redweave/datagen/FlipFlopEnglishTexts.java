package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.menu.FlipFlopScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/** The texts of the Signal Flip-Flop in English (signal-flip-flop.md, answers 877 and 890). */
final class FlipFlopEnglishTexts {
  private static final String SIGNAL_FLIP_FLOP = "Signal Flip-Flop";
  private static final String ON = "On";
  private static final String OFF = "Off";
  private static final String TURNED_ON = "%s: On";
  private static final String TURNED_OFF = "%s: Off";

  private FlipFlopEnglishTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveItems.SIGNAL_FLIP_FLOP.get().getDescriptionId(), SIGNAL_FLIP_FLOP);
    add.accept(FlipFlopScreenTexts.ON, ON);
    add.accept(FlipFlopScreenTexts.OFF, OFF);
    add.accept(ModuleMessages.FLIP_FLOP_ON, TURNED_ON);
    add.accept(ModuleMessages.FLIP_FLOP_OFF, TURNED_OFF);
  }
}
