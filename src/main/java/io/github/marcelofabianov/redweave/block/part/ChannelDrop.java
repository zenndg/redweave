package io.github.marcelofabianov.redweave.block.part;

import io.github.marcelofabianov.redweave.domain.module.Connection;

/**
 * Takes the connections of a module off their channels, for a paste whose network it cannot join,
 * and remembers whether any was on one, so the player is told (answer 732).
 */
public final class ChannelDrop implements ConnectionMapper {
  private boolean dropped;

  /** The connection, or the edge, which stands for none, if it was on a channel. */
  @Override
  public Connection of(Connection connection) {
    if (connection.isEdge()) {
      return connection;
    }
    dropped = true;
    return Connection.EDGE;
  }

  public boolean dropped() {
    return dropped;
  }
}
