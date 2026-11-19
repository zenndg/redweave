package io.github.zenndg.redweave.block.part;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Which part of a space a player aims at, so only that one breaks, takes the Configurator and shows
 * its outline (answer 515). The look of the player decides; when it misses every part, as a click
 * from a test, the part nearest to the point wins.
 */
public final class PartTargets {
  private PartTargets() {}

  /** The part under the look of the player, or the one nearest to the eyes. */
  public static Optional<Part> looked(Player player, BlockPos pos, Collection<Part> parts) {
    Vec3 eye = player.getEyePosition();
    Vec3 end = eye.add(player.getViewVector(1).scale(player.blockInteractionRange()));
    return parts.stream()
        .filter(part -> distanceOfHit(part, pos, eye, end).isPresent())
        .min(Comparator.comparingDouble(part -> distanceOfHit(part, pos, eye, end).orElseThrow()))
        .or(() -> nearest(pos, parts, eye));
  }

  /** The part nearest to the point, as where a click landed. */
  public static Optional<Part> nearest(BlockPos pos, Collection<Part> parts, Vec3 point) {
    return parts.stream()
        .min(Comparator.comparingDouble(part -> boundsOf(part, pos).distanceToSqr(point)));
  }

  private static Optional<Double> distanceOfHit(Part part, BlockPos pos, Vec3 eye, Vec3 end) {
    BlockHitResult hit = part.shape().clip(eye, end, pos);
    return Optional.ofNullable(hit).map(found -> found.getLocation().distanceToSqr(eye));
  }

  private static AABB boundsOf(Part part, BlockPos pos) {
    return part.shape().bounds().move(pos);
  }
}
