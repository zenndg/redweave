package io.github.marcelofabianov.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.slf4j.Logger;

/**
 * The strength of a source module in its NBT, under `strength`, from 1 to 15, as the Signal Lever
 * and the Signal Button keep it (answers 584 and 591). A missing one is 15, and an invalid one goes
 * back to 15 with a warning (rule 14).
 */
final class StrengthSave {
  static final String STRENGTH = "strength";

  private static final int WEAKEST = 1;
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String DROPPED = "Dropped an invalid strength from a {}: {}";

  private StrengthSave() {}

  static void write(CompoundTag tag, Signal strength) {
    tag.putInt(STRENGTH, strength.strength());
  }

  /** The saved strength of the module, named in the warning, or 15. */
  static Signal read(CompoundTag tag, String module) {
    if (!tag.contains(STRENGTH, Tag.TAG_INT)) {
      return Signal.MAX;
    }
    int strength = tag.getInt(STRENGTH);
    if (strength < WEAKEST || strength > Signal.MAX.strength()) {
      LOGGER.warn(DROPPED, module, strength);
      return Signal.MAX;
    }
    return new Signal(strength);
  }
}
