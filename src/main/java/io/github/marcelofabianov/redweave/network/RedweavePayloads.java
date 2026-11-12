package io.github.marcelofabianov.redweave.network;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.menu.ControllerScreenLog;
import io.github.marcelofabianov.redweave.menu.ControllerScreenRows;
import io.github.marcelofabianov.redweave.menu.ControllerScreenSignals;
import io.github.marcelofabianov.redweave.menu.NodeScreenChoices;
import io.github.marcelofabianov.redweave.menu.NodeScreenNames;
import io.github.marcelofabianov.redweave.menu.TerminalScreenRows;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * The packets of the mod. The actions of the screens go as button clicks; the packets of the client
 * are the Shift + scroll of the Configurator and the names typed on the screens, which do not fit a
 * button, all checked on the server (rule 17).
 */
@EventBusSubscriber(modid = RedweaveMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class RedweavePayloads {
  private static final String VERSION = "1";

  private RedweavePayloads() {}

  @SubscribeEvent
  public static void register(RegisterPayloadHandlersEvent event) {
    PayloadRegistrar registrar = event.registrar(VERSION);
    registerScreens(registrar);
    registerWorld(registrar);
    registerActions(registrar);
  }

  /** What the server sends to the open screens. */
  private static void registerScreens(PayloadRegistrar registrar) {
    registrar
        .playToClient(
            NodeScreenNames.TYPE,
            NodeScreenNames.STREAM_CODEC,
            (names, context) -> NodeScreenNames.receive(names, context.player()))
        .playToClient(
            NodeScreenChoices.TYPE,
            NodeScreenChoices.STREAM_CODEC,
            (choices, context) -> NodeScreenChoices.receive(choices, context.player()))
        .playToClient(
            ControllerScreenRows.TYPE,
            ControllerScreenRows.STREAM_CODEC,
            (rows, context) -> ControllerScreenRows.receive(rows, context.player()))
        .playToClient(
            ControllerScreenSignals.TYPE,
            ControllerScreenSignals.STREAM_CODEC,
            (signals, context) -> ControllerScreenSignals.receive(signals, context.player()))
        .playToClient(
            ControllerScreenLog.TYPE,
            ControllerScreenLog.STREAM_CODEC,
            (log, context) -> ControllerScreenLog.receive(log, context.player()))
        .playToClient(
            TerminalScreenRows.TYPE,
            TerminalScreenRows.STREAM_CODEC,
            (rows, context) -> TerminalScreenRows.receive(rows, context.player()));
  }

  /** What the server sends about the world, as the labels over the nodes (answer 269). */
  private static void registerWorld(PayloadRegistrar registrar) {
    registrar.playToClient(
        NodeLabelsUpdate.TYPE,
        NodeLabelsUpdate.STREAM_CODEC,
        (update, context) -> ReceivedLabels.accept(update));
  }

  /** What the client asks, checked on the server. */
  private static void registerActions(PayloadRegistrar registrar) {
    registrar
        .playToServer(
            TerminalRenamed.TYPE,
            TerminalRenamed.STREAM_CODEC,
            (renamed, context) -> TerminalRenamed.receive(renamed, context.player()))
        .playToServer(
            ControllerRenamed.TYPE,
            ControllerRenamed.STREAM_CODEC,
            (renamed, context) -> ControllerRenamed.receive(renamed, context.player()))
        .playToServer(
            SlotRenamed.TYPE,
            SlotRenamed.STREAM_CODEC,
            (renamed, context) -> SlotRenamed.receive(renamed, context.player()))
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
