package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.module.Draws;
import io.github.marcelofabianov.redweave.domain.module.RandomizerLimits;
import io.github.marcelofabianov.redweave.domain.module.RandomizerMode;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.module.SignalRandomizer;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The owner of a Signal Randomizer starts or stops it and changes its mode and its interval, from
 * its screen. Anyone else changes nothing, and the interval stays within the limits of the config
 * (answers 378, 477, 480 and 487). The randomizer lives in its block, so the caller keeps what
 * comes back.
 */
public final class ChangeRandomizer {
  public Optional<SignalRandomizer> toggle(
      PlayerId requester, SignalModule module, SignalRandomizer randomizer, Draws draws) {
    return owned(requester, module).map(owner -> randomizer.toggle(draws));
  }

  public Optional<SignalRandomizer> mode(
      PlayerId requester,
      SignalModule module,
      SignalRandomizer randomizer,
      RandomizerMode mode,
      Draws draws) {
    return owned(requester, module).map(owner -> randomizer.withMode(mode, draws));
  }

  public Optional<SignalRandomizer> interval(
      PlayerId requester,
      SignalModule module,
      SignalRandomizer randomizer,
      int ticks,
      RandomizerLimits limits) {
    return owned(requester, module).map(owner -> randomizer.withInterval(ticks, limits));
  }

  private static Optional<PlayerId> owned(PlayerId requester, SignalModule module) {
    return module.isOwner(requester) ? Optional.of(requester) : Optional.empty();
  }
}
