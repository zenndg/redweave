package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.block.part.CablePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.item.SignalCableItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;

/** The Signal Cables of a GameTest: placed as a player does, and read by their spot. */
final class CableScene {
  private static final String NO_CABLE = "Expected a cable at %s on %s";
  private static final String WRONG_STRENGTH = "The cable at %s should have %d, got %d";

  private CableScene() {}

  /** Places a cable of the color on the face of the block, by the player, who owns it. */
  static void place(
      GameTestHelper helper, Player owner, BlockPos block, Direction face, ChannelColor color) {
    GameTestActions.placeOn(helper, owner, SignalCableItem.stackOf(color, 1), block, face);
  }

  /** A row of stone from `from` along x, with a cable of the color on top of each block. */
  static void floorRow(
      GameTestHelper helper, Player owner, BlockPos from, int count, ChannelColor color) {
    for (int step = 0; step < count; step++) {
      BlockPos stone = from.east(step);
      helper.setBlock(stone, Blocks.STONE);
      place(helper, owner, stone, Direction.UP, color);
    }
  }

  /** A lever on the floor of the space, already on, on a block of stone. */
  static void leverOn(GameTestHelper helper, BlockPos space) {
    helper.setBlock(space.below(), Blocks.STONE);
    helper.setBlock(
        space, Blocks.LEVER.defaultBlockState().setValue(LeverBlock.FACE, AttachFace.FLOOR));
    helper.pullLever(space);
  }

  /** The cable resting on the side of the space. */
  static CablePart at(GameTestHelper helper, BlockPos space, Direction side) {
    PartSpot spot = new PartSpot(helper.absolutePos(space), side);
    return CablePart.at(helper.getLevel(), spot)
        .orElseThrow(() -> new GameTestAssertException(String.format(NO_CABLE, space, side)));
  }

  static void assertStrength(GameTestHelper helper, BlockPos space, Direction side, int strength) {
    int found = at(helper, space, side).strength();
    if (found != strength) {
      throw new GameTestAssertException(String.format(WRONG_STRENGTH, space, strength, found));
    }
  }

  /** The strength of the cable on the floor of the space. */
  static void assertFloor(GameTestHelper helper, BlockPos space, int strength) {
    assertStrength(helper, space, Direction.DOWN, strength);
  }
}
