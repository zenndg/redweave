package io.github.zenndg.redweave.server;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.config.NeoForgeNetworkLimits;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

/**
 * Opens the state of the server with the world, after the server config and the levels load, and
 * drops it when the world closes.
 */
@EventBusSubscriber(modid = RedweaveMod.MOD_ID)
public final class RedweaveServerEvents {
  private RedweaveServerEvents() {}

  @SubscribeEvent
  public static void onServerStarting(ServerStartingEvent event) {
    NeoForgeNetworkLimits limits = new NeoForgeNetworkLimits();
    RedweaveServer.start(limits, SavedDataNetworks.of(event.getServer(), limits));
  }

  @SubscribeEvent
  public static void onServerStopped(ServerStoppedEvent event) {
    RedweaveServer.stop();
  }
}
