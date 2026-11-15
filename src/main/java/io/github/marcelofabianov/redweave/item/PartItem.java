package io.github.marcelofabianov.redweave.item;

import io.github.marcelofabianov.redweave.block.PartSpaceBlockEntity;
import io.github.marcelofabianov.redweave.block.PartSpaces;
import io.github.marcelofabianov.redweave.block.Plates;
import io.github.marcelofabianov.redweave.block.part.LivePart;
import io.github.marcelofabianov.redweave.block.part.Part;
import io.github.marcelofabianov.redweave.block.part.PartMessages;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.block.part.PartTypes;
import io.github.marcelofabianov.redweave.domain.part.Fit;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The item of a part, as the Signal Node: it puts the part on the side of a block space, sharing
 * the space with other parts (answers 512, 523 and 533). Clicking a part that is already there aims
 * at the opposite side of the same space, as if the part were not there (answer 545).
 */
public final class PartItem extends Item {
  public PartItem(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level level = context.getLevel();
    Player player = context.getPlayer();
    PartSpot spot = spotOf(context);
    Optional<Part> part = PartTypes.placed(this, spot.face());
    if (player == null || part.isEmpty() || !fitsInto(level, spot)) {
      return InteractionResult.FAIL;
    }
    Fit fit = fitOf(level, spot, part.get());
    if (fit != Fit.FITS) {
      if (!level.isClientSide) {
        player.displayClientMessage(Component.translatable(PartMessages.of(fit)), true);
      }
      return InteractionResult.FAIL;
    }
    if (!level.isClientSide) {
      place(level, spot, part.get(), player);
      context.getItemInHand().consume(1, player);
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  /**
   * The space the part goes to and the side it rests on: the one in front of the clicked face, or
   * the clicked space itself when it is air or a part space (answer 545).
   */
  private static PartSpot spotOf(UseOnContext context) {
    BlockPos clicked = context.getClickedPos();
    Direction face = context.getClickedFace();
    BlockState state = context.getLevel().getBlockState(clicked);
    boolean here =
        context.getLevel().getBlockEntity(clicked) instanceof PartSpaceBlockEntity
            || state.canBeReplaced(new BlockPlaceContext(context));
    return new PartSpot(here ? clicked : clicked.relative(face), face.getOpposite());
  }

  /** The space is air, replaceable or a part space, and the block of support holds a plate. */
  private static boolean fitsInto(Level level, PartSpot spot) {
    BlockState state = level.getBlockState(spot.pos());
    boolean space = level.getBlockEntity(spot.pos()) instanceof PartSpaceBlockEntity;
    boolean free = state.isAir() || state.canBeReplaced();
    return (space || free) && Plates.isHeld(level, spot.pos(), spot.face().getOpposite());
  }

  private static Fit fitOf(Level level, PartSpot spot, Part part) {
    return level.getBlockEntity(spot.pos()) instanceof PartSpaceBlockEntity space
        ? space.fit(spot.face(), part.kind())
        : Fit.FITS;
  }

  private static void place(Level level, PartSpot spot, Part part, Player player) {
    PartSpaceBlockEntity space = PartSpaces.place(level, spot, part);
    if (part instanceof LivePart live) {
      live.placed(space, player);
    }
    SoundType sound = SoundType.COPPER;
    level.playSound(
        null,
        spot.pos(),
        sound.getPlaceSound(),
        SoundSource.BLOCKS,
        sound.getVolume(),
        sound.getPitch());
  }
}
