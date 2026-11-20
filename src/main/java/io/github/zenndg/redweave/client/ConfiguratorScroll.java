package io.github.zenndg.redweave.client;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.network.ConfiguratorScrolled;
import io.github.zenndg.redweave.registry.RedweaveItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Shift + scroll with the Configurator in the main hand switches its mode instead of the hotbar
 * slot (answers 143 and 192). The wheel exists only on the client, so it asks the server.
 */
@EventBusSubscriber(modid = RedweaveMod.MOD_ID, value = Dist.CLIENT)
public final class ConfiguratorScroll {
  private static final double STILL = 0;

  private ConfiguratorScroll() {}

  @SubscribeEvent
  public static void onScroll(InputEvent.MouseScrollingEvent event) {
    Minecraft game = Minecraft.getInstance();
    LocalPlayer player = game.player;
    double delta = ScrollDelta.of(event.getScrollDeltaX(), event.getScrollDeltaY());
    if (game.screen != null
        || player == null
        || !player.isShiftKeyDown()
        || delta == STILL
        || !player.getMainHandItem().is(RedweaveItems.CONFIGURATOR.get())) {
      return;
    }
    event.setCanceled(true);
    PacketDistributor.sendToServer(new ConfiguratorScrolled(delta < STILL));
  }
}
