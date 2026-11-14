package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.domain.module.Weather;
import io.github.marcelofabianov.redweave.domain.module.WeatherDetector;
import io.github.marcelofabianov.redweave.server.ModuleServices;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Each tick of a loaded Weather Detector: it reads the weather of its dimension, gives it to its
 * rule in the domain, writes its output and shows its look (weather-detector.md). An unloaded
 * detector gets no tick and is not sighted, as every module (answers 386 and 466).
 */
final class DetectorTicks {
  private DetectorTicks() {}

  static void tick(Level level, BlockPos pos, BlockState state, WeatherDetectorBlockEntity block) {
    Optional<ModuleServices> modules = ModuleTicks.start(level, pos, block);
    if (modules.isEmpty()) {
      return;
    }
    Weather weather = weatherOf(level);
    block.read(weather);
    WeatherDetector detector = block.detector();
    ModuleTicks.write(modules.get(), level, pos, state, block, detector.output(weather));
    look(level, pos, state, detector, weather);
  }

  /**
   * The weather of the dimension, whatever the sky over the plate (answer 458). The game moves the
   * weather only where there is sky light, so the Nether and the End have none (answer 464).
   */
  private static Weather weatherOf(Level level) {
    return Weather.of(level.dimensionType().hasSkyLight(), level.isRaining(), level.isThundering());
  }

  /** The arrow lit and the symbol of the mode, only sent to the clients (answer 471). */
  private static void look(
      Level level, BlockPos pos, BlockState state, WeatherDetector detector, Weather weather) {
    BlockState shown =
        state
            .setValue(WeatherDetectorBlock.POWERED, detector.output(weather).isOn())
            .setValue(WeatherDetectorBlock.MODE, DetectorMode.of(detector.mode()));
    if (shown != state) {
      level.setBlock(pos, shown, Block.UPDATE_CLIENTS);
    }
  }
}
