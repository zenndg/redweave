package io.github.marcelofabianov.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.LampMode;
import io.github.marcelofabianov.redweave.domain.module.SignalLamp;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.slf4j.Logger;

/**
 * The NBT of a Signal Lamp besides its module, part of the save (rule 15): its strength, the mode
 * of its control, whether it is switched off and the control of the last tick, its intensity and
 * control channels, absent when it has none, and its light, which the client draws (answers 650 to
 * 658). An invalid value goes back to a safe one with a warning (rule 14).
 */
final class LampSave {
  static final String INTENSITY = "intensity";
  static final String CONTROL = "control";

  private static final String MODE = "mode";
  private static final String OFF = "off";
  private static final String LAST_CONTROL = "last_control";
  private static final String LIGHT = "light";
  private static final String MODULE = "Signal Lamp";
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String DROPPED = "Dropped an invalid {} from a Signal Lamp: {}";

  private LampSave() {}

  static void write(CompoundTag tag, SignalLamp lamp, int light) {
    StrengthSave.write(tag, lamp.strength());
    tag.putString(MODE, lamp.mode().key());
    tag.putBoolean(OFF, lamp.switchedOff());
    tag.putBoolean(LAST_CONTROL, lamp.controlWasOn());
    tag.putInt(LIGHT, light);
  }

  static SignalLamp read(CompoundTag tag) {
    LampMode mode = LampMode.fromKey(tag.getString(MODE)).orElse(LampMode.HOLD);
    if (tag.contains(MODE, Tag.TAG_STRING) && LampMode.fromKey(tag.getString(MODE)).isEmpty()) {
      LOGGER.warn(DROPPED, MODE, tag.getString(MODE));
    }
    return new SignalLamp(
        StrengthSave.read(tag, MODULE), mode, tag.getBoolean(OFF), tag.getBoolean(LAST_CONTROL));
  }

  /** The light kept from 0 to 15; an invalid one is none (rule 14). */
  static int readLight(CompoundTag tag) {
    int light = tag.getInt(LIGHT);
    if (light < 0 || light > Signal.MAX.strength()) {
      LOGGER.warn(DROPPED, LIGHT, light);
      return 0;
    }
    return light;
  }

  /** A channel of the lamp, kept by the key of its color; none is the absence of the key. */
  static void writeChannel(CompoundTag tag, String key, Connection connection) {
    connection.channel().ifPresent(color -> tag.putString(key, color.key()));
  }

  static Connection readChannel(CompoundTag tag, String key) {
    if (!tag.contains(key, Tag.TAG_STRING)) {
      return Connection.EDGE;
    }
    Optional<ChannelColor> color = ChannelColor.fromKey(tag.getString(key));
    if (color.isEmpty()) {
      LOGGER.warn(DROPPED, key, tag.getString(key));
    }
    return color.map(Connection::on).orElse(Connection.EDGE);
  }
}
