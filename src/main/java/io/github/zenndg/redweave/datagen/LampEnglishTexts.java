package io.github.zenndg.redweave.datagen;

import io.github.zenndg.redweave.menu.LampScreenTexts;
import io.github.zenndg.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/** The texts of the Signal Lamp in English (lampada.md and answers 650 to 658). */
final class LampEnglishTexts {
  private static final String SIGNAL_LAMP = "Signal Lamp";
  private static final String INTENSITY = "Intensity";
  private static final String CONTROL = "Control";
  private static final String MODE = "Mode";
  private static final String HOLD = "Hold";
  private static final String PULSE = "Pulse";
  private static final String NONE = "None";
  private static final String LIGHT = "Light";

  private LampEnglishTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveItems.SIGNAL_LAMP.get().getDescriptionId(), SIGNAL_LAMP);
    add.accept(LampScreenTexts.INTENSITY, INTENSITY);
    add.accept(LampScreenTexts.CONTROL, CONTROL);
    add.accept(LampScreenTexts.MODE, MODE);
    add.accept(LampScreenTexts.HOLD, HOLD);
    add.accept(LampScreenTexts.PULSE, PULSE);
    add.accept(LampScreenTexts.NONE, NONE);
    add.accept(LampScreenTexts.LIGHT, LIGHT);
  }
}
