package io.github.zenndg.redweave.datagen;

import io.github.zenndg.redweave.block.ModuleMessages;
import io.github.zenndg.redweave.menu.ButtonScreenTexts;
import io.github.zenndg.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/** The texts of the Signal Button in English (botao.md and answers 550, 556, 593 and 595). */
final class ButtonEnglishTexts {
  private static final String SIGNAL_BUTTON = "Signal Button";
  private static final String ON = "On";
  private static final String OFF = "Off";
  private static final String ON_TIME = "On time";
  private static final String PRESSED = "%s: Pressed";

  private ButtonEnglishTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveItems.SIGNAL_BUTTON.get().getDescriptionId(), SIGNAL_BUTTON);
    add.accept(ButtonScreenTexts.ON, ON);
    add.accept(ButtonScreenTexts.OFF, OFF);
    add.accept(ButtonScreenTexts.ON_TIME, ON_TIME);
    add.accept(ModuleMessages.BUTTON_PRESSED, PRESSED);
  }
}
