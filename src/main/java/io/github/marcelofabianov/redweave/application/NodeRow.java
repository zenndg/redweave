package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.NodePosition;
import io.github.marcelofabianov.redweave.domain.network.NodeState;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * One row of the node list of the Controller Screen: the name, the signal of the lens, the state,
 * the position, only for the owner, and the slots in use (answers 29, 98, 149 and 179).
 */
public record NodeRow(
    NodeId id,
    String name,
    Signal signal,
    NodeState state,
    Optional<NodePosition> position,
    List<SlotRow> slots) {
  private static final String MISSING = "A node row needs every field, the position even if empty";

  public NodeRow {
    Objects.requireNonNull(id, MISSING);
    Objects.requireNonNull(name, MISSING);
    Objects.requireNonNull(signal, MISSING);
    Objects.requireNonNull(state, MISSING);
    Objects.requireNonNull(position, MISSING);
    slots = List.copyOf(slots);
  }
}
