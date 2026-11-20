package io.github.zenndg.redweave.domain.network;

import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.Objects;

/** The last reading of one input slot, as it goes to the saves (answer 37). */
public record SlotReading(NodeId node, int index, Signal signal) {
  private static final String MISSING = "A reading needs a node and a signal";
  private static final String NEGATIVE = "The index of a slot is never negative";

  public SlotReading {
    Objects.requireNonNull(node, MISSING);
    Objects.requireNonNull(signal, MISSING);
    if (index < 0) {
      throw new IllegalArgumentException(NEGATIVE);
    }
  }
}
