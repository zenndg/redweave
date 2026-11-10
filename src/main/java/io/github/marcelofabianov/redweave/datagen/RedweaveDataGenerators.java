package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.RedweaveMod;
import java.util.List;
import java.util.Set;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
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
    generator.addProvider(
        event.includeClient(), new RedweaveBlockStates(output, event.getExistingFileHelper()));
    generator.addProvider(
        event.includeServer(),
        new LootTableProvider(
            output,
            Set.of(),
            List.of(
                new LootTableProvider.SubProviderEntry(
                    RedweaveBlockLoot::new, LootContextParamSets.BLOCK)),
            event.getLookupProvider()));
  }
}
