package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.marcelofabianov.redweave.domain.module.TestAlarmLimits.STARTING;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.module.AlarmListeners;
import io.github.marcelofabianov.redweave.domain.module.SignalAlarm;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.ModuleId;
import org.junit.jupiter.api.Test;

/** Only the owner changes a Signal Alarm (answers 378, 645, 646 and 648). */
class ChangeAlarmTest {
  private static final int FOUR = 4;
  private static final int LONGER = 40;
  private static final int DURATION = 200;

  private final ChangeAlarm change = new ChangeAlarm();
  private final SignalModule module = SignalModule.placed(new ModuleId(NODE_ID), OWNER);
  private final SignalAlarm alarm = SignalAlarm.placed(STARTING);

  @Test
  void theOwnerChangesWhoHearsItItsRingsItsIntervalAndItsDuration() {
    assertEquals(
        AlarmListeners.NETWORK,
        change.listeners(OWNER, module, alarm).orElseThrow().settings().listeners());
    assertEquals(
        FOUR,
        change.repeats(OWNER, module, alarm, FOUR, STARTING).orElseThrow().settings().repeats());
    assertEquals(
        LONGER,
        change
            .interval(OWNER, module, alarm, LONGER, STARTING)
            .orElseThrow()
            .settings()
            .intervalTicks());
    assertEquals(
        DURATION,
        change
            .duration(OWNER, module, alarm, DURATION, STARTING)
            .orElseThrow()
            .settings()
            .durationTicks());
  }

  @Test
  void aStrangerChangesNothing() {
    assertTrue(change.listeners(STRANGER, module, alarm).isEmpty());
    assertTrue(change.repeats(STRANGER, module, alarm, FOUR, STARTING).isEmpty());
    assertTrue(change.interval(STRANGER, module, alarm, LONGER, STARTING).isEmpty());
    assertTrue(change.duration(STRANGER, module, alarm, DURATION, STARTING).isEmpty());
  }
}
