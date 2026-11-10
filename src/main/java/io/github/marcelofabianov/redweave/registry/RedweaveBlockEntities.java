package io.github.marcelofabianov.redweave.registry;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.NetworkControllerBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The block entity types of the mod. Their IDs are part of the save (AGENTS.md, rule 15). */
public final class RedweaveBlockEntities {
  private static final String NETWORK_CONTROLLER_ID = "network_controller";

  private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
      DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, RedweaveMod.MOD_ID);

  /** The data fixer type is null because the mod has no data fixes of the game. */
  public static final Supplier<BlockEntityType<NetworkControllerBlockEntity>> NETWORK_CONTROLLER =
      BLOCK_ENTITIES.register(
          NETWORK_CONTROLLER_ID,
          () ->
              BlockEntityType.Builder.of(
                      NetworkControllerBlockEntity::new, RedweaveBlocks.NETWORK_CONTROLLER.get())
                  .build(null));

  private RedweaveBlockEntities() {}

  public static void register(IEventBus modBus) {
    BLOCK_ENTITIES.register(modBus);
  }
}
