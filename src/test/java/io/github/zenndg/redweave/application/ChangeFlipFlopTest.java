package io.github.zenndg.redweave.application;

import static io.github.zenndg.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.redweave.domain.module.SignalFlipFlop;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.ModuleId;
import io.github.zenndg.redweave.domain.signal.Signal;
import org.junit.jupiter.api.Test;

/** Only the owner changes a Signal Flip-Flop (answers 880 and 882). */
class ChangeFlipFlopTest {
  private static final int SEVEN = 7;

  private final ChangeFlipFlop change = new ChangeFlipFlop();
  private final SignalModule module = SignalModule.placed(new ModuleId(NODE_ID), OWNER);
  private final SignalFlipFlop flipFlop = SignalFlipFlop.placed();

  @Test
  void theOwnerFlipsItWithTheToggle() {
    assertTrue(change.toggle(OWNER, module, flipFlop).orElseThrow().on());
  }

  @Test
  void theOwnerChangesTheStrength() {
    assertEquals(
        new Signal(SEVEN),
        change.strength(OWNER, module, flipFlop, SEVEN).orElseThrow().strength());
  }

  @Test
  void aStrangerChangesNothing() {
    assertTrue(change.toggle(STRANGER, module, flipFlop).isEmpty());
    assertTrue(change.strength(STRANGER, module, flipFlop, SEVEN).isEmpty());
  }
}
