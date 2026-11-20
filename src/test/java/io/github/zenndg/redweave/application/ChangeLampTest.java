package io.github.zenndg.redweave.application;

import static io.github.zenndg.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.redweave.domain.module.LampMode;
import io.github.zenndg.redweave.domain.module.SignalLamp;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.ModuleId;
import io.github.zenndg.redweave.domain.signal.Signal;
import org.junit.jupiter.api.Test;

/** Only the owner changes a Signal Lamp (answers 651, 656 and 658). */
class ChangeLampTest {
  private static final int SEVEN = 7;

  private final ChangeLamp change = new ChangeLamp();
  private final SignalModule module = SignalModule.placed(new ModuleId(NODE_ID), OWNER);
  private final SignalLamp lamp = SignalLamp.placed();

  @Test
  void theOwnerSwitchesTheLampOff() {
    assertTrue(change.toggle(OWNER, module, lamp).orElseThrow().switchedOff());
  }

  @Test
  void theOwnerChangesTheStrengthAndTheMode() {
    assertEquals(
        new Signal(SEVEN), change.strength(OWNER, module, lamp, SEVEN).orElseThrow().strength());
    assertEquals(
        LampMode.PULSE, change.mode(OWNER, module, lamp, LampMode.PULSE).orElseThrow().mode());
  }

  @Test
  void aStrangerChangesNothing() {
    assertTrue(change.toggle(STRANGER, module, lamp).isEmpty());
    assertTrue(change.strength(STRANGER, module, lamp, SEVEN).isEmpty());
    assertTrue(change.mode(STRANGER, module, lamp, LampMode.PULSE).isEmpty());
  }
}
