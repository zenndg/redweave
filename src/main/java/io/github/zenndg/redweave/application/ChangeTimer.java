package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.module.SignalTimer;
import io.github.zenndg.redweave.domain.module.TimerLimits;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The owner of a Signal Timer starts or stops it and changes its cycle, from its screen. Anyone
 * else changes nothing, and the cycle stays within the limits of the config (answers 378, 384, 398
 * and 409). The timer lives in its block, so the caller keeps what comes back.
 */
public final class ChangeTimer {
  public Optional<SignalTimer> toggle(PlayerId requester, SignalModule module, SignalTimer timer) {
    return owned(requester, module).map(owner -> timer.toggle());
  }

  /** Another interval restarts the cycle, with the pulse shortened to fit (answer 409). */
  public Optional<SignalTimer> interval(
      PlayerId requester, SignalModule module, SignalTimer timer, int ticks, TimerLimits limits) {
    return owned(requester, module)
        .map(owner -> timer.withCycle(timer.cycle().withInterval(ticks, limits)));
  }

  public Optional<SignalTimer> pulse(
      PlayerId requester, SignalModule module, SignalTimer timer, int ticks) {
    return owned(requester, module).map(owner -> timer.withCycle(timer.cycle().withPulse(ticks)));
  }

  private static Optional<PlayerId> owned(PlayerId requester, SignalModule module) {
    return module.isOwner(requester) ? Optional.of(requester) : Optional.empty();
  }
}
