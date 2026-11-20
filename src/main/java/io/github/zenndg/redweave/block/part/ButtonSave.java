package io.github.zenndg.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.zenndg.redweave.domain.module.ButtonTime;
import io.github.zenndg.redweave.domain.module.SignalButton;
import io.github.zenndg.redweave.domain.signal.Signal;
import net.minecraft.nbt.CompoundTag;
import org.slf4j.Logger;

/**
 * The NBT of a Signal Button besides its module, part of the save (rule 15): `on`, `strength`,
 * `interval`, its on time in ticks, `remaining`, the ticks left while on, and its output (answer
 * 591). An invalid value goes back to a safe one with a warning (rule 14): the on time to 1 s, and
 * the ticks left to a button turned off.
 */
final class ButtonSave {

  private static final String ON = "on";
  static final String INTERVAL = "interval";
  private static final String REMAINING = "remaining";
  private static final String MODULE = "Signal Button";
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String DROPPED = "Dropped an invalid {} from a Signal Button: {}";

  private ButtonSave() {}

  static void writeButton(CompoundTag tag, SignalButton button) {
    tag.putBoolean(ON, button.on());
    StrengthSave.write(tag, button.strength());
    tag.putInt(INTERVAL, button.time().ticks());
    tag.putInt(REMAINING, button.remaining());
  }

  static SignalButton readButton(CompoundTag tag) {
    Signal strength = StrengthSave.read(tag, MODULE);
    ButtonTime time = time(tag);
    int remaining = tag.getInt(REMAINING);
    boolean on = tag.getBoolean(ON);
    if (on != remaining > 0) {
      LOGGER.warn(DROPPED, REMAINING, remaining);
      return new SignalButton(false, strength, time, 0);
    }
    return new SignalButton(on, strength, time, remaining);
  }

  private static ButtonTime time(CompoundTag tag) {
    int ticks = tag.getInt(INTERVAL);
    if (ticks < ButtonTime.SHORTEST_TICKS) {
      LOGGER.warn(DROPPED, INTERVAL, ticks);
      return new ButtonTime(ButtonTime.PLACED_TICKS);
    }
    return new ButtonTime(ticks);
  }
}
