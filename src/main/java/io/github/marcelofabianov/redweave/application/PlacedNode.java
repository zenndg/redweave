package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import java.util.Objects;

/** The ID of a placed node, which the block keeps even without a network, and what happened. */
public record PlacedNode(NodeId node, Outcome outcome) {
  private static final String MISSING = "A placed node needs an ID and an outcome";

  public PlacedNode {
    Objects.requireNonNull(node, MISSING);
    Objects.requireNonNull(outcome, MISSING);
  }
}
