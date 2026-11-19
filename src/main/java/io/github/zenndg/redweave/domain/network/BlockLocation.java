package io.github.zenndg.redweave.domain.network;

import java.util.Objects;

/** A block in a dimension, such as the Network Controller. The dimension is the ID of the game. */
public record BlockLocation(String dimension, int x, int y, int z) {
  private static final String MISSING = "A block location needs a dimension";

  public BlockLocation {
    Objects.requireNonNull(dimension, MISSING);
  }
}
