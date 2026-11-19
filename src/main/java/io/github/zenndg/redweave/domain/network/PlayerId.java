package io.github.zenndg.redweave.domain.network;

import java.util.Objects;
import java.util.UUID;

/** A player, by the UUID the game gives them. It names the owner of a network (answer 22). */
public record PlayerId(UUID value) {
  private static final String MISSING = "A player ID needs a value";

  public PlayerId {
    Objects.requireNonNull(value, MISSING);
  }
}
