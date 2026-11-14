package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.module.WeatherDetector;
import io.github.marcelofabianov.redweave.domain.module.WeatherMode;
import io.github.marcelofabianov.redweave.domain.network.ModuleId;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Only the owner picks the mode of a Weather Detector (answers 378 and 470). */
class ChangeWeatherDetectorTest {
  private final ChangeWeatherDetector change = new ChangeWeatherDetector();
  private final SignalModule module = SignalModule.placed(new ModuleId(NODE_ID), OWNER);

  @Test
  void theOwnerPicksAMode() {
    assertEquals(
        Optional.of(new WeatherDetector(WeatherMode.CLEAR)),
        change.mode(OWNER, module, WeatherMode.CLEAR));
  }

  @Test
  void aStrangerChangesNothing() {
    assertEquals(Optional.empty(), change.mode(STRANGER, module, WeatherMode.CLEAR));
  }
}
