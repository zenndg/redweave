package io.github.marcelofabianov.redweave.block.part;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import org.slf4j.Logger;

/**
 * The NBT of a Signal Inverter besides its module, under the keys of answer 450, part of the save
 * (rule 15): its two connections and what is left of its burnout. A negative burnout is dropped
 * with a warning (rule 14).
 */
final class InverterSave {
  static final String INPUT = "input";
  static final String OUTPUT = "output";

  private static final String BURNOUT = "burnout_ticks";
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String DROPPED = "Dropped an invalid {} from a Signal Inverter: {}";

  private InverterSave() {}

  static void writeBurnout(CompoundTag tag, int ticks) {
    tag.putInt(BURNOUT, ticks);
  }

  static int readBurnout(CompoundTag tag) {
    int ticks = tag.getInt(BURNOUT);
    if (ticks < 0) {
      LOGGER.warn(DROPPED, BURNOUT, ticks);
      return 0;
    }
    return ticks;
  }
}
