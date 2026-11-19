package io.github.zenndg.redweave.registry;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.NetworkControllerBlock;
import io.github.zenndg.redweave.block.NetworkTerminalBlock;
import io.github.zenndg.redweave.block.PartSpaceBlock;
import java.util.Collection;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The blocks of the mod. Their IDs are part of the save (AGENTS.md, rule 15). */
public final class RedweaveBlocks {
  private static final String NETWORK_CONTROLLER_ID = "network_controller";
  private static final String PART_SPACE_ID = "part_space";
  private static final String NETWORK_TERMINAL_ID = "network_terminal";

  /** As fragile as a button. */
  private static final float NODE_HARDNESS = 0.5F;

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

  /** Protected as the controller: as long to break and not destroyed by explosions (137). */
  public static final DeferredBlock<NetworkTerminalBlock> NETWORK_TERMINAL =
      BLOCKS.register(
          NETWORK_TERMINAL_ID,
          () ->
              new NetworkTerminalBlock(
                  BlockBehaviour.Properties.of()
                      .mapColor(MapColor.DEEPSLATE)
                      .sound(SoundType.POLISHED_DEEPSLATE)
                      .strength(CONTROLLER_HARDNESS, CONTROLLER_EXPLOSION_RESISTANCE)));

  /**
   * The part space, which holds the Signal Node, the Signal Modules and, from the next step, the
   * cables: no collision, broken by pistons and explosions like a button, and no loot table, as
   * each part drops its own item (answers 74, 517 and 539).
   */
  public static final DeferredBlock<PartSpaceBlock> PART_SPACE =
      BLOCKS.register(
          PART_SPACE_ID,
          () ->
              new PartSpaceBlock(
                  BlockBehaviour.Properties.of()
                      .mapColor(MapColor.DEEPSLATE)
                      .sound(SoundType.COPPER)
                      .noCollission()
                      .noLootTable()
                      .strength(NODE_HARDNESS)
                      .lightLevel(PartSpaceBlock::lightOf)
                      .pushReaction(PushReaction.DESTROY)));

  private RedweaveBlocks() {}

  /** Every block of the mod, for the data generation. */
  public static Collection<DeferredHolder<Block, ? extends Block>> all() {
    return BLOCKS.getEntries();
  }

  public static void register(IEventBus modBus) {
    BLOCKS.register(modBus);
  }
}
