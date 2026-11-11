package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Outcome;
import java.util.Objects;

/** What the Toggle mode did: the outcome and how many channels it switched (answer 241). */
public record ToggleResult(Outcome outcome, int channels) {
  private static final String MISSING = "A toggle needs an outcome";

  public ToggleResult {
    Objects.requireNonNull(outcome, MISSING);
  }
}
