package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.module.ItemSensor;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.ModuleId;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Only the owner changes a Signal Item Sensor (answers 378, 689 and 696). */
class ChangeItemSensorTest {
  private static final String DIAMOND = "minecraft:diamond";
  private static final int SEVEN = 7;
  private static final int TEN = 10;
  private static final int MOST = 64;

  private final ChangeItemSensor change = new ChangeItemSensor();
  private final SignalModule module = SignalModule.placed(new ModuleId(NODE_ID), OWNER);
  private final ItemSensor sensor = ItemSensor.placed();

  @Test
  void theOwnerPicksTheItem() {
    Optional<String> diamond = Optional.of(DIAMOND);

    assertEquals(diamond, change.item(OWNER, module, sensor, diamond).orElseThrow().item());
  }

  @Test
  void theOwnerChangesTheCountAndTheStrength() {
    assertEquals(TEN, change.count(OWNER, module, sensor, TEN, MOST).orElseThrow().count());
    assertEquals(
        new Signal(SEVEN), change.strength(OWNER, module, sensor, SEVEN).orElseThrow().strength());
  }

  @Test
  void aStrangerChangesNothing() {
    assertTrue(change.item(STRANGER, module, sensor, Optional.of(DIAMOND)).isEmpty());
    assertTrue(change.count(STRANGER, module, sensor, TEN, MOST).isEmpty());
    assertTrue(change.strength(STRANGER, module, sensor, SEVEN).isEmpty());
  }
}
