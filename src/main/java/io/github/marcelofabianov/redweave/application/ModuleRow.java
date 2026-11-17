package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.ModuleChannel;
import io.github.marcelofabianov.redweave.domain.network.ModuleId;
import io.github.marcelofabianov.redweave.domain.network.NodePosition;
import io.github.marcelofabianov.redweave.domain.network.NodeState;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A Signal Module in the `Components` tab: its name, its kind, what it writes on its channel this
 * tick, or none, its state as a node, where it is only for the owner, and the channels it reads or
 * writes (answers 809, 810 and 814).
 */
public record ModuleRow(
    ModuleId id,
    String name,
    String kind,
    Optional<Signal> signal,
    NodeState state,
    Optional<NodePosition> position,
    List<ModuleChannel> channels) {
  private static final String MISSING = "A module row needs every part, empty or not";

  public ModuleRow {
    Objects.requireNonNull(id, MISSING);
    Objects.requireNonNull(name, MISSING);
    Objects.requireNonNull(kind, MISSING);
    Objects.requireNonNull(signal, MISSING);
    Objects.requireNonNull(state, MISSING);
    Objects.requireNonNull(position, MISSING);
    channels = List.copyOf(channels);
  }
}
