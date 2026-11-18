package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.Node;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.item.RemoteTicks;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.List;
import java.util.Optional;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * The end of each tick: the Signal Remotes and the Signal Modules that count reach their networks
 * (answers 355 and 387), the networks waiting for it are evaluated once, the nodes whose output
 * changed emit again, and the nodes of those networks show their state, only in loaded chunks
 * (rules 6 and 18, no-de-sinal.md and answers 86 and 87).
 */
@EventBusSubscriber(modid = RedweaveMod.MOD_ID)
public final class NetworkTicks {
  private NetworkTicks() {}

  @SubscribeEvent
  public static void onServerTick(ServerTickEvent.Post event) {
    RedweaveServer.running().ifPresent(server -> tick(event.getServer(), server));
  }

  private static void tick(MinecraftServer minecraft, RedweaveServer server) {
    server.remotes().apply().apply(RemoteTicks.sightings(minecraft, server));
    server.modules().apply().apply(server.modules().sightings().drain());
    List<Network> evaluated = server.pendingNetworks();
    server.evaluateNetworks().evaluate().stream()
        .map(change -> change.face().node())
        .distinct()
        .forEach(node -> emit(minecraft, server, node));
    evaluated.forEach(network -> network.nodes().forEach(node -> look(minecraft, server, node)));
    minecraft
        .getPlayerList()
        .getPlayers()
        .forEach(player ->
            server
                .labelWatch()
                .update(player)
                .ifPresent(update -> PacketDistributor.sendToPlayer(player, update)));
  }

  @SubscribeEvent
  public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
    RedweaveServer.running()
        .ifPresent(server -> server.labelWatch().forget(event.getEntity().getUUID()));
  }

  /** A node in an unloaded chunk is skipped; it asks for its signal when it loads again. */
  private static void emit(MinecraftServer minecraft, RedweaveServer server, NodeId id) {
    Optional<Network> network = server.networkOfNode(id);
    network
        .flatMap(found -> found.node(id))
        .flatMap(node -> NodeBlocks.locate(minecraft, node.position()))
        .ifPresent(found ->
            NodeSignals.emit(found.level(), found.spot(), server.nodeOutputs().emit().of(id)));
  }

  private static void look(MinecraftServer minecraft, RedweaveServer server, Node node) {
    NodeBlocks.locate(minecraft, node.position())
        .ifPresent(found -> {
          NodeSignals.silence(server, found.level(), found.spot());
          NodeLook.refresh(server, found.level(), found.spot());
        });
  }
}
