package io.github.marcelofabianov.redweave.server;

import io.github.marcelofabianov.redweave.application.LabelChanges;
import io.github.marcelofabianov.redweave.application.NodeLabel;
import io.github.marcelofabianov.redweave.application.NodeLabels;
import io.github.marcelofabianov.redweave.block.SignalNodeBlockEntity;
import io.github.marcelofabianov.redweave.config.NeoForgeLabelRange;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.network.NodeLabelsUpdate;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * Which labels each player with the Configurator in the main hand has, to send only what changed
 * (answers 268 and 269, and rule 18). It reads only loaded chunks, so no chunk loads (rule 6), and
 * keeps nothing in the saves.
 */
public final class LabelWatch {
  private final RedweaveServer server;
  private final Map<UUID, LabelChanges> holders = new HashMap<>();

  LabelWatch(RedweaveServer server) {
    this.server = server;
  }

  /** What to send to the player now, or nothing when nothing changed. */
  public Optional<NodeLabelsUpdate> update(ServerPlayer player) {
    if (!player.getMainHandItem().is(RedweaveItems.CONFIGURATOR.get())) {
      return holders.remove(player.getUUID()) == null
          ? Optional.empty()
          : Optional.of(NodeLabelsUpdate.clear());
    }
    Map<NodeId, BlockPos> near = nodesNear(player, new NeoForgeLabelRange().getAsInt());
    Map<NodeId, NodeLabel> labels = new LinkedHashMap<>();
    near.keySet().forEach(id -> labels.put(id, NodeLabels.of(id, server.networkOfNode(id))));
    LabelChanges.Delta delta =
        holders.computeIfAbsent(player.getUUID(), id -> new LabelChanges()).next(labels);
    if (delta.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(
        new NodeLabelsUpdate(
            false,
            delta.changed().entrySet().stream()
                .map(entry ->
                    NodeLabelsUpdate.Label.of(
                        entry.getKey().value(), near.get(entry.getKey()), entry.getValue()))
                .toList(),
            delta.gone().stream().map(NodeId::value).toList()));
  }

  /** A player who left takes nothing with them. */
  public void forget(UUID player) {
    holders.remove(player);
  }

  /** The nodes in loaded chunks within the reach of the player, in a sphere. */
  private static Map<NodeId, BlockPos> nodesNear(ServerPlayer player, int range) {
    ServerLevel level = player.serverLevel();
    BlockPos center = player.blockPosition();
    Map<NodeId, BlockPos> near = new LinkedHashMap<>();
    int fromX = SectionPos.blockToSectionCoord(center.getX() - range);
    int toX = SectionPos.blockToSectionCoord(center.getX() + range);
    int fromZ = SectionPos.blockToSectionCoord(center.getZ() - range);
    int toZ = SectionPos.blockToSectionCoord(center.getZ() + range);
    long reach = (long) range * range;
    for (int x = fromX; x <= toX; x++) {
      for (int z = fromZ; z <= toZ; z++) {
        LevelChunk chunk = level.getChunkSource().getChunkNow(x, z);
        if (chunk != null) {
          addNodes(chunk, center, reach, near);
        }
      }
    }
    return near;
  }

  private static void addNodes(
      LevelChunk chunk, BlockPos center, long reach, Map<NodeId, BlockPos> near) {
    for (BlockEntity entity : chunk.getBlockEntities().values()) {
      if (entity instanceof SignalNodeBlockEntity node
          && entity.getBlockPos().distSqr(center) <= reach) {
        node.node().ifPresent(id -> near.put(id, entity.getBlockPos()));
      }
    }
  }
}
