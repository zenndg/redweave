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
import net.minecraft.world.level.block.state.properties.EnumProperty;

/**
 * The Weather Detector: _docs/funcionalidades/weather-detector.md. A module plate with only its
 * output, on the front edge (answer 461). The plate is the one of every module; this adds the
 * symbol of the mode, its block entity and its tick (answer 471).
 */
public final class WeatherDetectorBlock extends ModulePlateBlock {
  public static final MapCodec<WeatherDetectorBlock> CODEC = simpleCodec(WeatherDetectorBlock::new);

  private static final String MODE_NAME = "mode";

  /** The mode, whose symbol the top shows: a cloud with rain, a lightning or a sun (471). */
  public static final EnumProperty<DetectorMode> MODE =
      EnumProperty.create(MODE_NAME, DetectorMode.class);

  public WeatherDetectorBlock(Properties properties) {
    super(properties);
    registerDefaultState(
        stateDefinition
            .any()
            .setValue(FACING, Direction.UP)
            .setValue(FRONT, Direction.NORTH)
            .setValue(POWERED, false)
            .setValue(MODE, DetectorMode.RAIN));
  }

  @Override
  protected MapCodec<WeatherDetectorBlock> codec() {
    return CODEC;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING, FRONT, POWERED, MODE);
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new WeatherDetectorBlockEntity(pos, state);
  }

  /** Only the server ticks the detector; the client sees its look in the blockstate (471). */
  @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> type) {
    if (level.isClientSide || type != RedweaveBlockEntities.WEATHER_DETECTOR.get()) {
      return null;
    }
    return (tickLevel, pos, tickState, block) -> {
      if (block instanceof WeatherDetectorBlockEntity detector) {
        DetectorTicks.tick(tickLevel, pos, tickState, detector);
      }
    };
  }
}
