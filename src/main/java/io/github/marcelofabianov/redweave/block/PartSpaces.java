package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.block.part.LivePart;
import io.github.marcelofabianov.redweave.block.part.OwnedPart;
import io.github.marcelofabianov.redweave.block.part.Part;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * What happens to a part space as parts come and go: a part enters a space, creating it in the air,
 * and a part leaves, dropping its item in code (answer 539); the empty space goes back to air.
 */
public final class PartSpaces {
  private static final boolean KEEP_DROPS = false;

  private PartSpaces() {}

  /** Puts the part, which fits there, in the space of the spot, making the space if needed. */
  public static PartSpaceBlockEntity place(Level level, PartSpot spot, Part part) {
    BlockPos pos = spot.pos();
    if (!(level.getBlockEntity(pos) instanceof PartSpaceBlockEntity)) {
      level.setBlock(pos, RedweaveBlocks.PART_SPACE.get().defaultBlockState(), Block.UPDATE_ALL);
    }
    PartSpaceBlockEntity space = (PartSpaceBlockEntity) level.getBlockEntity(pos);
    space.add(part);
    wake(space, part);
    return space;
  }

  /** The server decides: creative breaks anything, survival asks the owned part (rule 16). */
  static boolean mayBreakOnServer(PartSpaceBlockEntity space, Part part, Player player) {
    if (player.isCreative() || player.level().isClientSide) {
      return true;
    }
    return !(part instanceof OwnedPart owned) || owned.breakBy(space, player);
  }

  /** Takes the part out, dropping its item if asked, and wakes what touched it. */
  public static void breakPart(PartSpaceBlockEntity space, Part part, boolean drop) {
    Level level = space.getLevel();
    space.remove(part.face());
    if (level.isClientSide) {
      return;
    }
    if (part instanceof LivePart live) {
      live.removed(space);
    }
    if (drop) {
      Block.popResource(level, space.getBlockPos(), part.drop());
    }
    wake(space, part);
  }

  /** A part without its block of support falls as its item (answers 74 and 394). */
  static void drop(PartSpaceBlockEntity space, Part part) {
    breakPart(space, part, true);
  }

  /** The space with no part left goes back to air. */
  public static void clearIfEmpty(PartSpaceBlockEntity space) {
    if (space.isEmpty() && !space.getLevel().isClientSide) {
      space.getLevel().removeBlock(space.getBlockPos(), KEEP_DROPS);
    }
  }

  /** Takes the part out, without its item, and clears the space if it was the last (answer 161). */
  public static void pickUp(PartSpaceBlockEntity space, Part part) {
    breakPart(space, part, false);
    clearIfEmpty(space);
  }

  private static void wake(PartSpaceBlockEntity space, Part part) {
    Level level = space.getLevel();
    BlockPos pos = space.getBlockPos();
    Block block = RedweaveBlocks.PART_SPACE.get();
    level.updateNeighborsAt(pos, block);
    BlockPos support = new PartSpot(pos, part.face()).support();
    if (level.isLoaded(support)) {
      level.updateNeighborsAt(support, block);
    }
  }
}
