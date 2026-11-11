package io.github.marcelofabianov.redweave.network;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.menu.NodeScreenChoices;
import io.github.marcelofabianov.redweave.menu.NodeScreenNames;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/**
 * The packets of the mod. The actions of the screens go as button clicks; the packets of the client
 * are the Shift + scroll of the Configurator and the name of a node, which do not fit a button,
 * both checked on the server (rule 17).
 */
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
            (names, context) -> NodeScreenNames.receive(names, context.player()))
        .playToClient(
            NodeScreenChoices.TYPE,
            NodeScreenChoices.STREAM_CODEC,
            (choices, context) -> NodeScreenChoices.receive(choices, context.player()))
        .playToServer(
            NodeRenamed.TYPE,
            NodeRenamed.STREAM_CODEC,
            (renamed, context) -> NodeRenamed.receive(renamed, context.player()))
        .playToServer(
            ConfiguratorScrolled.TYPE,
            ConfiguratorScrolled.STREAM_CODEC,
            (scrolled, context) -> ConfiguratorScrolled.receive(scrolled, context.player()));
  }
}
