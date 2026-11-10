package io.github.marcelofabianov.redweave.registry;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.menu.NodeMenu;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The menus of the mod, behind its screens. */
public final class RedweaveMenus {
  private static final String NODE_ID = "signal_node";

  private static final DeferredRegister<MenuType<?>> MENUS =
      DeferredRegister.create(Registries.MENU, RedweaveMod.MOD_ID);

  public static final Supplier<MenuType<NodeMenu>> NODE =
      MENUS.register(NODE_ID, () -> IMenuTypeExtension.create(NodeMenu::new));

  private RedweaveMenus() {}

  public static void register(IEventBus modBus) {
    MENUS.register(modBus);
  }
}
