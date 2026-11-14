package io.github.marcelofabianov.redweave.registry;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.menu.ControllerMenu;
import io.github.marcelofabianov.redweave.menu.NodeMenu;
import io.github.marcelofabianov.redweave.menu.RemoteLinkMenu;
import io.github.marcelofabianov.redweave.menu.TerminalMenu;
import io.github.marcelofabianov.redweave.menu.TimerMenu;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The menus of the mod, behind its screens. */
public final class RedweaveMenus {
  private static final String NODE_ID = "signal_node";
  private static final String TIMER_ID = "signal_timer";
  private static final String CONTROLLER_ID = "network_controller";
  private static final String TERMINAL_ID = "network_terminal";
  private static final String REMOTE_LINK_ID = "remote_link";

  private static final DeferredRegister<MenuType<?>> MENUS =
      DeferredRegister.create(Registries.MENU, RedweaveMod.MOD_ID);

  public static final Supplier<MenuType<NodeMenu>> NODE =
      MENUS.register(NODE_ID, () -> IMenuTypeExtension.create(NodeMenu::new));

  public static final Supplier<MenuType<TimerMenu>> TIMER =
      MENUS.register(TIMER_ID, () -> IMenuTypeExtension.create(TimerMenu::new));

  public static final Supplier<MenuType<ControllerMenu>> CONTROLLER =
      MENUS.register(CONTROLLER_ID, () -> IMenuTypeExtension.create(ControllerMenu::new));

  public static final Supplier<MenuType<TerminalMenu>> TERMINAL =
      MENUS.register(TERMINAL_ID, () -> IMenuTypeExtension.create(TerminalMenu::new));

  public static final Supplier<MenuType<RemoteLinkMenu>> REMOTE_LINK =
      MENUS.register(REMOTE_LINK_ID, () -> IMenuTypeExtension.create(RemoteLinkMenu::new));

  private RedweaveMenus() {}

  public static void register(IEventBus modBus) {
    MENUS.register(modBus);
  }
}
