package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.RedweaveMod;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

/** One language file of the mod. English is the base, and every language has the same keys. */
final class RedweaveLanguageProvider extends LanguageProvider {
  /** The mod list reads this key before the description of the neoforge.mods.toml. */
  static final String MOD_DESCRIPTION_KEY = "fml.menu.mods.info.description." + RedweaveMod.MOD_ID;

  private final Language language;

  RedweaveLanguageProvider(PackOutput output, Language language) {
    super(output, RedweaveMod.MOD_ID, language.locale());
    this.language = language;
  }

  @Override
  protected void addTranslations() {
    language.addTexts(this::add);
  }
}
