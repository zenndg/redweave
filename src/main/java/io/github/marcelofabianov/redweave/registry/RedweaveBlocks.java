package io.github.marcelofabianov.redweave.registry;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.NetworkControllerBlock;
import java.util.Collection;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The blocks of the mod. Their IDs are part of the save (AGENTS.md, rule 15). */
public final class RedweaveBlocks {
  private static final String NETWORK_CONTROLLER_ID = "network_controller";

  /** As long to break as a furnace. */
  private static final float CONTROLLER_HARDNESS = 3.5F;

  /** The resistance of bedrock: explosions do not destroy the controller (answer 30). */
  private static final float CONTROLLER_EXPLOSION_RESISTANCE = 3_600_000.0F;

  private static final DeferredRegister.Blocks BLOCKS =
      DeferredRegister.createBlocks(RedweaveMod.MOD_ID);

  public static final DeferredBlock<NetworkControllerBlock> NETWORK_CONTROLLER =
      BLOCKS.register(
          NETWORK_CONTROLLER_ID,
          () ->
              new NetworkControllerBlock(
                  BlockBehaviour.Properties.of()
                      .mapColor(MapColor.DEEPSLATE)
                      .sound(SoundType.POLISHED_DEEPSLATE)
                      .strength(CONTROLLER_HARDNESS, CONTROLLER_EXPLOSION_RESISTANCE)));

  private RedweaveBlocks() {}

  /** Every block of the mod, for the data generation. */
  public static Collection<DeferredHolder<Block, ? extends Block>> all() {
    return BLOCKS.getEntries();
  }

  public static void register(IEventBus modBus) {
    BLOCKS.register(modBus);
  }
}
