package io.github.zenndg.redweave.domain.module;

import java.util.Objects;
import java.util.UUID;

/** The ID of a Signal Card, which every card made gets new (answer 668). */
public record CardId(UUID value) {
  private static final String MISSING = "A card ID needs a value";

  public CardId {
    Objects.requireNonNull(value, MISSING);
  }
}
