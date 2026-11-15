package io.github.marcelofabianov.redweave.block;

import com.mojang.serialization.MapCodec;
import io.github.marcelofabianov.redweave.block.part.HandPart;
import io.github.marcelofabianov.redweave.block.part.LivePart;
import io.github.marcelofabianov.redweave.block.part.OwnedPart;
import io.github.marcelofabianov.redweave.block.part.Part;
import io.github.marcelofabianov.redweave.block.part.PartTargets;
import io.github.marcelofabianov.redweave.item.ConfiguratorItem;
import io.github.marcelofabianov.redweave.registry.RedweaveBlockEntities;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The part space, `redweave:part_space`: one block space that holds the parts of the mod, one on
 * each side (_docs/funcionalidades/multipart.md). It joins the shapes and the signals of its parts
 * and hands each event to them; only the part under the aim answers a click or a break (515).
 */
public final class PartSpaceBlock extends Block implements EntityBlock {
  public static final MapCodec<PartSpaceBlock> CODEC = simpleCodec(PartSpaceBlock::new);

  public PartSpaceBlock(Properties properties) {
    super(properties);
  }

  @Override
  protected MapCodec<PartSpaceBlock> codec() {
    return CODEC;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new PartSpaceBlockEntity(pos, state);
  }

  /** Only the server ticks the parts; the client sees them in their looks (answer 401). */
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> type) {
    if (level.isClientSide || type != RedweaveBlockEntities.PART_SPACE.get()) {
      return null;
    }
    return (tickLevel, pos, tickState, entity) -> {
      if (entity instanceof PartSpaceBlockEntity space) {
        space.tickParts();
      }
    };
  }

  /**
   * A player sees only the part under its aim, so the outline shows only it (answer 515); anything
   * else sees every part.
   */
  @Override
  protected VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    if (!(level.getBlockEntity(pos) instanceof PartSpaceBlockEntity space)) {
      return Shapes.empty();
    }
    if (context instanceof EntityCollisionContext entity
        && entity.getEntity() instanceof Player player) {
      return PartTargets.looked(player, pos, space.parts()).map(Part::shape).orElse(Shapes.empty());
    }
    return space.parts().stream().map(Part::shape).reduce(Shapes.empty(), Shapes::or);
  }

  @Override
  protected boolean isSignalSource(BlockState state) {
    return true;
  }

  /**
   * The game wire turns to the space when a part takes it, as a Signal Node on the floor or a
   * Signal Cable (answers 507 and 574); the direction goes from the wire to the space.
   */
  @Override
  public boolean canConnectRedstone(
      BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
    return direction != null
        && level.getBlockEntity(pos) instanceof PartSpaceBlockEntity space
        && space.parts().stream().anyMatch(part -> part.turnsWire(direction));
  }

  /** The strongest signal any part gives to the side. */
  @Override
  protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
    if (!(level.getBlockEntity(pos) instanceof PartSpaceBlockEntity space)) {
      return 0;
    }
    return space.parts().stream().mapToInt(part -> part.signal(level, pos, side)).max().orElse(0);
  }

  @Override
  protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
    return getSignal(state, level, pos, side);
  }

  /** A part whose block of support is gone falls as an item, as a button (answers 74 and 394). */
  @Override
  protected void neighborChanged(
      BlockState state,
      Level level,
      BlockPos pos,
      Block neighborBlock,
      BlockPos neighborPos,
      boolean movedByPiston) {
    super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
    if (level.isClientSide || !(level.getBlockEntity(pos) instanceof PartSpaceBlockEntity space)) {
      return;
    }
    for (Part part : space.parts()) {
      if (!Plates.isHeld(level, pos, part.face().getOpposite())) {
        PartSpaces.drop(space, part);
      } else if (part instanceof LivePart live) {
        live.neighborChanged(space);
      }
    }
    PartSpaces.clearIfEmpty(space);
  }

  @Override
  protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    return true;
  }

  @Override
  protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    if (level.getBlockEntity(pos) instanceof PartSpaceBlockEntity space) {
      space.parts().stream()
          .filter(LivePart.class::isInstance)
          .forEach(part -> ((LivePart) part).tick(space));
    }
  }

  /** In survival, only who may break the aimed part breaks it; in creative, anyone (394). */
  @Override
  protected float getDestroyProgress(
      BlockState state, Player player, BlockGetter level, BlockPos pos) {
    boolean allowed =
        player.isCreative()
            || aimed(level, pos, player).map(part -> mayBreak(part, player)).orElse(true);
    return allowed ? super.getDestroyProgress(state, player, level, pos) : 0;
  }

  /**
   * Breaks only the aimed part and drops only its item (answers 515 and 539). The space stays while
   * it has parts, and the last part leaves it as air, which its neighbors may already have done
   * when they woke; the server has the last word on the owner (rule 16).
   */
  @Override
  public boolean onDestroyedByPlayer(
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      boolean willHarvest,
      FluidState fluid) {
    if (!(level.getBlockEntity(pos) instanceof PartSpaceBlockEntity space)) {
      return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }
    Optional<Part> aimed = PartTargets.looked(player, pos, space.parts());
    if (aimed.isEmpty() || !PartSpaces.mayBreakOnServer(space, aimed.get(), player)) {
      return false;
    }
    PartSpaces.breakPart(space, aimed.get(), !player.isCreative() && willHarvest);
    if (!space.isEmpty()) {
      return false;
    }
    super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    return true;
  }

  /** Gone by any means, as an explosion, the parts still in it leave and drop (answer 539). */
  @Override
  protected void onRemove(
      BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
    if (!level.isClientSide
        && !state.is(newState.getBlock())
        && level.getBlockEntity(pos) instanceof PartSpaceBlockEntity space) {
      space.parts().forEach(part -> PartSpaces.breakPart(space, part, true));
    }
    super.onRemove(state, level, pos, newState, movedByPiston);
  }

  /**
   * Only an empty hand uses the part itself; an item, as the Configurator, takes the click. A part
   * that takes any item, as the Signal Lever, takes it before the item, but never the Configurator
   * (answer 576).
   */
  @Override
  protected ItemInteractionResult useItemOn(
      ItemStack stack,
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hit) {
    if (stack.isEmpty()) {
      return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }
    Optional<HandPart> taker =
        stack.getItem() instanceof ConfiguratorItem
            ? Optional.empty()
            : aimed(level, pos, player)
                .filter(HandPart.class::isInstance)
                .map(HandPart.class::cast)
                .filter(HandPart::takesAnyItem);
    if (taker.isEmpty()) {
      return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }
    if (!level.isClientSide) {
      taker.get().emptyHand(player);
    }
    return ItemInteractionResult.sidedSuccess(level.isClientSide);
  }

  @Override
  protected InteractionResult useWithoutItem(
      BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
    if (!level.isClientSide) {
      aimed(level, pos, player)
          .filter(HandPart.class::isInstance)
          .ifPresent(part -> ((HandPart) part).emptyHand(player));
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  /** The middle click picks the item of the aimed part. */
  @Override
  public ItemStack getCloneItemStack(
      BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
    return aimed(level, pos, player).map(Part::drop).orElse(ItemStack.EMPTY);
  }

  private static Optional<Part> aimed(BlockGetter level, BlockPos pos, Player player) {
    return level.getBlockEntity(pos) instanceof PartSpaceBlockEntity space
        ? PartTargets.looked(player, pos, space.parts())
        : Optional.empty();
  }

  private static boolean mayBreak(Part part, Player player) {
    return !(part instanceof OwnedPart owned) || owned.mayBreak(player);
  }
}
