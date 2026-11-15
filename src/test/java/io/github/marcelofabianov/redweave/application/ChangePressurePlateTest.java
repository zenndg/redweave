package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.module.PlateMode;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.module.SignalPressurePlate;
import io.github.marcelofabianov.redweave.domain.network.ModuleId;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import org.junit.jupiter.api.Test;

/** Only the owner changes a Signal Pressure Plate (answers 378, 552 and 621). */
class ChangePressurePlateTest {
  private static final int SEVEN = 7;

  private final ChangePressurePlate change = new ChangePressurePlate();
  private final SignalModule module = SignalModule.placed(new ModuleId(NODE_ID), OWNER);
  private final SignalPressurePlate plate = SignalPressurePlate.placed();

  @Test
  void theOwnerChangesTheStrength() {
    assertEquals(
        new Signal(SEVEN), change.strength(OWNER, module, plate, SEVEN).orElseThrow().strength());
  }

  @Test
  void theOwnerChangesTheMode() {
    assertEquals(
        PlateMode.ITEMS, change.mode(OWNER, module, plate, PlateMode.ITEMS).orElseThrow().mode());
  }

  @Test
  void aStrangerChangesNothing() {
    assertTrue(change.strength(STRANGER, module, plate, SEVEN).isEmpty());
    assertTrue(change.mode(STRANGER, module, plate, PlateMode.ITEMS).isEmpty());
  }
}
