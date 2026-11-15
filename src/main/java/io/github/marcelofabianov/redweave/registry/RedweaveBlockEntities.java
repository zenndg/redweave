package io.github.marcelofabianov.redweave.registry;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.NetworkControllerBlockEntity;
import io.github.marcelofabianov.redweave.block.NetworkTerminalBlockEntity;
import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.block.SignalInverterBlockEntity;
import io.github.marcelofabianov.redweave.block.SignalRandomizerBlockEntity;
import io.github.marcelofabianov.redweave.block.SignalTimerBlockEntity;
import io.github.marcelofabianov.redweave.block.WeatherDetectorBlockEntity;
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
  private static final String SIGNAL_TIMER_ID = "signal_timer";
  private static final String SIGNAL_INVERTER_ID = "signal_inverter";
  private static final String WEATHER_DETECTOR_ID = "weather_detector";
  private static final String SIGNAL_RANDOMIZER_ID = "signal_randomizer";

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

  public static final Supplier<BlockEntityType<SignalTimerBlockEntity>> SIGNAL_TIMER =
      BLOCK_ENTITIES.register(
          SIGNAL_TIMER_ID,
          () ->
              BlockEntityType.Builder.of(
                      SignalTimerBlockEntity::new, RedweaveBlocks.SIGNAL_TIMER.get())
                  .build(null));

  public static final Supplier<BlockEntityType<SignalInverterBlockEntity>> SIGNAL_INVERTER =
      BLOCK_ENTITIES.register(
          SIGNAL_INVERTER_ID,
          () ->
              BlockEntityType.Builder.of(
                      SignalInverterBlockEntity::new, RedweaveBlocks.SIGNAL_INVERTER.get())
                  .build(null));

  public static final Supplier<BlockEntityType<WeatherDetectorBlockEntity>> WEATHER_DETECTOR =
      BLOCK_ENTITIES.register(
          WEATHER_DETECTOR_ID,
          () ->
              BlockEntityType.Builder.of(
                      WeatherDetectorBlockEntity::new, RedweaveBlocks.WEATHER_DETECTOR.get())
                  .build(null));

  public static final Supplier<BlockEntityType<SignalRandomizerBlockEntity>> SIGNAL_RANDOMIZER =
      BLOCK_ENTITIES.register(
          SIGNAL_RANDOMIZER_ID,
          () ->
              BlockEntityType.Builder.of(
                      SignalRandomizerBlockEntity::new, RedweaveBlocks.SIGNAL_RANDOMIZER.get())
                  .build(null));

  private RedweaveBlockEntities() {}

  public static void register(IEventBus modBus) {
    BLOCK_ENTITIES.register(modBus);
  }
}
