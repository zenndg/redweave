package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.menu.LampScreenTexts;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.function.BiConsumer;

/** The texts of the Signal Lamp in Portuguese (lampada.md and answers 650 to 658). */
final class LampPortugueseTexts {
  private static final String SIGNAL_LAMP = "Lâmpada de Sinal";
  private static final String INTENSITY = "Intensidade";
  private static final String CONTROL = "Controle";
  private static final String MODE = "Modo";
  private static final String HOLD = "Manter";
  private static final String PULSE = "Pulso";
  private static final String NONE = "Nenhum";
  private static final String LIGHT = "Luz";

  private LampPortugueseTexts() {}

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
