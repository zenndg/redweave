package io.github.zenndg.redweave.domain.network;

import java.util.Objects;
import java.util.UUID;

/**
 * The ID of a Signal Module, given when it is placed and kept in its block as `module_id` (answer
 * 411). The caller generates it.
 */
public record ModuleId(UUID value) {
  private static final String MISSING = "A module ID needs a value";

  public ModuleId {
    Objects.requireNonNull(value, MISSING);
  }
}
