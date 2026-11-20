package io.github.zenndg.redweave.domain.cable;

import java.util.Objects;

/** A link of a cable to the piece next to it, which the adapter finds in the world. */
public record CableLink<K>(K piece, Passage passage) {
  private static final String MISSING = "A cable link needs its piece and its passage";

  public CableLink {
    Objects.requireNonNull(piece, MISSING);
    Objects.requireNonNull(passage, MISSING);
  }
}
