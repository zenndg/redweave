package io.github.zenndg.redweave.registry;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.NetworkControllerBlockEntity;
import io.github.zenndg.redweave.block.NetworkTerminalBlockEntity;
import io.github.zenndg.redweave.block.PartSpaceBlockEntity;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The block entity types of the mod. Their IDs are part of the save (AGENTS.md, rule 15). */
public final class RedweaveBlockEntities {
  private static final String NETWORK_CONTROLLER_ID = "network_controller";
  private static final String PART_SPACE_ID = "part_space";
  private static final String NETWORK_TERMINAL_ID = "network_terminal";

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

  public static final Supplier<BlockEntityType<NetworkTerminalBlockEntity>> NETWORK_TERMINAL =
      BLOCK_ENTITIES.register(
          NETWORK_TERMINAL_ID,
          () ->
              BlockEntityType.Builder.of(
                      NetworkTerminalBlockEntity::new, RedweaveBlocks.NETWORK_TERMINAL.get())
                  .build(null));

  public static final Supplier<BlockEntityType<PartSpaceBlockEntity>> PART_SPACE =
      BLOCK_ENTITIES.register(
          PART_SPACE_ID,
          () ->
              BlockEntityType.Builder.of(PartSpaceBlockEntity::new, RedweaveBlocks.PART_SPACE.get())
                  .build(null));

  private RedweaveBlockEntities() {}

  public static void register(IEventBus modBus) {
    BLOCK_ENTITIES.register(modBus);
  }
}
