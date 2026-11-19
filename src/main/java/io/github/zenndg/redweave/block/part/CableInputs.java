package io.github.zenndg.redweave.block.part;

import io.github.zenndg.redweave.block.PartSpaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * What a Signal Cable reads from beside it, as the game wire reads its neighbors: levers, buttons,
 * repeaters, nodes, modules and the strong power of blocks, the game wire with one less (answers
 * 508 and 565), and the node or the module of its own space (answer 566). Only loaded blocks are
 * read (rule 6).
 */
final class CableInputs {
  private static final int ONE = 1;

  /**
   * While a cable reads, every cable gives nothing, as the game wire turns off its own signal while
   * it reads, so a cable never reads its own strength back through a block (answer 509).
   */
  private static boolean reading;

  private CableInputs() {}

  static boolean isReading() {
    return reading;
  }

  /** The strongest signal the cable at the spot reads now, from 0 to 15. */
  static int read(Level level, PartSpot spot) {
    reading = true;
    try {
      return Math.max(fromBeside(level, spot.pos()), fromSpace(level, spot));
    } finally {
      reading = false;
    }
  }

  private static int fromBeside(Level level, BlockPos pos) {
    int strongest = 0;
    for (Direction direction : Direction.values()) {
      BlockPos side = pos.relative(direction);
      if (!level.isLoaded(side)) {
        continue;
      }
      BlockState state = level.getBlockState(side);
      int signal =
          state.is(Blocks.REDSTONE_WIRE)
              ? state.getValue(RedStoneWireBlock.POWER) - ONE
              : level.getSignal(side, direction);
      strongest = Math.max(strongest, signal);
    }
    return strongest;
  }

  /** What a node of the space emits, and a module only by the edge on the cable's side (566). */
  private static int fromSpace(Level level, PartSpot spot) {
    return PartSpaceBlockEntity.at(level, spot.pos())
        .map(space ->
            space.parts().stream().mapToInt(part -> given(part, spot.face())).max().orElse(0))
        .orElse(0);
  }

  private static int given(Part part, Direction cableFace) {
    if (part instanceof NodePart node) {
      return node.emitted();
    }
    if (part instanceof ModulePart module && module.front() == cableFace) {
      return module.emitted();
    }
    return 0;
  }
}
