package io.github.marcelofabianov.redweave.block;

import com.mojang.serialization.MapCodec;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * The Network Controller: _docs/funcionalidades/controlador-de-rede.md. It adapts the world to the
 * use cases, and the rules stay in the domain (AGENTS.md, rule 10).
 */
public final class NetworkControllerBlock extends Block implements EntityBlock {
  public static final MapCodec<NetworkControllerBlock> CODEC =
      simpleCodec(NetworkControllerBlock::new);

  public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

  /** True when the block has a network, which lights the core on top (answer 64). */
  public static final BooleanProperty LINKED = BooleanProperty.create("linked");

  public NetworkControllerBlock(Properties properties) {
    super(properties);
    registerDefaultState(
        stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LINKED, false));
  }

  /** Scheduled when the block entity loads: the core shows the state of the network (177). */
  @Override
  protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    RedweaveServer.running().ifPresent(server -> ControllerLink.refreshCore(server, level, pos));
  }

  @Override
  protected MapCodec<NetworkControllerBlock> codec() {
    return CODEC;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, LINKED);
  }

  /** The front faces the player who places it, as a furnace does. */
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new NetworkControllerBlockEntity(pos, state);
  }

  /** In survival, only the owner breaks it, and the client shows the same (answer 30). */
  @Override
  protected float getDestroyProgress(
      BlockState state, Player player, BlockGetter level, BlockPos pos) {
    if (!player.isCreative() && !ownedBy(level, pos, player)) {
      return 0;
    }
    return super.getDestroyProgress(state, player, level, pos);
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
    if (!level.isClientSide && !player.isCreative() && !mayBreak(level, pos, player)) {
      return false;
    }
    return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
  }

  /** Gone by any means, the controller leaves its network unplaced (answers 49 and 146). */
  @Override
  protected void onRemove(
      BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
    if (!level.isClientSide && !state.is(newState.getBlock())) {
      Optional<RedweaveServer> server = RedweaveServer.running();
      controllerAt(level, pos)
          .flatMap(NetworkControllerBlockEntity::network)
          .ifPresent(id -> server.ifPresent(running -> running.loseController().lose(id)));
    }
    super.onRemove(state, level, pos, newState, movedByPiston);
  }

  /**
   * Only an empty hand uses this block itself. With an item, as the Configurator, the click goes
   * straight to the item (answer 113).
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
    if (!stack.isEmpty()) {
      return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
    }
    return super.useItemOn(stack, state, level, pos, player, hand, hit);
  }

  @Override
  protected InteractionResult useWithoutItem(
      BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
    if (level.isClientSide) {
      return InteractionResult.SUCCESS;
    }
    Optional<RedweaveServer> server = RedweaveServer.running();
    Optional<NetworkControllerBlockEntity> controller = controllerAt(level, pos);
    if (server.isPresent() && controller.isPresent()) {
      ControllerClick.show(server.get(), controller.get(), player);
    }
    return InteractionResult.CONSUME;
  }

  private static boolean ownedBy(BlockGetter level, BlockPos pos, Player player) {
    return controllerAt(level, pos).map(controller -> controller.isOwner(player)).orElse(true);
  }

  /** The owner breaks it, and the network is unplaced; a block without a network asks the BE. */
  private static boolean mayBreak(Level level, BlockPos pos, Player player) {
    Optional<RedweaveServer> server = RedweaveServer.running();
    Optional<NetworkControllerBlockEntity> controller = controllerAt(level, pos);
    if (server.isEmpty() || controller.isEmpty() || controller.get().network().isEmpty()) {
      return ownedBy(level, pos, player);
    }
    PlayerId breaker = new PlayerId(player.getUUID());
    Outcome outcome =
        server.get().breakController().breakBy(breaker, controller.get().network().get());
    if (outcome == Outcome.UNKNOWN_NETWORK) {
      return ownedBy(level, pos, player);
    }
    return outcome == Outcome.ACCEPTED;
  }

  private static Optional<NetworkControllerBlockEntity> controllerAt(
      BlockGetter level, BlockPos pos) {
    return level.getBlockEntity(pos) instanceof NetworkControllerBlockEntity controller
        ? Optional.of(controller)
        : Optional.empty();
  }
}
