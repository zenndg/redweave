package io.github.zenndg.redweave.application;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.UUID;

/** Hands out known IDs in order, so a test knows the ID of what it creates (answer 31). */
final class FixedIds implements IdGenerator {
  private final Deque<UUID> ids;

  FixedIds(List<UUID> ids) {
    this.ids = new ArrayDeque<>(ids);
  }

  @Override
  public UUID newId() {
    return ids.removeFirst();
  }
}
