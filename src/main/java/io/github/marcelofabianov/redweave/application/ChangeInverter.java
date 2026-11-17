package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.module.OutputDelayLimits;
import io.github.marcelofabianov.redweave.domain.module.SignalInverter;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The owner of a Signal Inverter changes its strength and its delay from its screen, one step at a
 * time. Anyone else changes nothing (answers 830, 834 and 840). The inverter lives in its part, so
 * the caller keeps what comes back.
 */
public final class ChangeInverter {
  public Optional<SignalInverter> strength(
      PlayerId requester, SignalModule module, SignalInverter inverter, int strength) {
    return owned(requester, module).map(owner -> inverter.withStrength(strength));
  }

  /** A step of the delay, in whole seconds, within the limits of the config (answer 837). */
  public Optional<SignalInverter> delay(
      PlayerId requester,
      SignalModule module,
      SignalInverter inverter,
      int seconds,
      OutputDelayLimits limits) {
    return owned(requester, module)
        .map(owner -> inverter.withDelay(inverter.settings().delay().stepped(seconds, limits)));
  }

  private static Optional<PlayerId> owned(PlayerId requester, SignalModule module) {
    return module.isOwner(requester) ? Optional.of(requester) : Optional.empty();
  }
}
