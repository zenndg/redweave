package io.github.zenndg.redweave.server;

import io.github.zenndg.redweave.application.ControllerLabels;
import io.github.zenndg.redweave.application.LabelChanges;
import io.github.zenndg.redweave.application.ModuleLabel;
import io.github.zenndg.redweave.application.NodeLabel;
import io.github.zenndg.redweave.application.NodeLabels;
import io.github.zenndg.redweave.block.NetworkControllerBlockEntity;
import io.github.zenndg.redweave.block.PartSpaceBlockEntity;
import io.github.zenndg.redweave.block.part.CablePart;
import io.github.zenndg.redweave.block.part.ModulePart;
import io.github.zenndg.redweave.block.part.NodePart;
import io.github.zenndg.redweave.block.part.Part;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.config.NeoForgeLabelRange;
import io.github.zenndg.redweave.domain.network.ModuleId;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.network.NodeLabelsUpdate;
import io.github.zenndg.redweave.registry.RedweaveItems;
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
 * Which labels each player with the Configurator in the main hand has, of the nodes, of the Signal
 * Modules, of the Signal Cables and of the Network Controllers, to send only what changed (answers
 * 268, 269, 417, 525 and 773, and rule 18). It reads only loaded chunks, so no chunk loads (rule
 * 6), and keeps nothing in the saves. Each label goes with its place, so the labels of the parts of
 * one space do not sit on each other (answer 547).
 */
public final class LabelWatch {
  private final RedweaveServer server;
  private final Map<UUID, LabelChanges<NodeId, Placed<NodeLabel>>> nodeHolders = new HashMap<>();
  private final Map<UUID, LabelChanges<ModuleId, Placed<ModuleLabel>>> moduleHolders =
      new HashMap<>();
  private final Map<UUID, LabelChanges<PartSpot, NodeLabelsUpdate.CableLine>> cableHolders =
      new HashMap<>();
  private final Map<UUID, LabelChanges<NetworkId, NodeLabelsUpdate.ControllerLine>>
      controllerHolders = new HashMap<>();

  LabelWatch(RedweaveServer server) {
    this.server = server;
  }

  /** A label with its place, so a space that gains or loses a labeled part sends it again. */
  private record Placed<L>(L label, NodeLabelsUpdate.Place place) {}

  /**
   * The nodes, the modules, the cables and the controllers near a player, with their spots and the
   * labeled parts per space.
   */
  private record Near(
      Map<NodeId, PartSpot> nodes,
      Map<ModuleId, PartSpot> modules,
      Map<ModuleId, ModuleLabel> moduleLabels,
      Map<PartSpot, CablePart> cables,
      Map<NetworkId, BlockPos> controllers,
      Map<BlockPos, Integer> labeled) {
    void count(BlockPos space) {
      labeled.merge(space, 1, Integer::sum);
    }

    /** Shared when the space has more than one labeled part (answer 547). */
    NodeLabelsUpdate.Place placeOf(PartSpot spot) {
      return new NodeLabelsUpdate.Place(spot, labeled.getOrDefault(spot.pos(), 0) > 1);
    }
  }

  /** What changed for a player since the last look, of each kind of label. */
  private record Deltas(
      LabelChanges.Delta<NodeId, Placed<NodeLabel>> nodes,
      LabelChanges.Delta<ModuleId, Placed<ModuleLabel>> modules,
      LabelChanges.Delta<PartSpot, NodeLabelsUpdate.CableLine> cables,
      LabelChanges.Delta<NetworkId, NodeLabelsUpdate.ControllerLine> controllers) {
    boolean isEmpty() {
      return nodes.isEmpty() && modules.isEmpty() && cables.isEmpty() && controllers.isEmpty();
    }
  }

  /** What to send to the player now, or nothing when nothing changed. */
  public Optional<NodeLabelsUpdate> update(ServerPlayer player) {
    UUID id = player.getUUID();
    if (!player.getMainHandItem().is(RedweaveItems.CONFIGURATOR.get())) {
      return letGo(id);
    }
    Near near = near(player, new NeoForgeLabelRange().getAsInt());
    Deltas deltas =
        new Deltas(
            changesOf(nodeHolders, id).next(nodeLabels(near)),
            changesOf(moduleHolders, id).next(moduleLabels(near)),
            changesOf(cableHolders, id).next(cableLabels(near)),
            changesOf(controllerHolders, id).next(controllerLabels(near)));
    return deltas.isEmpty() ? Optional.empty() : Optional.of(updateOf(deltas));
  }

  /** The player let the Configurator go: a clear of all, once, if they had labels. */
  private Optional<NodeLabelsUpdate> letGo(UUID id) {
    boolean had =
        nodeHolders.remove(id) != null
            | moduleHolders.remove(id) != null
            | cableHolders.remove(id) != null
            | controllerHolders.remove(id) != null;
    return had ? Optional.of(NodeLabelsUpdate.clear()) : Optional.empty();
  }

  private static <K, V> LabelChanges<K, V> changesOf(
      Map<UUID, LabelChanges<K, V>> holders, UUID id) {
    return holders.computeIfAbsent(id, holder -> new LabelChanges<>());
  }

