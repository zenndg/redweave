package io.github.marcelofabianov.redweave.datagen;

import java.util.function.BiConsumer;

/** The Brazilian Portuguese texts, with the same keys as English. */
final class PortugueseTexts {
  private static final String PITCH = "Teça a sua redstone.";
  private static final String NETWORKS = " Redes de sinal sem fio,";
  private static final String FEATURES = " canais programáveis e chips de lógica,";
  private static final String SCREENS = " com telas fáceis de aprender e bonitas de usar.";
  private static final String MOD_DESCRIPTION = PITCH + NETWORKS + FEATURES + SCREENS;

  private PortugueseTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveLanguageProvider.MOD_DESCRIPTION_KEY, MOD_DESCRIPTION);
  }
}
