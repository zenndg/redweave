package io.github.marcelofabianov.redweave.item;

import java.util.Objects;
import java.util.UUID;

/** The input slot a Signal Remote is linked to: the network, the node and the slot (287). */
public record RemoteLink(UUID network, UUID node, int slot) {
  private static final String MISSING = "A link needs the network and the node";

  public RemoteLink {
    Objects.requireNonNull(network, MISSING);
    Objects.requireNonNull(node, MISSING);
  }
}
