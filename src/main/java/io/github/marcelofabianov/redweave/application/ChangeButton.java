package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.module.ButtonLimits;
import io.github.marcelofabianov.redweave.domain.module.SignalButton;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The owner of a Signal Button presses it, by hand or with the Configurator, and changes its
 * strength and its on time from its screen. Anyone else changes nothing, and the on time stays
 * within the limits of the config (answers 552, 556, 588 and 592). The button lives in its part, so
 * the caller keeps what comes back.
 */
public final class ChangeButton {
  public Optional<SignalButton> press(
      PlayerId requester, SignalModule module, SignalButton button) {
    return owned(requester, module).map(owner -> button.press());
  }

  public Optional<SignalButton> strength(
      PlayerId requester, SignalModule module, SignalButton button, int strength) {
    return owned(requester, module).map(owner -> button.withStrength(strength));
  }

  public Optional<SignalButton> time(
      PlayerId requester,
      SignalModule module,
      SignalButton button,
      int ticks,
      ButtonLimits limits) {
    return owned(requester, module).map(owner -> button.withTime(ticks, limits));
  }

  private static Optional<PlayerId> owned(PlayerId requester, SignalModule module) {
    return module.isOwner(requester) ? Optional.of(requester) : Optional.empty();
  }
}
