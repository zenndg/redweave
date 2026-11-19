package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.BlockLocation;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.RemoteInput;
import java.util.Objects;

/**
 * A Signal Remote that is on this tick, in the inventory of an online player, with where that
 * player stands, so its reach is measured from the player (answers 354 to 356).
 */
public record RemoteSighting(NetworkId network, RemoteInput tablet, BlockLocation holder) {
  private static final String MISSING = "A sighting needs the network, the tablet and the holder";

  public RemoteSighting {
    Objects.requireNonNull(network, MISSING);
    Objects.requireNonNull(tablet, MISSING);
    Objects.requireNonNull(holder, MISSING);
  }
}
