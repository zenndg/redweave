package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import java.util.Objects;
import java.util.Optional;

/**
 * What placing a controller did. An accepted one tells the block which network it keeps, and a
 * refused one has no network, because the block is not placed (answers 55 and 56).
 */
public record PlacedController(Outcome outcome, Optional<NetworkId> network) {
  private static final String MISSING = "A placed controller needs an outcome and a network";
  private static final String MISMATCH = "Only an accepted controller keeps a network";

  public PlacedController {
    Objects.requireNonNull(outcome, MISSING);
    Objects.requireNonNull(network, MISSING);
    if ((outcome == Outcome.ACCEPTED) != network.isPresent()) {
      throw new IllegalArgumentException(MISMATCH);
    }
  }
}
