package io.github.marcelofabianov.redweave.domain.network;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A Signal Module as its network knows it, so the list and the count work with its chunk unloaded:
 * its ID, its kind, as the ID of its item, its own name, where it is and the channels it reads or
 * writes (answers 803 and 810). The module itself stays the source of each of them; the network
 * only keeps a copy, which the module brings up to date (answer 813).
 */
public record ModuleEntry(
    ModuleId id,
    String kind,
    Optional<String> name,
    NodePosition position,
    List<ModuleChannel> channels) {
  private static final String MISSING = "A module entry needs its ID, kind, name and position";
  private static final String BLANK_KIND = "A module entry needs a kind";
  private static final String INVALID_NAME = "The name of a module is never blank nor padded";

  public ModuleEntry {
    Objects.requireNonNull(id, MISSING);
    Objects.requireNonNull(kind, MISSING);
    Objects.requireNonNull(name, MISSING);
    Objects.requireNonNull(position, MISSING);
    if (kind.isBlank()) {
      throw new IllegalArgumentException(BLANK_KIND);
    }
    if (name.filter(given -> !Names.isKept(given)).isPresent()) {
      throw new IllegalArgumentException(INVALID_NAME);
    }
    channels = List.copyOf(channels);
  }
}
