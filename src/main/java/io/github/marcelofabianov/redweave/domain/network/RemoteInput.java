package io.github.marcelofabianov.redweave.domain.network;

import java.util.Objects;

/**
 * A Signal Remote that counts this tick, on the input slot it is linked to: the slot, not the
 * channel, so the tablet follows the slot to another channel (answers 287 and 360).
 */
public record RemoteInput(RemoteId remote, NodeId node, int index) {
  private static final String MISSING = "A remote input needs the remote and the node";

  public RemoteInput {
    Objects.requireNonNull(remote, MISSING);
    Objects.requireNonNull(node, MISSING);
  }
}
