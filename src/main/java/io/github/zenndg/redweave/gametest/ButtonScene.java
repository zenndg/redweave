package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.block.part.ButtonPart;
import io.github.zenndg.redweave.registry.RedweaveItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

/** The Signal Button of a GameTest: placed on a block of stone, clicked as the lever is. */
final class ButtonScene {
  private static final float LOOK_NORTH = 180;

  private ButtonScene() {}

  /** A button on top of a block of stone, by the player, who owns it, with its front north. */
  static ButtonPart place(GameTestHelper helper, Player owner, BlockPos stone) {
    helper.setBlock(stone, Blocks.STONE);
    owner.setYRot(LOOK_NORTH);
    owner.setYHeadRot(LOOK_NORTH);
    GameTestActions.placeOn(
        helper, owner, new ItemStack(RedweaveItems.SIGNAL_BUTTON.get()), stone, Direction.UP);
    return ModuleParts.only(helper, stone.above(), ButtonPart.class);
  }
}
