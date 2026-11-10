package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.Node;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
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
 * The end of each tick: the networks waiting for it are evaluated once, and the nodes whose output
 * changed emit again, only in loaded chunks (rules 6 and 18, and no-de-sinal.md).
 */
@EventBusSubscriber(modid = RedweaveMod.MOD_ID)
public final class NetworkTicks {
  private NetworkTicks() {}

  @SubscribeEvent
  public static void onServerTick(ServerTickEvent.Post event) {
    RedweaveServer.running()
        .ifPresent(server ->
            server.evaluateNetworks().evaluate().stream()
                .map(change -> change.face().node())
                .distinct()
                .forEach(node -> emit(event.getServer(), server, node)));
  }

  /** A node in an unloaded chunk is skipped; it asks for its signal when it loads again. */
  private static void emit(MinecraftServer minecraft, RedweaveServer server, NodeId id) {
    Optional<Network> network = server.networkOfNode(id);
    Optional<Node> node = network.flatMap(found -> found.node(id));
    if (node.isEmpty()) {
      return;
    }
    BlockLocation support = node.get().position().block();
    Optional<ServerLevel> level = levelOf(minecraft, support.dimension());
    BlockPos pos =
        new BlockPos(support.x(), support.y(), support.z())
            .relative(Faces.toDirection(node.get().position().face()));
    if (level.isPresent() && level.get().isLoaded(pos)) {
      NodeSignals.emit(level.get(), pos, network.get().emission(id));
    }
  }

  private static Optional<ServerLevel> levelOf(MinecraftServer minecraft, String dimension) {
    ResourceLocation id = ResourceLocation.tryParse(dimension);
    if (id == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(minecraft.getLevel(ResourceKey.create(Registries.DIMENSION, id)));
  }
}
