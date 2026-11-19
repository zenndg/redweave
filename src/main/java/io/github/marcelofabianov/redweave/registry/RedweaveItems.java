package io.github.marcelofabianov.redweave.registry;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.item.ConfiguratorItem;
import io.github.marcelofabianov.redweave.item.ItemSensorItem;
import io.github.marcelofabianov.redweave.item.NetworkControllerItem;
import io.github.marcelofabianov.redweave.item.NetworkTerminalItem;
import io.github.marcelofabianov.redweave.item.PartItem;
import io.github.marcelofabianov.redweave.item.PressurePlateItem;
import io.github.marcelofabianov.redweave.item.SignalCableItem;
import io.github.marcelofabianov.redweave.item.SignalRemoteItem;
import java.util.Collection;
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
  private static final String SIGNAL_RANDOMIZER_ID = "signal_randomizer";
  private static final String SIGNAL_LEVER_ID = "signal_lever";
  private static final String SIGNAL_BUTTON_ID = "signal_button";
  private static final String SIGNAL_PRESSURE_PLATE_ID = "signal_pressure_plate";
  private static final String SIGNAL_ITEM_SENSOR_ID = "signal_item_sensor";
  private static final String SIGNAL_LOCK_ID = "signal_lock";
  private static final String SIGNAL_ALARM_ID = "signal_alarm";
  private static final String SIGNAL_LAMP_ID = "signal_lamp";
  private static final String SIGNAL_FLIP_FLOP_ID = "signal_flip_flop";
  private static final String SIGNAL_CABLE_ID = "signal_cable";

  private static final DeferredRegister.Items ITEMS =
      DeferredRegister.createItems(RedweaveMod.MOD_ID);

  public static final DeferredItem<NetworkControllerItem> NETWORK_CONTROLLER =
      ITEMS.register(
          NETWORK_CONTROLLER_ID,
          () ->
              new NetworkControllerItem(
                  RedweaveBlocks.NETWORK_CONTROLLER.get(), new Item.Properties()));

  /** A part of the part space, with the ID of the old block item (answer 523). */
  public static final DeferredItem<PartItem> SIGNAL_NODE =
      ITEMS.register(SIGNAL_NODE_ID, () -> new PartItem(new Item.Properties()));

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

  /** A part of the part space that stacks and keeps no setting, with its old ID (394 and 523). */
  public static final DeferredItem<PartItem> SIGNAL_TIMER =
      ITEMS.register(SIGNAL_TIMER_ID, () -> new PartItem(new Item.Properties()));

  /** A part of the part space that stacks and keeps no setting, with its old ID (394 and 523). */
  public static final DeferredItem<PartItem> SIGNAL_INVERTER =
      ITEMS.register(SIGNAL_INVERTER_ID, () -> new PartItem(new Item.Properties()));

  /** A part of the part space that stacks and keeps no setting, with its old ID (394 and 523). */
  public static final DeferredItem<PartItem> WEATHER_DETECTOR =
      ITEMS.register(WEATHER_DETECTOR_ID, () -> new PartItem(new Item.Properties()));

  /** A part of the part space that stacks and keeps no setting, with its old ID (394 and 523). */
  public static final DeferredItem<PartItem> SIGNAL_RANDOMIZER =
      ITEMS.register(SIGNAL_RANDOMIZER_ID, () -> new PartItem(new Item.Properties()));

  /** A part of the part space that stacks and keeps no setting, as the other modules (394). */
  public static final DeferredItem<PartItem> SIGNAL_LEVER =
      ITEMS.register(SIGNAL_LEVER_ID, () -> new PartItem(new Item.Properties()));

  /** A part of the part space that stacks and keeps no setting, as the other modules (394). */
  public static final DeferredItem<PartItem> SIGNAL_BUTTON =
      ITEMS.register(SIGNAL_BUTTON_ID, () -> new PartItem(new Item.Properties()));

  /** A part that only goes on the floor, as the pressure plate of the game (551 and 611). */
  public static final DeferredItem<PressurePlateItem> SIGNAL_PRESSURE_PLATE =
      ITEMS.register(SIGNAL_PRESSURE_PLATE_ID, () -> new PressurePlateItem(new Item.Properties()));

  /** A part that only goes on a container, which it reads (answers 690 and 694). */
  public static final DeferredItem<ItemSensorItem> SIGNAL_ITEM_SENSOR =
      ITEMS.register(SIGNAL_ITEM_SENSOR_ID, () -> new ItemSensorItem(new Item.Properties()));

  /** A part of the part space that stacks and keeps no setting, as the other modules (641). */
  public static final DeferredItem<PartItem> SIGNAL_LOCK =
      ITEMS.register(SIGNAL_LOCK_ID, () -> new PartItem(new Item.Properties()));

  /** A part of the part space that stacks and keeps no setting, as the other modules (643). */
  public static final DeferredItem<PartItem> SIGNAL_ALARM =
      ITEMS.register(SIGNAL_ALARM_ID, () -> new PartItem(new Item.Properties()));

  /** A part of the part space that stacks and keeps no setting, as the Signal Node (652). */
  public static final DeferredItem<PartItem> SIGNAL_LAMP =
      ITEMS.register(SIGNAL_LAMP_ID, () -> new PartItem(new Item.Properties()));

  /** A part of the part space that stacks and keeps no setting, as the other modules (877). */
  public static final DeferredItem<PartItem> SIGNAL_FLIP_FLOP =
      ITEMS.register(SIGNAL_FLIP_FLOP_ID, () -> new PartItem(new Item.Properties()));

  /** The Signal Cable, white until dyed; cables of one color stack (answers 494 and 505). */
  public static final DeferredItem<SignalCableItem> SIGNAL_CABLE =
      ITEMS.register(SIGNAL_CABLE_ID, () -> new SignalCableItem(new Item.Properties()));

  private RedweaveItems() {}

  /** Every item of the mod, in the order they were registered. */
  public static Collection<DeferredHolder<Item, ? extends Item>> all() {
    return ITEMS.getEntries();
  }

  public static void register(IEventBus modBus) {
    ITEMS.register(modBus);
  }
}
