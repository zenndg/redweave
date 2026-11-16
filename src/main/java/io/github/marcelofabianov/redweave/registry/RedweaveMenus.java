package io.github.marcelofabianov.redweave.registry;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.menu.ButtonMenu;
import io.github.marcelofabianov.redweave.menu.ControllerMenu;
import io.github.marcelofabianov.redweave.menu.DetectorMenu;
import io.github.marcelofabianov.redweave.menu.InverterMenu;
import io.github.marcelofabianov.redweave.menu.ItemSensorMenu;
import io.github.marcelofabianov.redweave.menu.LeverMenu;
import io.github.marcelofabianov.redweave.menu.NodeMenu;
import io.github.marcelofabianov.redweave.menu.PressurePlateMenu;
import io.github.marcelofabianov.redweave.menu.RandomizerMenu;
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
  private static final String INVERTER_ID = "signal_inverter";
  private static final String DETECTOR_ID = "weather_detector";
  private static final String RANDOMIZER_ID = "signal_randomizer";
  private static final String LEVER_ID = "signal_lever";
  private static final String BUTTON_ID = "signal_button";
  private static final String PRESSURE_PLATE_ID = "signal_pressure_plate";
  private static final String ITEM_SENSOR_ID = "signal_item_sensor";
  private static final String CONTROLLER_ID = "network_controller";
  private static final String TERMINAL_ID = "network_terminal";
  private static final String REMOTE_LINK_ID = "remote_link";

  private static final DeferredRegister<MenuType<?>> MENUS =
      DeferredRegister.create(Registries.MENU, RedweaveMod.MOD_ID);

  public static final Supplier<MenuType<NodeMenu>> NODE =
      MENUS.register(NODE_ID, () -> IMenuTypeExtension.create(NodeMenu::new));

  public static final Supplier<MenuType<TimerMenu>> TIMER =
      MENUS.register(TIMER_ID, () -> IMenuTypeExtension.create(TimerMenu::new));

  public static final Supplier<MenuType<InverterMenu>> INVERTER =
      MENUS.register(INVERTER_ID, () -> IMenuTypeExtension.create(InverterMenu::new));

  public static final Supplier<MenuType<DetectorMenu>> DETECTOR =
      MENUS.register(DETECTOR_ID, () -> IMenuTypeExtension.create(DetectorMenu::new));

  public static final Supplier<MenuType<RandomizerMenu>> RANDOMIZER =
      MENUS.register(RANDOMIZER_ID, () -> IMenuTypeExtension.create(RandomizerMenu::new));

  public static final Supplier<MenuType<LeverMenu>> LEVER =
      MENUS.register(LEVER_ID, () -> IMenuTypeExtension.create(LeverMenu::new));

  public static final Supplier<MenuType<ButtonMenu>> BUTTON =
      MENUS.register(BUTTON_ID, () -> IMenuTypeExtension.create(ButtonMenu::new));

  public static final Supplier<MenuType<PressurePlateMenu>> PRESSURE_PLATE =
      MENUS.register(PRESSURE_PLATE_ID, () -> IMenuTypeExtension.create(PressurePlateMenu::new));

  public static final Supplier<MenuType<ItemSensorMenu>> ITEM_SENSOR =
      MENUS.register(ITEM_SENSOR_ID, () -> IMenuTypeExtension.create(ItemSensorMenu::new));

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
