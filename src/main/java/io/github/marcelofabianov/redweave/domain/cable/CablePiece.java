package io.github.marcelofabianov.redweave.domain.cable;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Objects;

/**
 * One Signal Cable of a run: its insulation and the strongest signal it reads from beside it, as
 * from a lever, a node, a module or the game wire, which the adapter reads (answers 507 and 508).
 */
public record CablePiece(Insulation insulation, Signal input) {
  private static final String MISSING = "A cable piece needs its insulation and its input";

  public CablePiece {
    Objects.requireNonNull(insulation, MISSING);
    Objects.requireNonNull(input, MISSING);
  }
}
