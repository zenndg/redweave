package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.RedweaveMod;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/** Generates the files in src/generated, which are never edited by hand (AGENTS.md, rule 26). */
@EventBusSubscriber(modid = RedweaveMod.MOD_ID)
public final class RedweaveDataGenerators {
  private RedweaveDataGenerators() {}

  @SubscribeEvent
  public static void gatherData(GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    PackOutput output = generator.getPackOutput();
    for (Language language : Language.values()) {
      generator.addProvider(event.includeClient(), new RedweaveLanguageProvider(output, language));
    }
  }
}
