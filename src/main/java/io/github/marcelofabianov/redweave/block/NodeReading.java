package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * What an input of a node reads from the block it sticks to, the way a repeater reads the block
 * behind it (answer 71). It only translates the redstone of the game into a Signal.
 */
final class NodeReading {
  private NodeReading() {}

  /**
   * On the face the node sticks to, the strength of the block itself, without the node, so an
   * output of the node is never read back. On any other face, what that neighbor sends in.
   */
  static Signal read(Level level, BlockPos nodePos, Direction stuckTo, Direction face) {
    BlockPos support = nodePos.relative(stuckTo.getOpposite());
    if (face == stuckTo) {
      return Signal.clamped(strengthWithout(level, support, nodePos, stuckTo.getOpposite()));
    }
    BlockPos neighbor = support.relative(face);
    if (!level.isLoaded(neighbor)) {
      return Signal.OFF;
    }
    return Signal.clamped(level.getSignal(neighbor, face));
  }

  /**
   * As the game reads a block, without the node: what the block emits toward the node and, if it
   * conducts redstone, the strong power its other neighbors give it.
   */
  private static int strengthWithout(
      Level level, BlockPos support, BlockPos node, Direction towardSupport) {
    BlockState state = level.getBlockState(support);
    int strength = state.getSignal(level, support, towardSupport);
    if (!state.shouldCheckWeakPower(level, support, towardSupport)) {
      return strength;
    }
    for (Direction side : Direction.values()) {
      BlockPos neighbor = support.relative(side);
      if (!neighbor.equals(node) && level.isLoaded(neighbor)) {
        strength = Math.max(strength, level.getDirectSignal(neighbor, side));
      }
    }
    return strength;
  }
}
