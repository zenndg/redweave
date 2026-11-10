package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.Node;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

/**
 * The end of each tick: the networks waiting for it are evaluated once, the nodes whose output
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
    List<Network> evaluated = server.pendingNetworks();
    server.evaluateNetworks().evaluate().stream()
        .map(change -> change.face().node())
        .distinct()
        .forEach(node -> emit(minecraft, server, node));
    evaluated.forEach(network -> network.nodes().forEach(node -> look(minecraft, server, node)));
  }

  /** A node in an unloaded chunk is skipped; it asks for its signal when it loads again. */
  private static void emit(MinecraftServer minecraft, RedweaveServer server, NodeId id) {
    Optional<Network> network = server.networkOfNode(id);
    network
        .flatMap(found -> found.node(id))
        .flatMap(node -> locate(minecraft, node))
        .ifPresent(
            found -> NodeSignals.emit(found.level(), found.pos(), network.get().emission(id)));
  }

  private static void look(MinecraftServer minecraft, RedweaveServer server, Node node) {
    locate(minecraft, node)
        .ifPresent(found -> NodeLook.refresh(server, found.level(), found.pos()));
  }

  /** The block of a node, when its dimension exists and its chunk is loaded (rule 6). */
  private static Optional<Located> locate(MinecraftServer minecraft, Node node) {
    BlockLocation support = node.position().block();
    BlockPos pos =
        new BlockPos(support.x(), support.y(), support.z())
            .relative(Faces.toDirection(node.position().face()));
    return levelOf(minecraft, support.dimension())
        .filter(level -> level.isLoaded(pos))
        .map(level -> new Located(level, pos));
  }

  private static Optional<ServerLevel> levelOf(MinecraftServer minecraft, String dimension) {
    ResourceLocation id = ResourceLocation.tryParse(dimension);
    if (id == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(minecraft.getLevel(ResourceKey.create(Registries.DIMENSION, id)));
  }

  private record Located(ServerLevel level, BlockPos pos) {}
}
