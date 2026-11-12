package io.github.marcelofabianov.redweave.block;

import com.mojang.serialization.MapCodec;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.menu.TerminalMenu;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.SimpleMenuProvider;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * The Network Terminal: terminal-de-rede.md. One per player, used and broken only by its owner, and
 * not destroyed by explosions (answers 130, 134, 137 and 153).
 */
public final class NetworkTerminalBlock extends Block implements EntityBlock {
  public static final MapCodec<NetworkTerminalBlock> CODEC = simpleCodec(NetworkTerminalBlock::new);

  public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

  public NetworkTerminalBlock(Properties properties) {
    super(properties);
    registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
  }

  @Override
  protected MapCodec<NetworkTerminalBlock> codec() {
    return CODEC;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
  }

  /** The screen faces the player who places it (answer 251). */
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new NetworkTerminalBlockEntity(pos, state);
  }

  /** In survival, only the owner breaks it, and the client shows the same (answer 137). */
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
    if (!level.isClientSide && !player.isCreative() && !ownedBy(level, pos, player)) {
      return false;
    }
    return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
  }

  /** Gone by any means, the terminal frees its owner to place another (answers 137 and 253). */
  @Override
  protected void onRemove(
      BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
    if (!level.isClientSide && !state.is(newState.getBlock())) {
      terminalAt(level, pos)
          .flatMap(NetworkTerminalBlockEntity::owner)
          .ifPresent(owner ->
              RedweaveServer.running()
                  .ifPresent(server ->
                      server
                          .terminal()
                          .breakTerminal()
                          .broke(new PlayerId(owner), BlockLocations.of(level, pos))));
    }
    super.onRemove(state, level, pos, newState, movedByPiston);
  }

  /** Only an empty hand uses the terminal; any item goes straight to its own use (answer 130). */
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

  /** The owner opens the terminal; anyone else is told it is not theirs (answers 130 and 153). */
  @Override
  protected InteractionResult useWithoutItem(
      BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
    if (level.isClientSide) {
      return InteractionResult.SUCCESS;
    }
    if (!ownedBy(level, pos, player)) {
      player.displayClientMessage(TerminalMessages.notOwner(), true);
    } else if (player instanceof ServerPlayer viewer) {
      viewer.openMenu(
          new SimpleMenuProvider(
              (id, inventory, opener) -> new TerminalMenu(id, viewer, pos),
              state.getBlock().getName()),
          extra -> TerminalMenu.writeOpening(extra, viewer, pos));
    }
    return InteractionResult.CONSUME;
  }

  private static boolean ownedBy(BlockGetter level, BlockPos pos, Player player) {
    return terminalAt(level, pos).map(terminal -> terminal.isOwner(player)).orElse(true);
  }

  private static Optional<NetworkTerminalBlockEntity> terminalAt(BlockGetter level, BlockPos pos) {
    return level.getBlockEntity(pos) instanceof NetworkTerminalBlockEntity terminal
        ? Optional.of(terminal)
        : Optional.empty();
  }
}
