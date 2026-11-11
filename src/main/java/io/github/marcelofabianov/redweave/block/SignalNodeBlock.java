package io.github.marcelofabianov.redweave.block;

import com.mojang.serialization.MapCodec;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.NodePosition;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.item.ConfiguratorMessages;
import io.github.marcelofabianov.redweave.menu.NodeMenu;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
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
 * The Signal Node: _docs/funcionalidades/no-de-sinal.md. A thin plate stuck to a face of a block,
 * like a button. It adapts the world to the use cases, and the rules stay in the domain (rule 10).
 */
public final class SignalNodeBlock extends Block implements EntityBlock {
  public static final MapCodec<SignalNodeBlock> CODEC = simpleCodec(SignalNodeBlock::new);

  /** The face of the block the node sticks to, pointing away from it (answer 72). */
  public static final DirectionProperty FACING = BlockStateProperties.FACING;

  /** True when the node emits more than 0, which lights its lens (answer 72). */
  public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

  /** True when the node is out of the reach of its controller: dashed red ring (answer 86). */
  public static final BooleanProperty OUT_OF_RANGE = BooleanProperty.create("out_of_range");

  public SignalNodeBlock(Properties properties) {
    super(properties);
    registerDefaultState(
        stateDefinition
            .any()
            .setValue(FACING, Direction.UP)
            .setValue(POWERED, false)
            .setValue(OUT_OF_RANGE, false));
  }

  @Override
  protected MapCodec<SignalNodeBlock> codec() {
    return CODEC;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, POWERED, OUT_OF_RANGE);
  }

  @Override
  protected VoxelShape getShape(
      BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
    return NodeShapes.of(state.getValue(FACING));
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    BlockState state = defaultBlockState().setValue(FACING, context.getClickedFace());
    return canSurvive(state, context.getLevel(), context.getClickedPos()) ? state : null;
  }

  /** The node sticks to any block, but not to the air (no-de-sinal.md). */
  @Override
  protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
    return !level.getBlockState(pos.relative(state.getValue(FACING).getOpposite())).isAir();
  }

  /** Without its block, the node falls as an item, like a button (answer 74). */
  @Override
  protected BlockState updateShape(
      BlockState state,
      Direction direction,
      BlockState neighbor,
      LevelAccessor level,
      BlockPos pos,
      BlockPos neighborPos) {
    if (direction == state.getValue(FACING).getOpposite() && !canSurvive(state, level, pos)) {
      return Blocks.AIR.defaultBlockState();
    }
    return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new SignalNodeBlockEntity(pos, state);
  }

  /**
   * The node joins the current network of the player who placed it (answers 25 and 70), and keeps
   * who placed it, the owner of a node without a network for the Wrench (answers 165 and 189).
   */
  @Override
  public void setPlacedBy(
      Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
    super.setPlacedBy(level, pos, state, placer, stack);
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (level.isClientSide || !(placer instanceof Player player) || server.isEmpty()) {
      return;
    }
    if (level.getBlockEntity(pos) instanceof SignalNodeBlockEntity node) {
      node.setPlacer(player.getUUID());
      node.setNode(server.get().placeNode().place());
      NodeSignals.read(server.get(), level, pos, state);
      NodeLook.refresh(server.get(), level, pos);
      level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
    }
  }

  /** The domain keeps the block the node sticks to and the face it sticks on. */
  public static NodePosition positionOf(Level level, BlockPos pos, BlockState state) {
    Direction stuckTo = state.getValue(FACING);
    return new NodePosition(
        BlockLocations.of(level, pos.relative(stuckTo.getOpposite())), Faces.of(stuckTo));
  }

  @Override
  protected void neighborChanged(
      BlockState state,
      Level level,
      BlockPos pos,
      Block neighborBlock,
      BlockPos neighborPos,
      boolean movedByPiston) {
    super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
    if (!level.isClientSide) {
      RedweaveServer.running().ifPresent(server -> NodeSignals.read(server, level, pos, state));
    }
  }

  /** Scheduled when the node loads: it reads again and takes the signal of its network. */
  @Override
  protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    RedweaveServer.running()
        .ifPresent(server -> {
          NodeSignals.read(server, level, pos, state);
          NodeSignals.refreshEmission(server, level, pos);
          NodeLook.refresh(server, level, pos);
        });
  }

  @Override
  protected boolean isSignalSource(BlockState state) {
    return true;
  }

  /** Like a lever: every neighbor of the node gets its signal (answer 71). */
  @Override
  protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
    return level.getBlockEntity(pos) instanceof SignalNodeBlockEntity node ? node.emitted() : 0;
  }

  /** Like a lever: the block it sticks to gets the signal with strength (answer 71). */
  @Override
  protected int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side) {
    return side == state.getValue(FACING) ? getSignal(state, level, pos, side) : 0;
  }

  /** In survival, only the owner of its network breaks the node (answer 74). */
  @Override
  protected float getDestroyProgress(
      BlockState state, Player player, BlockGetter level, BlockPos pos) {
    boolean owned =
        !(level.getBlockEntity(pos) instanceof SignalNodeBlockEntity node) || node.isOwner(player);
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
    if (!level.isClientSide && !player.isCreative() && !mayBreak(level, pos, player)) {
      return false;
    }
    return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
  }

  private static boolean mayBreak(Level level, BlockPos pos, Player player) {
    Optional<RedweaveServer> server = RedweaveServer.running();
    Optional<NodeId> node = NodeSignals.nodeAt(level, pos);
    if (server.isEmpty() || node.isEmpty()) {
      return true;
    }
    PlayerId breaker = new PlayerId(player.getUUID());
    return server.get().breakNode().breakBy(breaker, node.get()) == Outcome.ACCEPTED;
  }

  /** Gone by any means, the node leaves its network (answer 74). */
  @Override
  protected void onRemove(
      BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
    if (!level.isClientSide && !state.is(newState.getBlock())) {
      Optional<NodeId> node = NodeSignals.nodeAt(level, pos);
      RedweaveServer.running()
          .ifPresent(server -> node.ifPresent(id -> server.loseNode().lose(id)));
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

  /**
   * An empty hand does not open the node: only the Configurator in the Open mode does, and the
   * action bar teaches it (answers 154 and 188).
   */
  @Override
  protected InteractionResult useWithoutItem(
      BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
    if (!level.isClientSide) {
      player.displayClientMessage(Component.translatable(ConfiguratorMessages.USE_TO_OPEN), true);
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  /** Opens the Node Screen, from the Configurator in the Open mode (answer 140). */
  public static void openScreen(ServerPlayer viewer, BlockPos pos) {
    viewer.openMenu(
        new SimpleMenuProvider(
            (id, inventory, opener) -> new NodeMenu(id, viewer, pos),
            RedweaveBlocks.SIGNAL_NODE.get().getName()),
        extra -> NodeMenu.writeOpening(extra, viewer, pos));
  }
}
