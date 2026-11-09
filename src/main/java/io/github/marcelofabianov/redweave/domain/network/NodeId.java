package io.github.marcelofabianov.redweave.domain.network;

import java.util.Objects;
import java.util.UUID;

/** The ID of a node, unique on the server. It goes to the saves, and the caller generates it. */
public record NodeId(UUID value) {
  private static final String MISSING = "A node ID needs a value";

  public NodeId {
    Objects.requireNonNull(value, MISSING);
  }
}
