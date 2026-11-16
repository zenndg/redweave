package io.github.marcelofabianov.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.marcelofabianov.redweave.domain.module.PlateMode;
import io.github.marcelofabianov.redweave.domain.module.SignalPressurePlate;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import org.slf4j.Logger;

/**
 * The NBT of a Signal Pressure Plate besides its module, part of the save (rule 15): whether it is
 * on, under `on`, its strength, its mode, under `mode`, as `players`, `passive`, `hostile` or
 * `items`, and its output (answer 608). An invalid mode goes back to players with a warning (rule
 * 14).
 */
final class PressurePlateSave {

  private static final String ON = "on";
  static final String MODE = "mode";
  private static final String MODULE = "Signal Pressure Plate";
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String DROPPED = "Dropped an invalid mode of a Signal Pressure Plate: {}";

  private PressurePlateSave() {}

  static void writePlate(CompoundTag tag, SignalPressurePlate plate) {
    tag.putBoolean(ON, plate.on());
    StrengthSave.write(tag, plate.strength());
    tag.putString(MODE, plate.mode().key());
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
        mode.orElse(SignalPressurePlate.placed().mode()));
  }
}
