package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.module.SignalLever;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The owner of a Signal Lever turns it on and off, by hand or with the Configurator, and changes
 * its strength from its screen. Anyone else changes nothing (answers 552, 577 and 582). The lever
 * lives in its part, so the caller keeps what comes back.
 */
public final class ChangeLever {
  public Optional<SignalLever> toggle(PlayerId requester, SignalModule module, SignalLever lever) {
    return owned(requester, module).map(owner -> lever.toggle());
  }

  public Optional<SignalLever> strength(
      PlayerId requester, SignalModule module, SignalLever lever, int strength) {
    return owned(requester, module).map(owner -> lever.withStrength(strength));
  }

  private static Optional<PlayerId> owned(PlayerId requester, SignalModule module) {
    return module.isOwner(requester) ? Optional.of(requester) : Optional.empty();
  }
}
