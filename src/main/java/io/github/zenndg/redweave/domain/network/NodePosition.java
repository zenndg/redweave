package io.github.zenndg.redweave.domain.network;

import java.util.Objects;

/** Where a node is: the block it sticks to and the face of that block. */
public record NodePosition(BlockLocation block, Face face) {
  private static final String MISSING = "A node position needs a block and a face";

  public NodePosition {
    Objects.requireNonNull(block, MISSING);
    Objects.requireNonNull(face, MISSING);
  }
}
