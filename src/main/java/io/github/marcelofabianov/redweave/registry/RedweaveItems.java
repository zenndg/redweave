package io.github.marcelofabianov.redweave.registry;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.item.ConfiguratorItem;
import io.github.marcelofabianov.redweave.item.LabelItem;
import io.github.marcelofabianov.redweave.item.NetworkControllerItem;
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
  private static final String LABEL_ID = "label";
  private static final int LABEL_STACK = 16;

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

  /** Up to 16, and written ones only join with the same copy and name (answer 115). */
  public static final DeferredItem<LabelItem> LABEL =
      ITEMS.register(LABEL_ID, () -> new LabelItem(new Item.Properties().stacksTo(LABEL_STACK)));

  private RedweaveItems() {}

  /** Every item of the mod, in the order they were registered. */
  public static Collection<DeferredHolder<Item, ? extends Item>> all() {
    return ITEMS.getEntries();
  }

  public static void register(IEventBus modBus) {
    ITEMS.register(modBus);
  }
}
