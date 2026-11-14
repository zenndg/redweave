package io.github.marcelofabianov.redweave.block;

import com.mojang.serialization.MapCodec;
import io.github.marcelofabianov.redweave.registry.RedweaveBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * The Signal Timer: _docs/funcionalidades/signal-timer.md. A module plate, with its output on the
 * front edge and its `Pause` on the back one (answers 383, 416 and 422). The plate is the one of
 * every module; this adds the running LED, its block entity and its tick (rule 10).
 */
public final class SignalTimerBlock extends ModulePlateBlock {
  public static final MapCodec<SignalTimerBlock> CODEC = simpleCodec(SignalTimerBlock::new);

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
}
