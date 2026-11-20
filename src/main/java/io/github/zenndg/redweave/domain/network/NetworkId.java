package io.github.zenndg.redweave.domain.network;

import java.util.Objects;
import java.util.UUID;

/**
 * The ID of a network, unique on the server and never changed. It goes to the saves and to the item
 * of the controller, and the caller generates it (answers 24 and 31).
 */
public record NetworkId(UUID value) {
  private static final String MISSING = "A network ID needs a value";

  public NetworkId {
    Objects.requireNonNull(value, MISSING);
  }
}
