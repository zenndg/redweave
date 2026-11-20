package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.block.BlockLocations;
import io.github.zenndg.redweave.block.part.ModulePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.domain.module.Connection;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.server.level.ServerPlayer;

/**
 * How every Signal Module Screen keeps its values among the numbers the game syncs: a state as its
 * order plus one, where 0 is none, and a connection as the edge or its color plus one.
 */
final class ModuleScreenValues {
  /** No module or no network. */
  static final int NONE = 0;

  /** A connection on the edge; a channel is its color plus one. */
  static final int EDGE = 0;

  /** The edge and the 16 channels. */
  static final int CONNECTIONS = ChannelColor.values().length + 1;

  private ModuleScreenValues() {}

  /** The state of the network seen from the plate, as a node, or none (answer 385). */
  static int networkState(ServerPlayer viewer, PartSpot spot, ModulePart block) {
    Optional<SignalModule> module = block.module().filter(found -> found.network().isPresent());
    Optional<RedweaveServer> server = RedweaveServer.running();
    if (module.isEmpty() || server.isEmpty()) {
      return NONE;
    }
    return server
            .get()
            .modules()
            .show()
            .show(module.get(), BlockLocations.of(viewer.level(), spot.pos()))
            .state()
            .ordinal()
        + 1;
  }

  static int code(Connection connection) {
    return connection.channel().map(color -> color.ordinal() + 1).orElse(EDGE);
  }

  static Connection connectionOf(int code) {
    return colorAt(code - 1).map(Connection::on).orElse(Connection.EDGE);
  }

  static Optional<ChannelColor> colorAt(int index) {
    return index >= 0 && index < ChannelColor.values().length
        ? Optional.of(ChannelColor.values()[index])
        : Optional.empty();
  }

  static <T> Optional<T> valueAt(T[] values, int code) {
    return code > NONE && code <= values.length ? Optional.of(values[code - 1]) : Optional.empty();
  }
}
