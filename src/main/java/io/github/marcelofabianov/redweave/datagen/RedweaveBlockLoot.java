package io.github.marcelofabianov.redweave.datagen;

import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.registry.RedweaveDataComponents;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;

/** What the blocks drop. The controller hands its network to the item (answer 24). */
final class RedweaveBlockLoot extends BlockLootSubProvider {
  RedweaveBlockLoot(HolderLookup.Provider registries) {
    super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
  }

  @Override
  protected void generate() {
    add(
        RedweaveBlocks.NETWORK_CONTROLLER.get(),
        createSingleItemTable(RedweaveBlocks.NETWORK_CONTROLLER.get())
            .apply(
                CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                    .include(RedweaveDataComponents.NETWORK.get())));
    dropSelf(RedweaveBlocks.SIGNAL_NODE.get());
    dropSelf(RedweaveBlocks.NETWORK_TERMINAL.get());
    dropSelf(RedweaveBlocks.SIGNAL_TIMER.get());
  }

  @Override
  protected Iterable<Block> getKnownBlocks() {
    return RedweaveBlocks.all().stream().map(Holder::value).map(Block.class::cast).toList();
  }
}