  private Map<NodeId, Placed<NodeLabel>> nodeLabels(Near near) {
    Map<NodeId, Placed<NodeLabel>> labels = new LinkedHashMap<>();
    near.nodes()
        .forEach((node, spot) ->
            labels.put(
                node,
                new Placed<>(NodeLabels.of(node, server.networkOfNode(node)), near.placeOf(spot))));
    return labels;
  }

  private static Map<ModuleId, Placed<ModuleLabel>> moduleLabels(Near near) {
    Map<ModuleId, Placed<ModuleLabel>> labels = new LinkedHashMap<>();
    near.moduleLabels()
        .forEach((module, label) ->
            labels.put(module, new Placed<>(label, near.placeOf(near.modules().get(module)))));
    return labels;
  }

  /** The color and the strength of each cable, as `15 · 12` (answers 525 and 770). */
  private static Map<PartSpot, NodeLabelsUpdate.CableLine> cableLabels(Near near) {
    Map<PartSpot, NodeLabelsUpdate.CableLine> labels = new LinkedHashMap<>();
    near.cables()
        .forEach((spot, cable) ->
            labels.put(
                spot,
                NodeLabelsUpdate.CableLine.of(
                    near.placeOf(spot), cable.color(), cable.strength())));
    return labels;
  }

  /** The network, its state and its nodes of each controller with a network (answer 773). */
  private Map<NetworkId, NodeLabelsUpdate.ControllerLine> controllerLabels(Near near) {
    Map<NetworkId, NodeLabelsUpdate.ControllerLine> labels = new LinkedHashMap<>();
    near.controllers()
        .forEach((network, pos) ->
            server
                .network(network)
                .map(ControllerLabels::of)
                .ifPresent(label ->
                    labels.put(
                        network, NodeLabelsUpdate.ControllerLine.of(network.value(), pos, label))));
    return labels;
  }

  private static NodeLabelsUpdate updateOf(Deltas deltas) {
    List<UUID> gone = new ArrayList<>(deltas.nodes().gone().stream().map(NodeId::value).toList());
    deltas.modules().gone().forEach(module -> gone.add(module.value()));
    deltas.controllers().gone().forEach(network -> gone.add(network.value()));
    return new NodeLabelsUpdate(
        false,
        deltas.nodes().changed().entrySet().stream()
            .map(entry ->
                NodeLabelsUpdate.Label.of(
                    entry.getKey().value(), entry.getValue().place(), entry.getValue().label()))
            .toList(),
        deltas.modules().changed().entrySet().stream()
            .map(entry ->
                NodeLabelsUpdate.ModuleLine.of(
                    entry.getKey().value(), entry.getValue().place(), entry.getValue().label()))
            .toList(),
        List.copyOf(deltas.cables().changed().values()),
        List.copyOf(deltas.controllers().changed().values()),
        gone,
        deltas.cables().gone());
  }

  /** A player who left takes nothing with them. */
  public void forget(UUID player) {
    nodeHolders.remove(player);
    moduleHolders.remove(player);
    cableHolders.remove(player);
    controllerHolders.remove(player);
  }

  /**
   * The nodes, the modules, the cables and the controllers in loaded chunks within the reach of the
   * player, in a sphere.
   */
  private static Near near(ServerPlayer player, int range) {
    ServerLevel level = player.serverLevel();
    BlockPos center = player.blockPosition();
    Near near =
        new Near(
            new LinkedHashMap<>(),
            new LinkedHashMap<>(),
            new LinkedHashMap<>(),
            new LinkedHashMap<>(),
            new LinkedHashMap<>(),
            new HashMap<>());
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
      if (entity instanceof PartSpaceBlockEntity space) {
        addParts(space, near);
      } else if (entity instanceof NetworkControllerBlockEntity controller
          && controller.network().isPresent()) {
        near.controllers().put(controller.network().get(), entity.getBlockPos());
      }
    }
  }

  private static void addParts(PartSpaceBlockEntity space, Near near) {
    BlockPos pos = space.getBlockPos();
    for (Part part : space.parts()) {
      PartSpot spot = new PartSpot(pos, part.face());
      if (part instanceof NodePart node && node.node().isPresent()) {
        near.nodes().put(node.node().get(), spot);
        near.count(pos);
      } else if (part instanceof ModulePart module) {
        addModule(module, spot, near);
      } else if (part instanceof CablePart cable) {
        near.cables().put(spot, cable);
        near.count(pos);
      }
    }
  }

  /** The name, the state and the channel of the output of a module (answer 417). */
  private static void addModule(ModulePart module, PartSpot spot, Near near) {
    if (module.module().isEmpty() || module.stateKey().isEmpty()) {
      return;
    }
    ModuleId id = module.module().get().id();
    near.modules().put(id, spot);
    near.count(spot.pos());
    near.moduleLabels()
        .put(
            id,
            new ModuleLabel(
                module.module().get().displayName(),
                module.stateKey().get(),
                module.output().channel()));
  }
}
