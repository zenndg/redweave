package io.github.zenndg.redweave.domain.network;

import java.util.Objects;

/**
 * One face that a node emits into: the node, the block it sticks to and the face of that block.
 * Every output slot of the node on the same face emits through it (answer 39).
 */
public record OutputFace(NodeId node, BlockLocation block, Face face) {
  private static final String MISSING = "An output face needs a node, a block and a face";

  public OutputFace {
    Objects.requireNonNull(node, MISSING);
    Objects.requireNonNull(block, MISSING);
    Objects.requireNonNull(face, MISSING);
  }
}
