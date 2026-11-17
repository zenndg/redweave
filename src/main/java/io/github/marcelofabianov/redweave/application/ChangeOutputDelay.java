package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.module.OutputDelay;
import io.github.marcelofabianov.redweave.domain.module.OutputDelayLimits;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The owner of a module with a delay steps it from its screen, one second at a time, within the
 * config. Anyone else changes nothing (answers 830, 837 and 850). The delay lives in its part, so
 * the caller keeps what comes back.
 */
public final class ChangeOutputDelay {
  public Optional<OutputDelay> step(
      PlayerId requester,
      SignalModule module,
      OutputDelay now,
      int seconds,
      OutputDelayLimits limits) {
    if (!module.isOwner(requester)) {
      return Optional.empty();
    }
    return Optional.of(now.stepped(seconds, limits));
  }
}
