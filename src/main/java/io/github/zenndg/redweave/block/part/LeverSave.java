package io.github.zenndg.redweave.block.part;

import io.github.zenndg.redweave.domain.module.SignalLever;
import net.minecraft.nbt.CompoundTag;

/**
 * The NBT of a Signal Lever besides its module, part of the save (rule 15): whether it is on, under
 * `on` (answer 579), its strength (answer 584) and its output. An invalid strength goes back to 15
 * with a warning (rule 14).
 */
final class LeverSave {

  private static final String ON = "on";
  private static final String MODULE = "Signal Lever";

  private LeverSave() {}

  static void writeLever(CompoundTag tag, SignalLever lever) {
    tag.putBoolean(ON, lever.on());
    StrengthSave.write(tag, lever.strength());
  }

  static SignalLever readLever(CompoundTag tag) {
    return new SignalLever(tag.getBoolean(ON), StrengthSave.read(tag, MODULE));
  }
}
