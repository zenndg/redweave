package io.github.marcelofabianov.redweave.block;

import com.mojang.logging.LogUtils;
import io.github.marcelofabianov.redweave.config.RedweaveConfig;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.module.SignalTimer;
import io.github.marcelofabianov.redweave.domain.module.TimerCycle;
import io.github.marcelofabianov.redweave.domain.network.ModuleId;
import io.github.marcelofabianov.redweave.domain.network.Names;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.slf4j.Logger;

/**
 * The NBT of a Signal Timer block, under the keys of answer 411, part of the save (rule 15). Each
 * part is read on its own: an invalid one is dropped with a warning, and the rest is kept (14).
 */
final class TimerSave {
  static final String OUTPUT = "output";
  static final String PAUSE = "pause";

  private static final String MODULE_ID = "module_id";
  private static final String OWNER = "owner";
  private static final String NAME = "name";
  private static final String NETWORK = "network";
  private static final String RUNNING = "running";
  private static final String INTERVAL = "interval_ticks";
  private static final String PULSE = "pulse_ticks";
  private static final String ELAPSED = "elapsed_ticks";

  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String DROPPED = "Dropped an invalid {} from a Signal Timer: {}";
  private static final String MISSING = "missing";

  private TimerSave() {}

  static void writeModule(CompoundTag tag, SignalModule module) {
    tag.putUUID(MODULE_ID, module.id().value());
    tag.putUUID(OWNER, module.owner().value());
    module.name().ifPresent(name -> tag.putString(NAME, name));
    module.network().ifPresent(network -> tag.putUUID(NETWORK, network.value()));
  }

  static void writeTimer(CompoundTag tag, SignalTimer timer) {
    tag.putBoolean(RUNNING, timer.isRunning());
    tag.putInt(INTERVAL, timer.cycle().intervalTicks());
    tag.putInt(PULSE, timer.cycle().pulseTicks());
    tag.putInt(ELAPSED, timer.elapsedTicks());
  }

  static void writeConnection(CompoundTag tag, String key, Connection connection) {
    tag.putString(key, connection.key());
  }

  /** Without its ID or owner the module is dropped, and nobody owns the plate. */
  static Optional<SignalModule> readModule(CompoundTag tag) {
    Optional<UUID> id = uuid(tag, MODULE_ID);
    Optional<UUID> owner = uuid(tag, OWNER);
    if (id.isEmpty() || owner.isEmpty()) {
      LOGGER.warn(DROPPED, id.isEmpty() ? MODULE_ID : OWNER, MISSING);
      return Optional.empty();
    }
    return Optional.of(
        SignalModule.restore(
            new ModuleId(id.get()),
            new PlayerId(owner.get()),
            name(tag),
            uuid(tag, NETWORK).map(NetworkId::new)));
  }

  /**
   * The timer as saved, or none when its cycle is invalid, so the caller starts a new one. The
   * limits of the config are applied later, when the block loads (answer 425).
   */
  static Optional<SignalTimer> readTimer(CompoundTag tag) {
    TimerCycle cycle;
    try {
      cycle = new TimerCycle(tag.getInt(INTERVAL), tag.getInt(PULSE));
    } catch (IllegalArgumentException invalid) {
      LOGGER.warn(DROPPED, INTERVAL, invalid.getMessage());
      return Optional.empty();
    }
    boolean running = tag.getBoolean(RUNNING);
    int elapsed = running ? tag.getInt(ELAPSED) : 0;
    if (elapsed < 0 || elapsed >= cycle.intervalTicks()) {
      LOGGER.warn(DROPPED, ELAPSED, elapsed);
      elapsed = 0;
    }
    return Optional.of(SignalTimer.restore(cycle, running, elapsed));
  }

  /** An unknown connection goes back to the edge, where a new timer starts (answer 390). */
  static Connection readConnection(CompoundTag tag, String key) {
    if (!tag.contains(key, Tag.TAG_STRING)) {
      return Connection.EDGE;
    }
    Optional<Connection> connection = Connection.fromKey(tag.getString(key));
    if (connection.isEmpty()) {
      LOGGER.warn(DROPPED, key, tag.getString(key));
    }
    return connection.orElse(Connection.EDGE);
  }

  /** A saved name is kept up to the ceiling of the config, not its current limit (answer 93). */
  private static Optional<String> name(CompoundTag tag) {
    if (!tag.contains(NAME, Tag.TAG_STRING)) {
      return Optional.empty();
    }
    String saved = tag.getString(NAME);
    Optional<String> name = Names.valid(saved, RedweaveConfig.MAX_NAME).filter(saved::equals);
    if (name.isEmpty()) {
      LOGGER.warn(DROPPED, NAME, saved);
    }
    return name;
  }

  private static Optional<UUID> uuid(CompoundTag tag, String key) {
    return tag.hasUUID(key) ? Optional.of(tag.getUUID(key)) : Optional.empty();
  }
}
