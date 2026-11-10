package io.github.marcelofabianov.redweave.domain.network;

import java.util.List;
import java.util.Optional;

/**
 * Every slot of a node after a paste, and how many copied slots did not fit it (answer 106). An
 * empty slot is a free one.
 */
public record Pasted(List<Optional<ChannelSlot>> slots, int leftOut) {
  public Pasted {
    slots = List.copyOf(slots);
  }

  /** How many slots of the node are in use after the paste. */
  public int inUse() {
    return (int) slots.stream().filter(Optional::isPresent).count();
  }
}
