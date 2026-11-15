package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.block.part.NodePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.Node;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
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

  /** Emits what the network says the node emits now, or nothing without a network (71). */
  public static void refreshEmission(RedweaveServer server, Level level, PartSpot spot) {
    Optional<NodeId> id = nodeAt(level, spot);
    Signal emission =
        id.flatMap(server::networkOfNode)
            .map(network -> network.emission(id.get()))
            .orElse(Signal.OFF);
    emit(level, spot, emission);
  }

  /**
   * Feeds the block the node sticks to, like a lever, and wakes the neighbors only when the value
   * changes, so there are no cascading block updates (rule 18).
   */
  public static void emit(Level level, PartSpot spot, Signal signal) {
    Optional<PartSpaceBlockEntity> space = PartSpaceBlockEntity.at(level, spot.pos());
    Optional<NodePart> node = NodePart.at(level, spot);
    if (node.isEmpty() || node.get().emitted() == signal.strength()) {
      return;
    }
    node.get().setEmitted(signal.strength());
    if (node.get().setPowered(signal.isOn())) {
      space.orElseThrow().changed();
    }
    space.ifPresent(found -> found.wakeOthers(node.get()));
    Block block = RedweaveBlocks.PART_SPACE.get();
    level.updateNeighborsAt(spot.pos(), block);
    level.updateNeighborsAt(spot.support(), block);
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
