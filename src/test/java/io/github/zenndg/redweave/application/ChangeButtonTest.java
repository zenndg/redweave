package io.github.zenndg.redweave.application;

import static io.github.zenndg.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.zenndg.redweave.domain.module.TestButtonLimits.STARTING;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.redweave.domain.module.ButtonTime;
import io.github.zenndg.redweave.domain.module.SignalButton;
import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.network.ModuleId;
import io.github.zenndg.redweave.domain.signal.Signal;
import org.junit.jupiter.api.Test;

/** Only the owner changes a Signal Button (answers 552, 556, 588 and 592). */
class ChangeButtonTest {
  private static final int SEVEN = 7;
  private static final int LONGER = 40;

  private final ChangeButton change = new ChangeButton();
  private final SignalModule module = SignalModule.placed(new ModuleId(NODE_ID), OWNER);
  private final SignalButton button = SignalButton.placed(STARTING);

  @Test
  void theOwnerPressesTheButton() {
    assertTrue(change.press(OWNER, module, button).orElseThrow().on());
  }

  @Test
  void theOwnerChangesTheStrengthAndTheOnTime() {
    assertEquals(
        new Signal(SEVEN), change.strength(OWNER, module, button, SEVEN).orElseThrow().strength());
    assertEquals(
        new ButtonTime(LONGER),
        change.time(OWNER, module, button, LONGER, STARTING).orElseThrow().time());
  }

  @Test
  void aStrangerChangesNothing() {
    assertTrue(change.press(STRANGER, module, button).isEmpty());
    assertTrue(change.strength(STRANGER, module, button, SEVEN).isEmpty());
    assertTrue(change.time(STRANGER, module, button, LONGER, STARTING).isEmpty());
  }
}
