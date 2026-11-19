package io.github.zenndg.redweave.block.part;

import com.mojang.logging.LogUtils;
import io.github.zenndg.redweave.config.RedweaveConfig;
import io.github.zenndg.redweave.domain.module.Connection;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.ModuleId;
import io.github.zenndg.redweave.domain.network.Names;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.slf4j.Logger;

/**
 * The NBT every Signal Module block shares, under the keys of answer 411, part of the save (rule
 * 15): the module, the network it is registered in (answers 811 and 817) and its connections. Each
 * part is read on its own: an invalid one is dropped with a warning, and the rest is kept (rule
 * 14).
 */
final class ModuleSave {
  private static final String MODULE_ID = "module_id";
  private static final String OWNER = "owner";
  private static final String NAME = "name";
  private static final String NETWORK = "network";
  private static final String REGISTERED = "registered";

  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String DROPPED = "Dropped an invalid {} from a Signal Module: {}";
  private static final String MISSING = "missing";

  private ModuleSave() {}

  static void writeModule(CompoundTag tag, SignalModule module) {
    tag.putUUID(MODULE_ID, module.id().value());
    tag.putUUID(OWNER, module.owner().value());
    module.name().ifPresent(name -> tag.putString(NAME, name));
    module.network().ifPresent(network -> tag.putUUID(NETWORK, network.value()));
  }

  /** The network the module is registered in, absent before its first register (811, 817). */
  static void writeRegistered(CompoundTag tag, Optional<NetworkId> registeredIn) {
    registeredIn.ifPresent(network -> tag.putUUID(REGISTERED, network.value()));
  }

  static Optional<NetworkId> readRegistered(CompoundTag tag) {
    return uuid(tag, REGISTERED).map(NetworkId::new);
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
