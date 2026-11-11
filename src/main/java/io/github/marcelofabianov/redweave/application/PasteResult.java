package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Outcome;

/** What a paste did: the outcome, the slots in use after it and the ones left out (106). */
public record PasteResult(Outcome outcome, int pasted) {
  static PasteResult refused(Outcome outcome) {
    return new PasteResult(outcome, 0);
  }
}
