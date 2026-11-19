package io.github.zenndg.redweave.block;

import io.github.zenndg.redweave.block.part.CablePart;
import io.github.zenndg.redweave.block.part.NodeBeside;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.domain.signal.Signal;
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
   * output of the node is never read back, or of a cable of its space or a cable or the game wire
   * beside it, on its side (answers 566 and 574). On any other face, what that neighbor sends in.
   */
  static Signal read(Level level, PartSpot node, Direction face) {
    Direction stuckTo = node.face().getOpposite();
    BlockPos support = node.support();
    if (face == stuckTo) {
      int fromCables = Math.max(cablesOf(level, node), NodeBeside.strength(level, node));
      return Signal.clamped(Math.max(strengthWithout(level, support, node), fromCables));
    }
    BlockPos neighbor = support.relative(face);
    if (!level.isLoaded(neighbor)) {
      return Signal.OFF;
    }
    return Signal.clamped(level.getSignal(neighbor, face));
  }

  /**
   * As the game reads a block, without the node: what the block emits toward the node and, if it
   * conducts redstone, the strong power its other neighbors give it. The other parts of the space
   * of the node count, as neighbors of the block (answer 514).
   */
  private static int strengthWithout(Level level, BlockPos support, PartSpot node) {
    Direction towardSupport = node.face();
    BlockState state = level.getBlockState(support);
    int strength = state.getSignal(level, support, towardSupport);
    if (!state.shouldCheckWeakPower(level, support, towardSupport)) {
      return strength;
    }
    for (Direction side : Direction.values()) {
      BlockPos neighbor = support.relative(side);
      if (level.isLoaded(neighbor)) {
        strength = Math.max(strength, directSignal(level, neighbor, side, node));
      }
    }
    return strength;
  }

  /**
   * A Signal Cable of the space of the node counts as its block of support, by the inner curve
   * (answer 566).
   */
  private static int cablesOf(Level level, PartSpot node) {
    return PartSpaceBlockEntity.at(level, node.pos())
        .map(space ->
            space.parts().stream()
                .filter(CablePart.class::isInstance)
                .mapToInt(part -> ((CablePart) part).strength())
                .max()
                .orElse(0))
        .orElse(0);
  }

  private static int directSignal(Level level, BlockPos neighbor, Direction side, PartSpot node) {
    if (!neighbor.equals(node.pos())) {
      return level.getDirectSignal(neighbor, side);
    }
    return PartSpaceBlockEntity.at(level, neighbor)
        .map(space -> space.signalWithout(space.at(node.face()).orElse(null), side))
        .orElse(0);
  }
}
