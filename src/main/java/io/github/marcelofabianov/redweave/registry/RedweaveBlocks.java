package io.github.marcelofabianov.redweave.registry;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.NetworkControllerBlock;
import io.github.marcelofabianov.redweave.block.NetworkTerminalBlock;
import io.github.marcelofabianov.redweave.block.SignalInverterBlock;
import io.github.marcelofabianov.redweave.block.SignalNodeBlock;
import io.github.marcelofabianov.redweave.block.SignalTimerBlock;
import io.github.marcelofabianov.redweave.block.WeatherDetectorBlock;
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
  private static final String SIGNAL_NODE_ID = "signal_node";
  private static final String NETWORK_TERMINAL_ID = "network_terminal";
  private static final String SIGNAL_TIMER_ID = "signal_timer";
  private static final String SIGNAL_INVERTER_ID = "signal_inverter";
  private static final String WEATHER_DETECTOR_ID = "weather_detector";

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

  /** A plate without collision that pistons and explosions break, like a button (answer 74). */
  public static final DeferredBlock<SignalNodeBlock> SIGNAL_NODE =
      BLOCKS.register(
          SIGNAL_NODE_ID,
          () ->
              new SignalNodeBlock(
                  BlockBehaviour.Properties.of()
                      .mapColor(MapColor.DEEPSLATE)
                      .sound(SoundType.COPPER)
                      .noCollission()
                      .strength(NODE_HARDNESS)
                      .pushReaction(PushReaction.DESTROY)));

  /** A plate like the node: no collision, as fragile, broken by pistons (answers 394 and 422). */
  public static final DeferredBlock<SignalTimerBlock> SIGNAL_TIMER =
      BLOCKS.register(
          SIGNAL_TIMER_ID,
          () ->
              new SignalTimerBlock(
                  BlockBehaviour.Properties.of()
                      .mapColor(MapColor.DEEPSLATE)
                      .sound(SoundType.COPPER)
                      .noCollission()
                      .strength(NODE_HARDNESS)
                      .pushReaction(PushReaction.DESTROY)));

  /** A module plate, as the Signal Timer (signal-inverter.md). */
  public static final DeferredBlock<SignalInverterBlock> SIGNAL_INVERTER =
      BLOCKS.register(
          SIGNAL_INVERTER_ID,
          () ->
              new SignalInverterBlock(
                  BlockBehaviour.Properties.of()
                      .mapColor(MapColor.DEEPSLATE)
                      .sound(SoundType.COPPER)
                      .noCollission()
                      .strength(NODE_HARDNESS)
                      .pushReaction(PushReaction.DESTROY)));

  /** A module plate, as the Signal Timer (weather-detector.md). */
  public static final DeferredBlock<WeatherDetectorBlock> WEATHER_DETECTOR =
      BLOCKS.register(
          WEATHER_DETECTOR_ID,
          () ->
              new WeatherDetectorBlock(
                  BlockBehaviour.Properties.of()
                      .mapColor(MapColor.DEEPSLATE)
                      .sound(SoundType.COPPER)
                      .noCollission()
                      .strength(NODE_HARDNESS)
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
