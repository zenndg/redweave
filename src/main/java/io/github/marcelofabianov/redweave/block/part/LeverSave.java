package io.github.marcelofabianov.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.marcelofabianov.redweave.domain.module.SignalLever;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.slf4j.Logger;

/**
 * The NBT of a Signal Lever besides its module, part of the save (rule 15): whether it is on, under
 * `on` (answer 579), its strength and its output. An invalid strength goes back to 15 with a
 * warning (rule 14).
 */
final class LeverSave {
  static final String OUTPUT = "output";

  private static final String ON = "on";
  private static final String STRENGTH = "strength";
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String DROPPED = "Dropped an invalid strength from a Signal Lever: {}";

  private LeverSave() {}

  static void writeLever(CompoundTag tag, SignalLever lever) {
    tag.putBoolean(ON, lever.on());
    tag.putInt(STRENGTH, lever.strength().strength());
  }

  static SignalLever readLever(CompoundTag tag) {
    return new SignalLever(tag.getBoolean(ON), strength(tag));
  }

  private static Signal strength(CompoundTag tag) {
    if (!tag.contains(STRENGTH, Tag.TAG_INT)) {
      return Signal.MAX;
    }
    int strength = tag.getInt(STRENGTH);
    if (strength < SignalLever.WEAKEST || strength > Signal.MAX.strength()) {
      LOGGER.warn(DROPPED, strength);
      return Signal.MAX;
    }
    return new Signal(strength);
  }
}
