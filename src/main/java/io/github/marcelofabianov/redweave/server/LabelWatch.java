package io.github.marcelofabianov.redweave.server;

import io.github.marcelofabianov.redweave.application.LabelChanges;
import io.github.marcelofabianov.redweave.application.ModuleLabel;
import io.github.marcelofabianov.redweave.application.NodeLabel;
import io.github.marcelofabianov.redweave.application.NodeLabels;
import io.github.marcelofabianov.redweave.block.SignalNodeBlockEntity;
import io.github.marcelofabianov.redweave.block.SignalTimerBlockEntity;
import io.github.marcelofabianov.redweave.config.NeoForgeLabelRange;
import io.github.marcelofabianov.redweave.domain.network.ModuleId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.network.NodeLabelsUpdate;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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
 * Which labels each player with the Configurator in the main hand has, of the nodes and of the
 * Signal Modules, to send only what changed (answers 268, 269 and 417, and rule 18). It reads only
 * loaded chunks, so no chunk loads (rule 6), and keeps nothing in the saves.
 */
public final class LabelWatch {
  private final RedweaveServer server;
  private final Map<UUID, LabelChanges<NodeId, NodeLabel>> nodeHolders = new HashMap<>();
  private final Map<UUID, LabelChanges<ModuleId, ModuleLabel>> moduleHolders = new HashMap<>();

  LabelWatch(RedweaveServer server) {
    this.server = server;
  }

  /** The nodes and the modules near a player, with their blocks. */
  private record Near(
      Map<NodeId, BlockPos> nodes,
      Map<ModuleId, BlockPos> modules,
      Map<ModuleId, ModuleLabel> moduleLabels) {}

  /** What to send to the player now, or nothing when nothing changed. */
  public Optional<NodeLabelsUpdate> update(ServerPlayer player) {
    UUID id = player.getUUID();
    if (!player.getMainHandItem().is(RedweaveItems.CONFIGURATOR.get())) {
      boolean had = nodeHolders.remove(id) != null | moduleHolders.remove(id) != null;
      return had ? Optional.of(NodeLabelsUpdate.clear()) : Optional.empty();
    }
    Near near = near(player, new NeoForgeLabelRange().getAsInt());
    Map<NodeId, NodeLabel> labels = new LinkedHashMap<>();
    near.nodes()
        .keySet()
        .forEach(node -> labels.put(node, NodeLabels.of(node, server.networkOfNode(node))));
    LabelChanges.Delta<NodeId, NodeLabel> nodes =
        nodeHolders.computeIfAbsent(id, holder -> new LabelChanges<>()).next(labels);
    LabelChanges.Delta<ModuleId, ModuleLabel> modules =
        moduleHolders.computeIfAbsent(id, holder -> new LabelChanges<>()).next(near.moduleLabels());
    if (nodes.isEmpty() && modules.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(updateOf(near, nodes, modules));
  }

  private static NodeLabelsUpdate updateOf(
      Near near,
      LabelChanges.Delta<NodeId, NodeLabel> nodes,
      LabelChanges.Delta<ModuleId, ModuleLabel> modules) {
    List<UUID> gone = new ArrayList<>(nodes.gone().stream().map(NodeId::value).toList());
    modules.gone().forEach(module -> gone.add(module.value()));
    return new NodeLabelsUpdate(
        false,
        nodes.changed().entrySet().stream()
            .map(entry ->
                NodeLabelsUpdate.Label.of(
                    entry.getKey().value(), near.nodes().get(entry.getKey()), entry.getValue()))
            .toList(),
        modules.changed().entrySet().stream()
            .map(entry ->
                NodeLabelsUpdate.ModuleLine.of(
                    entry.getKey().value(), near.modules().get(entry.getKey()), entry.getValue()))
            .toList(),
        gone);
  }

  /** A player who left takes nothing with them. */
  public void forget(UUID player) {
    nodeHolders.remove(player);
    moduleHolders.remove(player);
  }

  /** The nodes and modules in loaded chunks within the reach of the player, in a sphere. */
  private static Near near(ServerPlayer player, int range) {
    ServerLevel level = player.serverLevel();
    BlockPos center = player.blockPosition();
    Near near = new Near(new LinkedHashMap<>(), new LinkedHashMap<>(), new LinkedHashMap<>());
    int fromX = SectionPos.blockToSectionCoord(center.getX() - range);
    int toX = SectionPos.blockToSectionCoord(center.getX() + range);
    int fromZ = SectionPos.blockToSectionCoord(center.getZ() - range);
    int toZ = SectionPos.blockToSectionCoord(center.getZ() + range);
    long reach = (long) range * range;
    for (int x = fromX; x <= toX; x++) {
      for (int z = fromZ; z <= toZ; z++) {
        LevelChunk chunk = level.getChunkSource().getChunkNow(x, z);
        if (chunk != null) {
          addPlates(chunk, center, reach, near);
        }
      }
    }
    return near;
  }

  private static void addPlates(LevelChunk chunk, BlockPos center, long reach, Near near) {
    for (BlockEntity entity : chunk.getBlockEntities().values()) {
      if (entity.getBlockPos().distSqr(center) > reach) {
        continue;
      }
      if (entity instanceof SignalNodeBlockEntity node) {
        node.node().ifPresent(id -> near.nodes().put(id, entity.getBlockPos()));
      } else if (entity instanceof SignalTimerBlockEntity timer) {
        addTimer(timer, near);
      }
    }
  }

  /** The name, the state and the channel of the output of a timer (answer 417). */
  private static void addTimer(SignalTimerBlockEntity timer, Near near) {
    if (timer.module().isEmpty() || timer.timer().isEmpty()) {
      return;
    }
    ModuleId id = timer.module().get().id();
    near.modules().put(id, timer.getBlockPos());
    near.moduleLabels()
        .put(
            id,
            new ModuleLabel(
                timer.module().get().displayName(),
                timer.timer().get().state(),
                timer.output().channel()));
  }
}
