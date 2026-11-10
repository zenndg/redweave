package io.github.marcelofabianov.redweave;

import io.github.marcelofabianov.redweave.config.RedweaveConfig;
import io.github.marcelofabianov.redweave.registry.RedweaveBlockEntities;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.registry.RedweaveCreativeTabs;
import io.github.marcelofabianov.redweave.registry.RedweaveDataComponents;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.registry.RedweaveMenus;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

/** Entry point of the mod: it only wires the registries and the config. */
@Mod(RedweaveMod.MOD_ID)
public final class RedweaveMod {
  public static final String MOD_ID = "redweave";

  public RedweaveMod(IEventBus modBus, ModContainer container) {
    RedweaveDataComponents.register(modBus);
    RedweaveBlocks.register(modBus);
    RedweaveItems.register(modBus);
    RedweaveBlockEntities.register(modBus);
    RedweaveCreativeTabs.register(modBus);
    RedweaveMenus.register(modBus);
    container.registerConfig(ModConfig.Type.SERVER, RedweaveConfig.SPEC);
  }
}
