package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.Node;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/** Carries the redstone between a node block and its network: reading in, emitting out. */
public final class NodeSignals {
  private NodeSignals() {}

  /** Reads every input of the node into its network (no-de-sinal.md, section Ler). */
  static void read(RedweaveServer server, Level level, BlockPos pos, BlockState state) {
    Optional<NodeId> id = nodeAt(level, pos);
    Optional<Node> node = id.flatMap(server::networkOfNode).flatMap(net -> net.node(id.get()));
    if (node.isEmpty()) {
      return;
    }
    net.minecraft.core.Direction stuckTo = state.getValue(SignalNodeBlock.FACING);
    List<Optional<ChannelSlot>> slots = node.get().slots();
    for (int index = 0; index < slots.size(); index++) {
      Optional<ChannelSlot> input = slots.get(index).filter(s -> s.direction() == Direction.INPUT);
      if (input.isPresent()) {
        net.minecraft.core.Direction face = Faces.toDirection(input.get().face());
        server.readInput().read(id.get(), index, NodeReading.read(level, pos, stuckTo, face));
      }
    }
  }

  /** Emits what the network says the node emits now, or nothing without a network (71). */
  static void refreshEmission(RedweaveServer server, Level level, BlockPos pos) {
    Optional<NodeId> id = nodeAt(level, pos);
    Signal emission =
        id.flatMap(server::networkOfNode)
            .map(network -> network.emission(id.get()))
            .orElse(Signal.OFF);
    emit(level, pos, emission);
  }

  /**
   * Feeds the block the node sticks to, like a lever, and wakes the neighbors only when the value
   * changes, so there are no cascading block updates (rule 18).
   */
  static void emit(Level level, BlockPos pos, Signal signal) {
    BlockState state = level.getBlockState(pos);
    if (!(level.getBlockEntity(pos) instanceof SignalNodeBlockEntity node)
        || node.emitted() == signal.strength()) {
      return;
    }
    node.setEmitted(signal.strength());
    BlockState powered = state.setValue(SignalNodeBlock.POWERED, signal.isOn());
    if (powered != state) {
      level.setBlock(pos, powered, Block.UPDATE_ALL);
    }
    Block block = state.getBlock();
    level.updateNeighborsAt(pos, block);
    level.updateNeighborsAt(
        pos.relative(state.getValue(SignalNodeBlock.FACING).getOpposite()), block);
  }

  /** The node of the block, when the block is one and has its ID. */
  public static Optional<NodeId> nodeAt(Level level, BlockPos pos) {
    return level.getBlockEntity(pos) instanceof SignalNodeBlockEntity node
        ? node.node()
        : Optional.empty();
  }

  /** The network of a node, if it is in one. */
  static Optional<Network> networkAt(RedweaveServer server, Level level, BlockPos pos) {
    return nodeAt(level, pos).flatMap(server::networkOfNode);
  }
}
