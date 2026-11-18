package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.application.Emission;
import io.github.marcelofabianov.redweave.block.part.NodePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.Node;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/** Carries the redstone between a node part and its network: reading in, emitting out. */
public final class NodeSignals {
  private NodeSignals() {}

  /** Reads every input of the node into its network (no-de-sinal.md, section Ler). */
  public static void read(RedweaveServer server, Level level, PartSpot spot) {
    Optional<NodeId> id = nodeAt(level, spot);
    Optional<Node> node = id.flatMap(server::networkOfNode).flatMap(net -> net.node(id.get()));
    if (node.isEmpty()) {
      return;
    }
    List<Optional<ChannelSlot>> slots = node.get().slots();
    for (int index = 0; index < slots.size(); index++) {
      Optional<ChannelSlot> input = slots.get(index).filter(ChannelSlot::reads);
      if (input.isPresent()) {
        Direction face = Faces.toDirection(input.get().face());
        server.readInput().read(id.get(), index, NodeReading.read(level, spot, face));
      }
    }
  }

  /** Tells the node what its network says to emit now, or nothing without a network (71). */
  public static void refreshEmission(RedweaveServer server, Level level, PartSpot spot) {
    Emission emission =
        nodeAt(level, spot).map(server.nodeOutputs().emit()::of).orElse(Emission.NONE);
    emit(level, spot, emission);
  }

  /** Cuts the output of a node that stopped counting, though no face of it changed (862). */
  public static void silence(RedweaveServer server, Level level, PartSpot spot) {
    nodeAt(level, spot)
        .filter(server.nodeOutputs().emit()::isSilent)
        .ifPresent(id -> emit(level, spot, server.nodeOutputs().emit().of(id)));
  }

  /**
   * Hands the node what to emit; its output follows after the delay of the node, and its lens at
   * once, so only a change of the lens reaches the clients (answers 860 and 863).
   */
  public static void emit(Level level, PartSpot spot, Emission emission) {
    Optional<PartSpaceBlockEntity> space = PartSpaceBlockEntity.at(level, spot.pos());
    Optional<NodePart> node = NodePart.at(level, spot);
    if (space.isEmpty() || node.isEmpty()) {
      return;
    }
    if (node.get().take(emission)) {
      space.get().changed();
    }
    apply(space.get(), node.get());
  }

  /**
   * Feeds the block the node sticks to, like a lever, and wakes the neighbors only when the value
   * changes, so there are no cascading block updates (rule 18).
   */
  public static void apply(PartSpaceBlockEntity space, NodePart node) {
    if (!node.applyOutput()) {
      return;
    }
    PartSpot spot = space.spotOf(node);
    space.wakeOthers(node);
    Block block = RedweaveBlocks.PART_SPACE.get();
    space.getLevel().updateNeighborsAt(spot.pos(), block);
    space.getLevel().updateNeighborsAt(spot.support(), block);
  }

  /** The node at the spot, when the space has a node there with its ID. */
  public static Optional<NodeId> nodeAt(Level level, PartSpot spot) {
    return NodePart.at(level, spot).flatMap(NodePart::node);
  }

  /** The network of a node, if it is in one. */
  static Optional<Network> networkAt(RedweaveServer server, Level level, PartSpot spot) {
    return nodeAt(level, spot).flatMap(server::networkOfNode);
  }
}
