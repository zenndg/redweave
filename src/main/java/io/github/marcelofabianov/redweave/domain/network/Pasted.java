package io.github.marcelofabianov.redweave.domain.network;

import java.util.List;
import java.util.Optional;

/** The 16 slots of a node after a paste (answers 106 and 230). An empty slot was never enabled. */
public record Pasted(List<Optional<ChannelSlot>> slots) {
  public Pasted {
    slots = List.copyOf(slots);
  }

  /** How many slots of the node are configured after the paste, enabled or not (answer 240). */
  public int inUse() {
    return (int) slots.stream().filter(Optional::isPresent).count();
  }
}
