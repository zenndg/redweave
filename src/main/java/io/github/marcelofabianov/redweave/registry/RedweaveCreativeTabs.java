package io.github.marcelofabianov.redweave.registry;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.item.SignalCableItem;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/** The creative tab of the mod, which holds every item of the mod (answer 69). */
public final class RedweaveCreativeTabs {
  private static final String TAB_ID = "redweave";

  /** The translation key of the title of the tab. */
  public static final String TITLE = "itemGroup." + RedweaveMod.MOD_ID;

  private static final DeferredRegister<CreativeModeTab> TABS =
      DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RedweaveMod.MOD_ID);

  public static final Supplier<CreativeModeTab> REDWEAVE =
      TABS.register(
          TAB_ID,
          () ->
              CreativeModeTab.builder()
                  .title(Component.translatable(TITLE))
                  .icon(() -> RedweaveItems.NETWORK_CONTROLLER.get().getDefaultInstance())
                  .displayItems((parameters, output) ->
                      RedweaveItems.all()
                          .forEach(item -> {
                            if (item.get() instanceof SignalCableItem) {
                              output.acceptAll(SignalCableItem.everyColor());
                            } else {
                              output.accept(item.get());
                            }
                          }))
                  .build());

  private RedweaveCreativeTabs() {}

  public static void register(IEventBus modBus) {
    TABS.register(modBus);
  }
}
