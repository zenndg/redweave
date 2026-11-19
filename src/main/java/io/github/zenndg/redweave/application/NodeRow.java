package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.NodeKind;
import io.github.zenndg.redweave.domain.network.NodePosition;
import io.github.zenndg.redweave.domain.network.NodeState;
import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * One row of the node list of the Controller Screen: the name, the signal of the lens, the state,
 * the kind, the position, only for the owner, and the slots in use (answers 29, 98, 149, 179 and
 * 337).
 */
public record NodeRow(
    NodeId id,
    String name,
    Signal signal,
    NodeState state,
    NodeKind kind,
    Optional<NodePosition> position,
    List<SlotRow> slots) {
  private static final String MISSING = "A node row needs every field, the position even if empty";

  public NodeRow {
    Objects.requireNonNull(id, MISSING);
    Objects.requireNonNull(name, MISSING);
    Objects.requireNonNull(signal, MISSING);
    Objects.requireNonNull(state, MISSING);
    Objects.requireNonNull(kind, MISSING);
    Objects.requireNonNull(position, MISSING);
    slots = List.copyOf(slots);
  }
}
