package io.github.marcelofabianov.redweave.datagen;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The languages of the mod. English is the base, and every other language has the same keys, with
 * its own texts (AGENTS.md, rule 1).
 */
enum Language {
  EN_US(Locales.ENGLISH, EnglishTexts::addTo),
  PT_BR(Locales.PORTUGUESE, PortugueseTexts::addTo);

  private final String locale;
  private final Consumer<BiConsumer<String, String>> texts;

  Language(String locale, Consumer<BiConsumer<String, String>> texts) {
    this.locale = locale;
    this.texts = texts;
  }

  String locale() {
    return locale;
  }

  void addTexts(BiConsumer<String, String> add) {
    texts.accept(add);
  }

  /** The codes of the game for each language. */
  private static final class Locales {
    private static final String ENGLISH = "en_us";
    private static final String PORTUGUESE = "pt_br";
  }
}
