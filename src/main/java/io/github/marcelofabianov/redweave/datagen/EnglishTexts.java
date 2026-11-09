package io.github.marcelofabianov.redweave.datagen;

import java.util.function.BiConsumer;

/** The English texts, the base of every language. */
final class EnglishTexts {
  private static final String PITCH = "Weave your redstone.";
  private static final String NETWORKS = " Wireless signal networks,";
  private static final String FEATURES = " programmable channels and logic chips,";
  private static final String SCREENS = " with screens that are easy to learn";
  private static final String BEAUTY = " and beautiful to use.";

  /** The same text as the description of the neoforge.mods.toml, checked by a test. */
  private static final String MOD_DESCRIPTION = PITCH + NETWORKS + FEATURES + SCREENS + BEAUTY;

  private EnglishTexts() {}

  static void addTo(BiConsumer<String, String> add) {
    add.accept(RedweaveLanguageProvider.MOD_DESCRIPTION_KEY, MOD_DESCRIPTION);
  }
}
