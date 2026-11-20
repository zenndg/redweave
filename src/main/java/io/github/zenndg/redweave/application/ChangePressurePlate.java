package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.module.PlateMode;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.module.SignalPressurePlate;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The owner of a Signal Pressure Plate changes its strength and its mode from its screen. Anyone
 * else changes nothing (answers 378, 552 and 621). The plate lives in its part, so the caller keeps
 * what comes back.
 */
public final class ChangePressurePlate {
  public Optional<SignalPressurePlate> strength(
      PlayerId requester, SignalModule module, SignalPressurePlate plate, int strength) {
    return owned(requester, module).map(owner -> plate.withStrength(strength));
  }

  public Optional<SignalPressurePlate> mode(
      PlayerId requester, SignalModule module, SignalPressurePlate plate, PlateMode mode) {
    return owned(requester, module).map(owner -> plate.withMode(mode));
  }

  private static Optional<PlayerId> owned(PlayerId requester, SignalModule module) {
    return module.isOwner(requester) ? Optional.of(requester) : Optional.empty();
  }
}
