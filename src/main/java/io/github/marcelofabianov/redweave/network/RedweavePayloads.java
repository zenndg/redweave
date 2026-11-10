package io.github.marcelofabianov.redweave.network;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.menu.NodeScreenNames;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** The packets of the mod. Only the server sends them; the actions go as button clicks. */
@EventBusSubscriber(modid = RedweaveMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class RedweavePayloads {
  private static final String VERSION = "1";

  private RedweavePayloads() {}

  @SubscribeEvent
  public static void register(RegisterPayloadHandlersEvent event) {
    event
        .registrar(VERSION)
        .playToClient(
            NodeScreenNames.TYPE,
            NodeScreenNames.STREAM_CODEC,
            (names, context) -> NodeScreenNames.receive(names, context.player()));
  }
}
