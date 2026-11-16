package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.module.LockLimits;
import io.github.marcelofabianov.redweave.domain.module.SignalLock;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The owner of a Signal Lock changes its key, its strength and the pulse of its `Toggle` from its
 * screen. Anyone else changes nothing, and the pulse stays within the limits of the config (answers
 * 636, 637 and 640). The lock lives in its part, so the caller keeps what comes back.
 */
public final class ChangeLock {
  public Optional<SignalLock> key(
      PlayerId requester, SignalModule module, SignalLock lock, int key) {
    return owned(requester, module).map(owner -> lock.withKey(key));
  }

  public Optional<SignalLock> strength(
      PlayerId requester, SignalModule module, SignalLock lock, int strength) {
    return owned(requester, module).map(owner -> lock.withStrength(strength));
  }

  public Optional<SignalLock> pulse(
      PlayerId requester, SignalModule module, SignalLock lock, int ticks, LockLimits limits) {
    return owned(requester, module).map(owner -> lock.withPulse(ticks, limits));
  }

  private static Optional<PlayerId> owned(PlayerId requester, SignalModule module) {
    return module.isOwner(requester) ? Optional.of(requester) : Optional.empty();
  }
}
