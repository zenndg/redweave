package io.github.zenndg.redweave.application;

import static io.github.zenndg.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.ModuleId;
import io.github.zenndg.redweave.domain.signal.OutputDelay;
import io.github.zenndg.redweave.domain.signal.OutputDelayLimits;
import org.junit.jupiter.api.Test;

/** Only the owner steps the delay of a module, within the config (answers 830, 837 and 850). */
class ChangeOutputDelayTest {
  private static final int UP = 1;
  private static final int DOWN = -1;
  private static final int FIFTEEN_SECONDS = 15 * OutputDelay.STEP_TICKS;
  private static final OutputDelayLimits LIMITS = () -> FIFTEEN_SECONDS;
  private static final OutputDelay ONE_SECOND = new OutputDelay(OutputDelay.STEP_TICKS);

  private final ChangeOutputDelay change = new ChangeOutputDelay();
  private final SignalModule module = SignalModule.placed(new ModuleId(NODE_ID), OWNER);

  @Test
  void theOwnerStepsTheDelayBySeconds() {
    assertEquals(
        ONE_SECOND, change.step(OWNER, module, OutputDelay.NONE, UP, LIMITS).orElseThrow());
    assertEquals(
        OutputDelay.NONE, change.step(OWNER, module, ONE_SECOND, DOWN, LIMITS).orElseThrow());
  }

  @Test
  void aStrangerChangesNothing() {
    assertTrue(change.step(STRANGER, module, OutputDelay.NONE, UP, LIMITS).isEmpty());
  }
}
