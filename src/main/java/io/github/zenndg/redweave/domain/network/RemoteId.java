package io.github.zenndg.redweave.domain.network;

import java.util.Objects;
import java.util.UUID;

/**
 * The ID of a Signal Remote, given on its first link. Copies of one tablet share it and count as
 * one source (answers 27 and 358). The caller generates it.
 */
public record RemoteId(UUID value) {
  private static final String MISSING = "A remote ID needs a value";

  public RemoteId {
    Objects.requireNonNull(value, MISSING);
  }
}
