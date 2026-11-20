package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.block.part.LockPart;
import io.github.zenndg.redweave.registry.RedweaveItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

/** The Signal Lock of a GameTest: placed on a block of stone, with its front to the north. */
final class LockScene {
  private static final float LOOK_NORTH = 180;

  private LockScene() {}

  /** A lock on top of a block of stone, by the player, who owns it, with its front north. */
  static LockPart place(GameTestHelper helper, Player owner, BlockPos stone) {
    helper.setBlock(stone, Blocks.STONE);
    owner.setYRot(LOOK_NORTH);
    owner.setYHeadRot(LOOK_NORTH);
    GameTestActions.placeOn(
        helper, owner, new ItemStack(RedweaveItems.SIGNAL_LOCK.get()), stone, Direction.UP);
    return ModuleParts.only(helper, stone.above(), LockPart.class);
  }
}
