package io.github.zenndg.redweave.domain.network;

import io.github.zenndg.redweave.domain.signal.OutputDelay;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The 16 slots of a node after a paste (answers 106 and 230), and its output delay (answer 868). An
 * empty slot was never enabled.
 */
public record Pasted(List<Optional<ChannelSlot>> slots, OutputDelay delay) {
  public Pasted {
    Objects.requireNonNull(delay);
    slots = List.copyOf(slots);
  }

  /** How many slots of the node are configured after the paste, enabled or not (answer 240). */
  public int inUse() {
    return (int) slots.stream().filter(Optional::isPresent).count();
  }
}
