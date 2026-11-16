package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.marcelofabianov.redweave.domain.module.TestLockLimits.STARTING;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.module.LockPulse;
import io.github.marcelofabianov.redweave.domain.module.SignalLock;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.ModuleId;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import org.junit.jupiter.api.Test;

/** Only the owner changes a Signal Lock (answers 636, 637 and 640). */
class ChangeLockTest {
  private static final int SEVEN = 7;
  private static final int LONGER = 40;

  private final ChangeLock change = new ChangeLock();
  private final SignalModule module = SignalModule.placed(new ModuleId(NODE_ID), OWNER);
  private final SignalLock lock = SignalLock.placed(STARTING);

  @Test
  void theOwnerChangesTheKeyTheStrengthAndThePulse() {
    assertEquals(new Signal(SEVEN), change.key(OWNER, module, lock, SEVEN).orElseThrow().key());
    assertEquals(
        new Signal(SEVEN), change.strength(OWNER, module, lock, SEVEN).orElseThrow().strength());
    assertEquals(
        new LockPulse(LONGER),
        change.pulse(OWNER, module, lock, LONGER, STARTING).orElseThrow().pulse());
  }

  @Test
  void aStrangerChangesNothing() {
    assertTrue(change.key(STRANGER, module, lock, SEVEN).isEmpty());
    assertTrue(change.strength(STRANGER, module, lock, SEVEN).isEmpty());
    assertTrue(change.pulse(STRANGER, module, lock, LONGER, STARTING).isEmpty());
  }
}
