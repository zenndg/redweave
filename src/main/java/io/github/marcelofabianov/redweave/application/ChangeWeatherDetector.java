package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.module.WeatherDetector;
import io.github.marcelofabianov.redweave.domain.module.WeatherMode;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The owner of a Weather Detector changes its mode, with the Toggle or on its screen. Anyone else
 * changes nothing (answers 378, 462 and 470). The detector lives in its block, so the caller keeps
 * what comes back.
 */
public final class ChangeWeatherDetector {
  public Optional<WeatherDetector> toggle(
      PlayerId requester, SignalModule module, WeatherDetector detector) {
    return owned(requester, module).map(owner -> detector.toggled());
  }

  public Optional<WeatherDetector> mode(PlayerId requester, SignalModule module, WeatherMode mode) {
    return owned(requester, module).map(owner -> new WeatherDetector(mode));
  }

  private static Optional<PlayerId> owned(PlayerId requester, SignalModule module) {
    return module.isOwner(requester) ? Optional.of(requester) : Optional.empty();
  }
}
