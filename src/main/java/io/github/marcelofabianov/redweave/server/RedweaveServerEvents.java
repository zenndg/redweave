package io.github.marcelofabianov.redweave.server;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.config.NeoForgeNetworkLimits;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;

/** Opens and drops the state of the server with the world, after the server config loads. */
@EventBusSubscriber(modid = RedweaveMod.MOD_ID)
public final class RedweaveServerEvents {
  private RedweaveServerEvents() {}

  @SubscribeEvent
  public static void onServerStarting(ServerStartingEvent event) {
    RedweaveServer.start(new NeoForgeNetworkLimits());
  }

  @SubscribeEvent
  public static void onServerStopped(ServerStoppedEvent event) {
    RedweaveServer.stop();
  }
}
