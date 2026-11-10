package io.github.marcelofabianov.redweave.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/** What a player does in the GameTests, the way the game does it, without a real connection. */
final class GameTestActions {
  private GameTestActions() {}

  /**
   * Places the item on the face of a block. The game places what is in the hand of the player, so
   * the item goes to the hand first, as when a player right-clicks.
   */
  static void placeOn(
      GameTestHelper helper, Player player, ItemStack stack, BlockPos block, Direction face) {
    player.setItemInHand(InteractionHand.MAIN_HAND, stack);
    helper.placeAt(player, stack, block, face);
  }

  /** What the game does when a player finishes breaking a block, and whether it broke. */
  static boolean breakBy(GameTestHelper helper, Player player, BlockPos relative) {
    Level level = helper.getLevel();
    BlockPos pos = helper.absolutePos(relative);
    BlockState state = level.getBlockState(pos);
    BlockEntity blockEntity = level.getBlockEntity(pos);
    boolean removed = state.onDestroyedByPlayer(level, pos, player, true, level.getFluidState(pos));
    if (removed) {
      Block.dropResources(state, level, pos, blockEntity, player, ItemStack.EMPTY);
    }
    return removed;
  }
}
