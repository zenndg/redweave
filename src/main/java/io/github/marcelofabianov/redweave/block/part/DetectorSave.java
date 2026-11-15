package io.github.marcelofabianov.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.marcelofabianov.redweave.domain.module.WeatherMode;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import org.slf4j.Logger;

/**
 * The NBT of a Weather Detector besides its module, under the keys of answer 467, part of the save
 * (rule 15): its mode and its output. A damaged mode falls back to the rain, with a warning (rule
 * 14). The weather is never saved: it is read again on the first tick.
 */
final class DetectorSave {
  static final String OUTPUT = "output";

  private static final String MODE = "mode";
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String DROPPED = "Dropped an invalid {} from a Weather Detector: {}";

  private DetectorSave() {}

  static void writeMode(CompoundTag tag, WeatherMode mode) {
    tag.putString(MODE, mode.key());
  }

  static WeatherMode readMode(CompoundTag tag) {
    String key = tag.getString(MODE);
    Optional<WeatherMode> mode = WeatherMode.fromKey(key);
    if (mode.isEmpty()) {
      LOGGER.warn(DROPPED, MODE, key);
      return WeatherMode.RAIN;
    }
    return mode.get();
  }
}
