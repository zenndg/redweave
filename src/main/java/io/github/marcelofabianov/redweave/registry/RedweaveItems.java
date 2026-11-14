package io.github.marcelofabianov.redweave.registry;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.item.ConfiguratorItem;
import io.github.marcelofabianov.redweave.item.NetworkControllerItem;
import io.github.marcelofabianov.redweave.item.NetworkTerminalItem;
import io.github.marcelofabianov.redweave.item.SignalRemoteItem;
import java.util.Collection;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The items of the mod. Their IDs are part of the save (AGENTS.md, rule 15). */
public final class RedweaveItems {
  private static final String NETWORK_CONTROLLER_ID = "network_controller";
  private static final String SIGNAL_NODE_ID = "signal_node";
  private static final String CONFIGURATOR_ID = "configurator";
  private static final String NETWORK_TERMINAL_ID = "network_terminal";
  private static final String SIGNAL_REMOTE_ID = "signal_remote";
  private static final String SIGNAL_TIMER_ID = "signal_timer";
  private static final String SIGNAL_INVERTER_ID = "signal_inverter";
  private static final String WEATHER_DETECTOR_ID = "weather_detector";

  private static final DeferredRegister.Items ITEMS =
      DeferredRegister.createItems(RedweaveMod.MOD_ID);

  public static final DeferredItem<NetworkControllerItem> NETWORK_CONTROLLER =
      ITEMS.register(
          NETWORK_CONTROLLER_ID,
          () ->
              new NetworkControllerItem(
                  RedweaveBlocks.NETWORK_CONTROLLER.get(), new Item.Properties()));

  public static final DeferredItem<BlockItem> SIGNAL_NODE =
      ITEMS.registerSimpleBlockItem(SIGNAL_NODE_ID, RedweaveBlocks.SIGNAL_NODE);

  /** One per stack, because each one keeps its own network and copy (configurador.md). */
  public static final DeferredItem<ConfiguratorItem> CONFIGURATOR =
      ITEMS.register(
          CONFIGURATOR_ID, () -> new ConfiguratorItem(new Item.Properties().stacksTo(1)));

  /** One per player in the world, so the stack is the usual one (answer 137). */
  public static final DeferredItem<NetworkTerminalItem> NETWORK_TERMINAL =
      ITEMS.register(
          NETWORK_TERMINAL_ID,
          () ->
              new NetworkTerminalItem(
                  RedweaveBlocks.NETWORK_TERMINAL.get(), new Item.Properties()));

  /** One per stack: each tablet keeps its own ID, link and state (answer 371). */
  public static final DeferredItem<SignalRemoteItem> SIGNAL_REMOTE =
      ITEMS.register(
          SIGNAL_REMOTE_ID, () -> new SignalRemoteItem(new Item.Properties().stacksTo(1)));

  /** A plain item that stacks and keeps no setting, as the node (answer 394). */
  public static final DeferredItem<BlockItem> SIGNAL_TIMER =
      ITEMS.registerSimpleBlockItem(SIGNAL_TIMER_ID, RedweaveBlocks.SIGNAL_TIMER);

  /** A plain item that stacks and keeps no setting, as the timer (answer 394). */
  public static final DeferredItem<BlockItem> SIGNAL_INVERTER =
      ITEMS.registerSimpleBlockItem(SIGNAL_INVERTER_ID, RedweaveBlocks.SIGNAL_INVERTER);

  /** A plain item that stacks and keeps no setting, as the other modules (answer 394). */
  public static final DeferredItem<BlockItem> WEATHER_DETECTOR =
      ITEMS.registerSimpleBlockItem(WEATHER_DETECTOR_ID, RedweaveBlocks.WEATHER_DETECTOR);

  private RedweaveItems() {}

  /** Every item of the mod, in the order they were registered. */
  public static Collection<DeferredHolder<Item, ? extends Item>> all() {
    return ITEMS.getEntries();
  }

  public static void register(IEventBus modBus) {
    ITEMS.register(modBus);
  }
}
