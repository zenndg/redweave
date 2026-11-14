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
 * The Signal Randomizer: _docs/funcionalidades/signal-randomizer.md. A module plate with only its
 * output, on the front edge (answer 479). The plate is the one of every module; this adds the LED
 * of `Running`, its block entity and its tick (answer 488).
 */
public final class SignalRandomizerBlock extends ModulePlateBlock {
  public static final MapCodec<SignalRandomizerBlock> CODEC =
      simpleCodec(SignalRandomizerBlock::new);

  private static final String RUNNING_NAME = "running";

  /** Running: the green LED of the top is lit, as on the timer (answer 488). */
  public static final BooleanProperty RUNNING = BooleanProperty.create(RUNNING_NAME);

  public SignalRandomizerBlock(Properties properties) {
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
  protected MapCodec<SignalRandomizerBlock> codec() {
    return CODEC;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, FRONT, POWERED, RUNNING);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new SignalRandomizerBlockEntity(pos, state);
  }

  /** Only the server ticks the randomizer; the client sees its look in the blockstate (488). */
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> type) {
    if (level.isClientSide || type != RedweaveBlockEntities.SIGNAL_RANDOMIZER.get()) {
      return null;
    }
    return (tickLevel, pos, tickState, block) -> {
      if (block instanceof SignalRandomizerBlockEntity randomizer) {
        RandomizerTicks.tick(tickLevel, pos, tickState, randomizer);
      }
    };
  }
}
