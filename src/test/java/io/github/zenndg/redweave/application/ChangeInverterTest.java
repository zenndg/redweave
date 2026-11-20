package io.github.zenndg.redweave.application;

import static io.github.zenndg.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.redweave.domain.module.SignalInverter;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.ModuleId;
import io.github.zenndg.redweave.domain.signal.OutputDelay;
import io.github.zenndg.redweave.domain.signal.OutputDelayLimits;
import io.github.zenndg.redweave.domain.signal.Signal;
import org.junit.jupiter.api.Test;

/** Only the owner changes the strength and the delay of a Signal Inverter (834, 830 and 840). */
class ChangeInverterTest {
  private static final int SEVEN = 7;
  private static final int UP = 1;
  private static final int FIFTEEN_SECONDS = 15 * OutputDelay.STEP_TICKS;
  private static final OutputDelayLimits LIMITS = () -> FIFTEEN_SECONDS;

  private final ChangeInverter change = new ChangeInverter();
  private final SignalModule module = SignalModule.placed(new ModuleId(NODE_ID), OWNER);
  private final SignalInverter inverter = SignalInverter.placed();

  @Test
  void theOwnerChangesTheStrength() {
    assertEquals(
        new Signal(SEVEN),
        change.strength(OWNER, module, inverter, SEVEN).orElseThrow().settings().strength());
  }

  @Test
  void theOwnerStepsTheDelayBySeconds() {
    SignalInverter next = change.delay(OWNER, module, inverter, UP, LIMITS).orElseThrow();

    assertEquals(new OutputDelay(OutputDelay.STEP_TICKS), next.settings().delay());
  }

  @Test
  void aStrangerChangesNothing() {
    assertTrue(change.strength(STRANGER, module, inverter, SEVEN).isEmpty());
    assertTrue(change.delay(STRANGER, module, inverter, UP, LIMITS).isEmpty());
  }
}
