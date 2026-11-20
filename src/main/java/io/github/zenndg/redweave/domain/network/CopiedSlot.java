package io.github.zenndg.redweave.domain.network;

import java.util.Objects;

/** One slot in use of a copied node: where it is on the node and its configuration (105). */
public record CopiedSlot(int index, ChannelSlot slot) {
  private static final String NOT_ITS_COLOR = "A copied slot sits at the place of its color";
  private static final String MISSING = "A copied slot needs its configuration";

  public CopiedSlot {
    Objects.requireNonNull(slot, MISSING);
    if (index != slot.channel().ordinal()) {
      throw new IllegalArgumentException(NOT_ITS_COLOR);
    }
  }
}
