package io.github.zenndg.redweave.server;

import io.github.zenndg.redweave.application.IdGenerator;
import java.util.UUID;

/** Random UUIDs, as the game makes for entities and players (answer 31). */
final class RandomIds implements IdGenerator {
  @Override
  public UUID newId() {
    return UUID.randomUUID();
  }
}
