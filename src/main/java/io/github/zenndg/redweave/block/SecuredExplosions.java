package io.github.zenndg.redweave.block;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.part.Part;
import io.github.zenndg.redweave.block.part.SecuredPart;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ExplosionEvent;

/**
 * No explosion takes a secured part, as the Signal Lock, nor its block of support (answer 641).
 * Before an explosion breaks its blocks, the spaces with a secured part and the blocks they rest on
 * leave its list. Only loaded blocks are looked at (rule 6).
 */
@EventBusSubscriber(modid = RedweaveMod.MOD_ID)
public final class SecuredExplosions {
  private SecuredExplosions() {}

  @SubscribeEvent
  public static void onDetonate(ExplosionEvent.Detonate event) {
    Level level = event.getLevel();
    if (level.isClientSide) {
      return;
    }
    List<BlockPos> blown = event.getAffectedBlocks();
    Set<BlockPos> kept = new HashSet<>();
    for (BlockPos pos : blown) {
      keepAround(level, pos, kept);
    }
    blown.removeIf(kept::contains);
  }

  /** The block, if it is a space with a secured part, or the support of one beside it. */
  private static void keepAround(Level level, BlockPos pos, Set<BlockPos> kept) {
    keepSpace(level, pos, kept);
    for (Direction side : Direction.values()) {
      BlockPos beside = pos.relative(side);
      if (level.isLoaded(beside)
          && level.getBlockEntity(beside) instanceof PartSpaceBlockEntity space
          && space.at(side.getOpposite()).filter(SecuredPart.class::isInstance).isPresent()) {
        kept.add(pos);
      }
    }
  }

  private static void keepSpace(Level level, BlockPos pos, Set<BlockPos> kept) {
    if (level.getBlockEntity(pos) instanceof PartSpaceBlockEntity space) {
      for (Part part : space.parts()) {
        if (part instanceof SecuredPart secured) {
          kept.add(pos);
          kept.add(pos.relative(secured.face()));
        }
      }
    }
  }
}
