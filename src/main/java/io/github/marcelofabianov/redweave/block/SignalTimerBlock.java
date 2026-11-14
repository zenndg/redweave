package io.github.marcelofabianov.redweave.block;

import com.mojang.serialization.MapCodec;
import io.github.marcelofabianov.redweave.config.NeoForgeTimerLimits;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.menu.TimerMenu;
import io.github.marcelofabianov.redweave.registry.RedweaveBlockEntities;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The Signal Timer: _docs/funcionalidades/signal-timer.md. A plate stuck to a face of a block, the
 * size of a node, with its output on the front edge and its `Pause` on the back one (answers 383,
 * 416 and 422). It adapts the world to the domain, where the rules stay (rule 10).
 */
public final class SignalTimerBlock extends Block implements EntityBlock {
  public static final MapCodec<SignalTimerBlock> CODEC = simpleCodec(SignalTimerBlock::new);

  /** The face of the block the plate sticks to, pointing away from it, as the node (428). */
  public static final DirectionProperty FACING = BlockStateProperties.FACING;

  /** Where the front edge points in the world, always across `facing` (answer 428). */
  public static final DirectionProperty FRONT = DirectionProperty.create("front");

  /** The pulse LED: on while the output is on (answer 401). */
  public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

  /** The running LED: on while the owner leaves the timer running (answer 401). */
  public static final BooleanProperty RUNNING = BooleanProperty.create("running");

  public SignalTimerBlock(Properties properties) {
    super(properties);
    registerDefaultState(
        stateDefinition
            .any()
            .setValue(FACING, Direction.UP)
            .setValue(FRONT, Direction.NORTH)
            .setValue(POWERED, false)
            .setValue(RUNNING, false));
  }

  @Override
  protected MapCodec<SignalTimerBlock> codec() {
    return CODEC;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, FRONT, POWERED, RUNNING);
  }

  @Override
  protected VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return NodeShapes.of(state.getValue(FACING));
  }

  /**
   * The front points where the player looks, laid on the face, as a repeater (answer 399): the
   * nearest direction of the look that is across the face.
   */
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    Direction facing = context.getClickedFace();
    Direction front =
        Arrays.stream(context.getNearestLookingDirections())
            .filter(direction -> direction.getAxis() != facing.getAxis())
            .findFirst()
            .orElseThrow();
    BlockState state = defaultBlockState().setValue(FACING, facing).setValue(FRONT, front);
    return canSurvive(state, context.getLevel(), context.getClickedPos()) ? state : null;
  }

  @Override
  protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    return Plates.isHeld(level, pos, state.getValue(FACING));
  }

  /** Without its block, the plate falls as an item, as the node (answer 394). */
  @Override
  protected BlockState updateShape(
      BlockState state,
      Direction direction,
      BlockState neighbor,
      LevelAccessor level,
      BlockPos pos,
      BlockPos neighborPos) {
    if (Plates.falls(level, pos, state.getValue(FACING), direction)) {
      return Blocks.AIR.defaultBlockState();
    }
    return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new SignalTimerBlockEntity(pos, state);
  }

  /** Only the server ticks the timer; the client sees its LEDs in the blockstate (401). */
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> type) {
    if (level.isClientSide || type != RedweaveBlockEntities.SIGNAL_TIMER.get()) {
      return null;
    }
    return (tickLevel, pos, tickState, block) -> {
      if (block instanceof SignalTimerBlockEntity timer) {
        TimerTicks.tick(tickLevel, pos, tickState, timer);
      }
    };
  }

  @Override
  protected boolean isSignalSource(BlockState state) {
    return true;
  }

  /**
   * Only the block in front gets the signal, as from a repeater (answer 407). The game asks with
   * the side seen from the asker, which for the block in front is the opposite of the front.
   */
  @Override
  protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
    if (side != state.getValue(FRONT).getOpposite()) {
      return 0;
    }
    return level.getBlockEntity(pos) instanceof SignalTimerBlockEntity timer ? timer.emitted() : 0;
  }

  /** The block in front gets it with strength, so a solid block passes it on (answer 407). */
  @Override
  protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
    return getSignal(state, level, pos, side);
  }

  /** Gone by any means, the front edge goes to 0 at once (answer 394). */
  @Override
  protected void onRemove(
      BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
    super.onRemove(state, level, pos, newState, movedByPiston);
    if (!level.isClientSide && !state.is(newState.getBlock())) {
      TimerTicks.wakeFront(level, pos, state);
    }
  }

  /** Whoever places the timer owns it, and it is born stopped (answers 390 and 416). */
  @Override
  public void setPlacedBy(
      Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
    super.setPlacedBy(level, pos, state, placer, stack);
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (level.isClientSide || !(placer instanceof Player player) || server.isEmpty()) {
      return;
    }
    if (level.getBlockEntity(pos) instanceof SignalTimerBlockEntity timer) {
      PlayerId owner = new PlayerId(player.getUUID());
      timer.place(server.get().modules().place().place(owner), new NeoForgeTimerLimits());
      level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
    }
  }

  /** In survival, only the owner breaks the plate; in creative, anyone (answer 394). */
  @Override
  protected float getDestroyProgress(
      BlockState state, Player player, BlockGetter level, BlockPos pos) {
    boolean owned =
        !(level.getBlockEntity(pos) instanceof SignalTimerBlockEntity timer)
            || timer.isOwner(player);
    return player.isCreative() || owned ? super.getDestroyProgress(state, player, level, pos) : 0;
  }

  /** The server has the last word on breaking, whatever the client shows (rule 16). */
  @Override
  public boolean onDestroyedByPlayer(
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      boolean willHarvest,
      FluidState fluid) {
    boolean owned =
        !(level.getBlockEntity(pos) instanceof SignalTimerBlockEntity timer)
            || timer.isOwner(player);
    if (!level.isClientSide && !player.isCreative() && !owned) {
      return false;
    }
    return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
  }

  /** Only an empty hand uses the plate itself; an item, as the Configurator, takes the click. */
  @Override
  protected ItemInteractionResult useItemOn(
      ItemStack stack,
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hit) {
    if (!stack.isEmpty()) {
      return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }
    return super.useItemOn(stack, state, level, pos, player, hand, hit);
  }

  /** An empty hand does not open the module, and the action bar teaches how (answer 395). */
  @Override
  protected InteractionResult useWithoutItem(
      BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
    if (!level.isClientSide) {
      player.displayClientMessage(Component.translatable(ModuleMessages.USE_TO_OPEN), true);
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  /** Opens the Signal Timer Screen, from the Configurator in the Open mode (answer 393). */
  public static void openScreen(ServerPlayer viewer, BlockPos pos) {
    viewer.openMenu(
        new SimpleMenuProvider(
            (id, inventory, opener) -> new TimerMenu(id, viewer, pos),
            RedweaveBlocks.SIGNAL_TIMER.get().getName()),
        extra -> TimerMenu.writeOpening(extra, viewer, pos));
  }

  /**
   * Turns the plate 90° clockwise, seen from its front, with the click of a comparator (answers
   * 399, 403 and 432). The old front loses the signal at once, and the next tick feeds the new one.
   */
  public static Direction turn(Level level, BlockPos pos, BlockState state) {
    Direction front = Plates.clockwise(state.getValue(FACING), state.getValue(FRONT));
    if (level.getBlockEntity(pos) instanceof SignalTimerBlockEntity timer) {
      timer.setEmitted(0);
    }
    TimerTicks.wakeFront(level, pos, state);
    level.setBlock(pos, state.setValue(FRONT, front), Block.UPDATE_ALL);
    level.playSound(null, pos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS);
    return front;
  }
}
