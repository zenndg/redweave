package io.github.marcelofabianov.redweave.block.part;

import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * What a Signal Node exchanges by the sides of its space: only with a Signal Cable and with the
 * game wire in the space beside it, on the same side, which take what it emits and feed it as its
 * block of support, as a cable of its own space (answers 566 and 574). The game wire lies only on
 * the floor, so it meets only a node on the floor. Everything else, as lamps and pistons, still
 * gets the node only through its block of support (answer 262).
 */
public final class NodeBeside {
  private NodeBeside() {}

  /** Whether the block at the position, beside the space of a node on the face, takes from it. */
  static boolean takes(BlockGetter level, BlockPos beside, Direction face) {
    BlockState state = level.getBlockState(beside);
    if (state.is(Blocks.REDSTONE_WIRE)) {
      return face == Direction.DOWN;
    }
    return level.getBlockEntity(beside) instanceof PartSpaceBlockEntity space
        && space.at(face).filter(CablePart.class::isInstance).isPresent();
  }

  /** Whether the game wire, coming from the direction, turns to a node on the face (574). */
  static boolean turnsWire(Direction face, Direction toward) {
    return face == Direction.DOWN && toward.getAxis().isHorizontal();
  }

  /**
   * The strongest cable or game wire beside the space of the node, on its side, which the node
   * reads as its block of support; only loaded blocks are read (rule 6).
   */
  public static int strength(Level level, PartSpot node) {
    int strongest = 0;
    for (Direction direction : CableLinks.across(node.face())) {
      BlockPos beside = node.pos().relative(direction);
      if (level.isLoaded(beside)) {
        strongest = Math.max(strongest, strengthAt(level, beside, node.face()));
      }
    }
    return strongest;
  }

  private static int strengthAt(Level level, BlockPos beside, Direction face) {
    BlockState state = level.getBlockState(beside);
    if (state.is(Blocks.REDSTONE_WIRE)) {
      return face == Direction.DOWN ? state.getValue(RedStoneWireBlock.POWER) : 0;
    }
    return CablePart.at(level, new PartSpot(beside, face)).map(CablePart::strength).orElse(0);
  }
}
