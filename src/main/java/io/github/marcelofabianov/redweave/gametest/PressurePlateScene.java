package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.block.part.PressurePlatePart;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

/**
 * The Signal Pressure Plate of a GameTest: placed on the floor of a block of stone, and the
 * entities the game walks into its space.
 */
final class PressurePlateScene {
  private static final float LOOK_NORTH = 180;

  private PressurePlateScene() {}

  /** A plate on top of a block of stone, by the player, who owns it, with its front north. */
  static PressurePlatePart place(GameTestHelper helper, Player owner, BlockPos stone) {
    helper.setBlock(stone, Blocks.STONE);
    owner.setYRot(LOOK_NORTH);
    owner.setYHeadRot(LOOK_NORTH);
    GameTestActions.placeOn(
        helper,
        owner,
        new ItemStack(RedweaveItems.SIGNAL_PRESSURE_PLATE.get()),
        stone,
        Direction.UP);
    return ModuleParts.only(helper, stone.above(), PressurePlatePart.class);
  }

  /** A mob that stays where it is put, standing on the plate. */
  static <T extends Mob> T standing(GameTestHelper helper, EntityType<T> type, BlockPos plate) {
    return helper.spawnWithNoFreeWill(type, plate);
  }

  /** Puts the entity on the plate and tells the space, as the game does each tick it is inside. */
  static void stepOn(GameTestHelper helper, Entity entity, BlockPos plate) {
    entity.setPos(Vec3.atBottomCenterOf(helper.absolutePos(plate)));
    BlockPos pos = helper.absolutePos(plate);
    helper.getLevel().getBlockState(pos).entityInside(helper.getLevel(), pos, entity);
  }
}
