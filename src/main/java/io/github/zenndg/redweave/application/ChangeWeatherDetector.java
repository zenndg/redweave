package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.module.WeatherDetector;
import io.github.zenndg.redweave.domain.module.WeatherMode;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The owner of a Weather Detector picks its mode on its screen. Anyone else changes nothing
 * (answers 378 and 470); the Toggle of the Configurator checks the owner before it (answer 462).
 * The detector lives in its block, so the caller keeps what comes back.
 */
public final class ChangeWeatherDetector {
  public Optional<WeatherDetector> mode(PlayerId requester, SignalModule module, WeatherMode mode) {
    return owned(requester, module).map(owner -> new WeatherDetector(mode));
  }

  private static Optional<PlayerId> owned(PlayerId requester, SignalModule module) {
    return module.isOwner(requester) ? Optional.of(requester) : Optional.empty();
  }
}
