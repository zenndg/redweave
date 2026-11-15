package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.module.SignalLever;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.ModuleId;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import org.junit.jupiter.api.Test;

/** Only the owner changes a Signal Lever (answers 552, 577 and 582). */
class ChangeLeverTest {
  private static final int SEVEN = 7;

  private final ChangeLever change = new ChangeLever();
  private final SignalModule module = SignalModule.placed(new ModuleId(NODE_ID), OWNER);
  private final SignalLever lever = SignalLever.placed();

  @Test
  void theOwnerTurnsTheLeverOn() {
    assertTrue(change.toggle(OWNER, module, lever).orElseThrow().on());
  }

  @Test
  void theOwnerChangesTheStrength() {
    assertEquals(
        new Signal(SEVEN), change.strength(OWNER, module, lever, SEVEN).orElseThrow().strength());
  }

  @Test
  void aStrangerChangesNothing() {
    assertTrue(change.toggle(STRANGER, module, lever).isEmpty());
    assertTrue(change.strength(STRANGER, module, lever, SEVEN).isEmpty());
  }
}
