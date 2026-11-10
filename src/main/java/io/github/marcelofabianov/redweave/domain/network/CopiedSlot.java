package io.github.marcelofabianov.redweave.domain.network;

import java.util.Objects;

/** One slot in use of a copied node: where it is on the node and its configuration (105). */
public record CopiedSlot(int index, ChannelSlot slot) {
  private static final String NEGATIVE = "The index of a slot is never negative";
  private static final String MISSING = "A copied slot needs its configuration";

  public CopiedSlot {
    Objects.requireNonNull(slot, MISSING);
    if (index < 0) {
      throw new IllegalArgumentException(NEGATIVE);
    }
  }
}
