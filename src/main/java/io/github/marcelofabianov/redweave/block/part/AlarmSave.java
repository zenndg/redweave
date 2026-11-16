package io.github.marcelofabianov.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.marcelofabianov.redweave.domain.module.AlarmListeners;
import io.github.marcelofabianov.redweave.domain.module.AlarmSettings;
import io.github.marcelofabianov.redweave.domain.module.SignalAlarm;
import java.util.Locale;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.slf4j.Logger;

/**
 * The NBT of a Signal Alarm besides its module, part of the save (rule 15): who hears it,
 * `listeners` (`owner`, `network` or `range`); its `repeats`, its `interval` and its `duration`, in
 * ticks; the alarm under way, `remaining`, `rings_left` and `until_ring`; the input of the last
 * tick, `last_input`; and its `input`. An invalid value goes back to a safe one with a warning
 * (rule 14): the settings to the ones of an alarm just placed, and the alarm under way to none.
 */
final class AlarmSave {
  static final String INPUT = InverterSave.INPUT;
  static final String LISTENERS = "listeners";
  static final String REPEATS = "repeats";
  static final String INTERVAL = "interval";
  static final String DURATION = "duration";

  private static final String REMAINING = "remaining";
  private static final String RINGS_LEFT = "rings_left";
  private static final String UNTIL_RING = "until_ring";
  private static final String LAST_INPUT = "last_input";
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String DROPPED = "Dropped an invalid {} from a Signal Alarm: {}";

  private AlarmSave() {}

  static void write(CompoundTag tag, SignalAlarm alarm) {
    AlarmSettings settings = alarm.settings();
    tag.putString(LISTENERS, settings.listeners().name().toLowerCase(Locale.ROOT));
    tag.putInt(REPEATS, settings.repeats());
    tag.putInt(INTERVAL, settings.intervalTicks());
    tag.putInt(DURATION, settings.durationTicks());
    tag.putInt(REMAINING, alarm.remaining());
    tag.putInt(RINGS_LEFT, alarm.ringsLeft());
    tag.putInt(UNTIL_RING, alarm.untilRing());
    tag.putBoolean(LAST_INPUT, alarm.wasOn());
  }

  static SignalAlarm read(CompoundTag tag) {
    AlarmSettings settings = settings(tag);
    int remaining = tag.getInt(REMAINING);
    int rings = tag.getInt(RINGS_LEFT);
    int until = tag.getInt(UNTIL_RING);
    if (remaining < 0 || rings < 0 || until < 0) {
      LOGGER.warn(DROPPED, REMAINING, remaining);
      return new SignalAlarm(settings, 0, 0, 0, tag.getBoolean(LAST_INPUT));
    }
    return new SignalAlarm(settings, remaining, rings, until, tag.getBoolean(LAST_INPUT));
  }

  private static AlarmSettings settings(CompoundTag tag) {
    int repeats = tag.contains(REPEATS, Tag.TAG_INT) ? tag.getInt(REPEATS) : 1;
    int interval = tag.getInt(INTERVAL);
    int duration = tag.getInt(DURATION);
    if (repeats < 1
        || interval < AlarmSettings.SHORTEST_TICKS
        || duration < AlarmSettings.SHORTEST_TICKS) {
      LOGGER.warn(DROPPED, DURATION, duration);
      return new AlarmSettings(
          listeners(tag),
          AlarmSettings.PLACED_REPEATS,
          AlarmSettings.PLACED_INTERVAL_TICKS,
          AlarmSettings.PLACED_DURATION_TICKS);
    }
    return new AlarmSettings(listeners(tag), repeats, interval, duration);
  }

  private static AlarmListeners listeners(CompoundTag tag) {
    String saved = tag.getString(LISTENERS);
    for (AlarmListeners listeners : AlarmListeners.values()) {
      if (listeners.name().toLowerCase(Locale.ROOT).equals(saved)) {
        return listeners;
      }
    }
    LOGGER.warn(DROPPED, LISTENERS, saved);
    return AlarmListeners.OWNER;
  }
}
