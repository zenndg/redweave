package io.github.marcelofabianov.redweave.registry;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.item.NetworkControllerItem;
import java.util.Collection;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The items of the mod. Their IDs are part of the save (AGENTS.md, rule 15). */
public final class RedweaveItems {
  private static final String NETWORK_CONTROLLER_ID = "network_controller";

  private static final DeferredRegister.Items ITEMS =
      DeferredRegister.createItems(RedweaveMod.MOD_ID);

  public static final DeferredItem<NetworkControllerItem> NETWORK_CONTROLLER =
      ITEMS.register(
          NETWORK_CONTROLLER_ID,
          () ->
              new NetworkControllerItem(
                  RedweaveBlocks.NETWORK_CONTROLLER.get(), new Item.Properties()));

  private RedweaveItems() {}

  /** Every item of the mod, in the order they were registered. */
  public static Collection<DeferredHolder<Item, ? extends Item>> all() {
    return ITEMS.getEntries();
  }

  public static void register(IEventBus modBus) {
    ITEMS.register(modBus);
  }
}
