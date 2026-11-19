package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.module.SignalFlipFlop;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The owner of a Signal Flip-Flop flips it with the Configurator, also on an `Offline` network, and
 * changes its strength from its screen. Anyone else changes nothing (answers 880 and 882). The
 * flip-flop lives in its part, so the caller keeps what comes back.
 */
public final class ChangeFlipFlop {
  public Optional<SignalFlipFlop> toggle(
      PlayerId requester, SignalModule module, SignalFlipFlop flipFlop) {
    return owned(requester, module).map(owner -> flipFlop.toggle());
  }

  public Optional<SignalFlipFlop> strength(
      PlayerId requester, SignalModule module, SignalFlipFlop flipFlop, int strength) {
    return owned(requester, module).map(owner -> flipFlop.withStrength(strength));
  }

  private static Optional<PlayerId> owned(PlayerId requester, SignalModule module) {
    return module.isOwner(requester) ? Optional.of(requester) : Optional.empty();
  }
}
