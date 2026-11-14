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
 * The Signal Inverter: _docs/funcionalidades/signal-inverter.md. A module plate, with its input on
 * the back edge and its output on the front one (answer 441). The plate is the one of every module;
 * this adds the burnt look, its block entity and its tick (answers 452 and 455).
 */
public final class SignalInverterBlock extends ModulePlateBlock {
  public static final MapCodec<SignalInverterBlock> CODEC = simpleCodec(SignalInverterBlock::new);

  /** Burnt out by a loop: the arrow turns dark gray, and `powered` stays false (452 and 455). */
  public static final BooleanProperty BURNT = BooleanProperty.create("burnt");

  public SignalInverterBlock(Properties properties) {
    super(properties);
    registerDefaultState(
        stateDefinition
            .any()
            .setValue(FACING, Direction.UP)
            .setValue(FRONT, Direction.NORTH)
            .setValue(POWERED, false)
            .setValue(BURNT, false));
  }

  @Override
  protected MapCodec<SignalInverterBlock> codec() {
    return CODEC;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, FRONT, POWERED, BURNT);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new SignalInverterBlockEntity(pos, state);
  }

  /** Only the server ticks the inverter; the client sees its look in the blockstate (452). */
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> type) {
    if (level.isClientSide || type != RedweaveBlockEntities.SIGNAL_INVERTER.get()) {
      return null;
    }
    return (tickLevel, pos, tickState, block) -> {
      if (block instanceof SignalInverterBlockEntity inverter) {
        InverterTicks.tick(tickLevel, pos, tickState, inverter);
      }
    };
  }
}
