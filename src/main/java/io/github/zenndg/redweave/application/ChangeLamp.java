package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.module.LampMode;
import io.github.zenndg.redweave.domain.module.SignalLamp;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The owner of a Signal Lamp turns it off or on with the Configurator, and changes its strength and
 * the mode of its control from its screen. Anyone else changes nothing (answers 651, 656 and 658).
 * The lamp lives in its part, so the caller keeps what comes back.
 */
public final class ChangeLamp {
  public Optional<SignalLamp> toggle(PlayerId requester, SignalModule module, SignalLamp lamp) {
    return owned(requester, module).map(owner -> lamp.toggle());
  }

  public Optional<SignalLamp> strength(
      PlayerId requester, SignalModule module, SignalLamp lamp, int strength) {
    return owned(requester, module).map(owner -> lamp.withStrength(strength));
  }

  public Optional<SignalLamp> mode(
      PlayerId requester, SignalModule module, SignalLamp lamp, LampMode mode) {
    return owned(requester, module).map(owner -> lamp.withMode(mode));
  }

  private static Optional<PlayerId> owned(PlayerId requester, SignalModule module) {
    return module.isOwner(requester) ? Optional.of(requester) : Optional.empty();
  }
}
