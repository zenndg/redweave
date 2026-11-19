package io.github.zenndg.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.zenndg.redweave.domain.module.PlateMode;
import io.github.zenndg.redweave.domain.module.SignalPressurePlate;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import org.slf4j.Logger;

/**
 * The NBT of a Signal Pressure Plate besides its module, part of the save (rule 15): whether
 * something stands on it, under `on`, its strength, its mode, under `mode`, as `players`,
 * `passive`, `hostile` or `items`, its output (answer 608), and the ticks left of the pulse of a
 * Signal Remote, under `remote_pulse`, only while it lasts, so a plate with no pulse saves as
 * before (answer 793). An invalid mode goes back to players, and a negative pulse to none, with a
 * warning (rule 14).
 */
final class PressurePlateSave {

  private static final String ON = "on";
  static final String REMOTE_PULSE = "remote_pulse";
  private static final int NO_PULSE = 0;
  private static final String DROPPED_PULSE = "Dropped a negative pulse of a plate: {}";
  static final String MODE = "mode";
  private static final String MODULE = "Signal Pressure Plate";
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String DROPPED = "Dropped an invalid mode of a Signal Pressure Plate: {}";

  private PressurePlateSave() {}

  static void writePlate(CompoundTag tag, SignalPressurePlate plate) {
    tag.putBoolean(ON, plate.stood());
    StrengthSave.write(tag, plate.strength());
    tag.putString(MODE, plate.mode().key());
    if (plate.pulse() > NO_PULSE) {
      tag.putInt(REMOTE_PULSE, plate.pulse());
    }
  }

  static SignalPressurePlate readPlate(CompoundTag tag) {
    String saved = tag.getString(MODE);
    Optional<PlateMode> mode = PlateMode.fromKey(saved);
    if (mode.isEmpty()) {
      LOGGER.warn(DROPPED, saved);
    }
    return new SignalPressurePlate(
        tag.getBoolean(ON),
        StrengthSave.read(tag, MODULE),
        mode.orElse(SignalPressurePlate.placed().mode()),
        pulseOf(tag));
  }

  private static int pulseOf(CompoundTag tag) {
    int saved = tag.getInt(REMOTE_PULSE);
    if (saved < NO_PULSE) {
      LOGGER.warn(DROPPED_PULSE, saved);
      return NO_PULSE;
    }
    return saved;
  }
}
