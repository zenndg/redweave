package io.github.marcelofabianov.redweave.block.part;

import io.github.marcelofabianov.redweave.domain.module.PlateMode;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * What a Signal Pressure Plate on the floor senses: the area of its plate, 10 × 10 px, up to 4 px
 * above it, and the kind of each entity there, as its mode counts them (answers 607 and 621).
 */
final class PlateSensing {
  private static final double EDGE = 3.0 / 16;
  private static final double FAR_EDGE = 13.0 / 16;
  private static final double HEIGHT = 4.0 / 16;

  private PlateSensing() {}

  /** The area over the plate in the space at the position. */
  static AABB areaAt(BlockPos pos) {
    return new AABB(EDGE, 0, EDGE, FAR_EDGE, HEIGHT, FAR_EDGE).move(pos);
  }

  /** Whether the entity touches the plate in the space at the position. */
  static boolean touches(BlockPos pos, Entity entity) {
    return entity.getBoundingBox().intersects(areaAt(pos));
  }

  /** The kinds of thing on the plate now: one search of its small area. */
  static Set<PlateMode> presentAt(Level level, BlockPos pos) {
    Set<PlateMode> present = EnumSet.noneOf(PlateMode.class);
    for (Entity entity : level.getEntities((Entity) null, areaAt(pos), PlateSensing::counts)) {
      kindOf(entity).ifPresent(present::add);
    }
    return present;
  }

  /**
   * A player that is not a spectator, an item on the ground, a hostile mob, which the game marks as
   * an enemy, or another living mob, passive; arrows, armor stands and the rest count as nothing
   * (answer 621).
   */
  static Optional<PlateMode> kindOf(Entity entity) {
    if (!counts(entity)) {
      return Optional.empty();
    }
    if (entity instanceof Player) {
      return Optional.of(PlateMode.PLAYERS);
    }
    if (entity instanceof ItemEntity) {
      return Optional.of(PlateMode.ITEMS);
    }
    if (!(entity instanceof LivingEntity) || entity instanceof ArmorStand) {
      return Optional.empty();
    }
    return Optional.of(entity instanceof Enemy ? PlateMode.HOSTILE : PlateMode.PASSIVE);
  }

  private static boolean counts(Entity entity) {
    return entity.isAlive() && !entity.isSpectator() && !entity.isIgnoringBlockTriggers();
  }
}
