package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.block.part.LeverPart;
import io.github.zenndg.redweave.registry.RedweaveItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/** The Signal Lever of a GameTest: placed on a block of stone and clicked as a player does. */
final class LeverScene {
  private static final float LOOK_NORTH = 180;

  private LeverScene() {}

  /** A lever on top of a block of stone, by the player, who owns it, with its front north. */
  static LeverPart place(GameTestHelper helper, Player owner, BlockPos stone) {
    helper.setBlock(stone, Blocks.STONE);
    owner.setYRot(LOOK_NORTH);
    owner.setYHeadRot(LOOK_NORTH);
    GameTestActions.placeOn(
        helper, owner, new ItemStack(RedweaveItems.SIGNAL_LEVER.get()), stone, Direction.UP);
    return ModuleParts.only(helper, stone.above(), LeverPart.class);
  }

  /** A right-click on the space with the stack in the hand, as the game handles the packet. */
  static InteractionResult click(
      GameTestHelper helper, ServerPlayer player, ItemStack stack, BlockPos relative) {
    player.setItemInHand(InteractionHand.MAIN_HAND, stack);
    BlockPos pos = helper.absolutePos(relative);
    BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
    return player.gameMode.useItemOn(
        player, helper.getLevel(), stack, InteractionHand.MAIN_HAND, hit);
  }
}
