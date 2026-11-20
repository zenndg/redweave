package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.Outcome;
import java.util.Optional;
import java.util.function.Function;

/** Applies a change to a network and saves it only when accepted, so a refusal changes nothing. */
final class SavedChange {
  private SavedChange() {}

  static Outcome apply(
      Optional<Network> network,
      Outcome whenMissing,
      Function<Network, Outcome> change,
      NetworkSaver saver) {
    if (network.isEmpty()) {
      return whenMissing;
    }
    Outcome outcome = change.apply(network.get());
    if (outcome == Outcome.ACCEPTED) {
      saver.save(network.get());
    }
    return outcome;
  }
}
