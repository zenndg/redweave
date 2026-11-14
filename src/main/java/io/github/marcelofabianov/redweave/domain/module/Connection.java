package io.github.marcelofabianov.redweave.domain.module;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Objects;
import java.util.Optional;

/**
 * An input or an output of a module: the fixed edge of the plate, or a channel of the network of
 * the module, never both (answers 376 and 420). It is not a gate.
 */
public record Connection(Optional<ChannelColor> channel) {
  /** The fixed edge of the plate that the module defines (answer 377). */
  public static final Connection EDGE = new Connection(Optional.empty());

  private static final String EDGE_KEY = "edge";
  private static final String MISSING = "A connection needs its channel, or none for the edge";

  public Connection {
    Objects.requireNonNull(channel, MISSING);
  }

  public static Connection on(ChannelColor color) {
    return new Connection(Optional.of(color));
  }

  public boolean isEdge() {
    return channel.isEmpty();
  }

  /** `edge` or the key of the color, as the saves keep it (answer 411). */
  public String key() {
    return channel.map(ChannelColor::key).orElse(EDGE_KEY);
  }

  /** An unknown key, as from a damaged save, gives no connection instead of breaking the game. */
  public static Optional<Connection> fromKey(String key) {
    if (EDGE_KEY.equals(key)) {
      return Optional.of(EDGE);
    }
    return ChannelColor.fromKey(key).map(Connection::on);
  }
}
