package io.github.zenndg.redweave.application;

import static io.github.zenndg.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.redweave.domain.module.SignalModule;
import io.github.zenndg.redweave.domain.module.SignalTimer;
import io.github.zenndg.redweave.domain.module.TimerCycle;
import io.github.zenndg.redweave.domain.module.TimerLimits;
import io.github.zenndg.redweave.domain.network.ModuleId;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Only the owner changes a Signal Timer, within the limits (answers 378, 384, 398 and 409). */
class ChangeTimerTest {
  private static final int SHORTEST = 4;
  private static final int LONGEST = 6000;
  private static final int SECOND = 20;
  private static final int HALF_SECOND = 10;
  private static final int LONGER = 40;
  private static final int SHORTER_PULSE = 6;
  private static final TimerLimits LIMITS = new Limits(SHORTEST, LONGEST);

  private final ChangeTimer change = new ChangeTimer();
  private final SignalModule module = SignalModule.placed(new ModuleId(NODE_ID), OWNER);
  private final SignalTimer timer = SignalTimer.stopped(new TimerCycle(SECOND, HALF_SECOND));

  @Test
  void theOwnerTogglesTheTimer() {
    Optional<SignalTimer> toggled = change.toggle(OWNER, module, timer);

    assertTrue(toggled.orElseThrow().isRunning());
  }

  @Test
  void theOwnerChangesTheIntervalWithinTheLimits() {
    Optional<SignalTimer> changed = change.interval(OWNER, module, timer, LONGER, LIMITS);

    assertEquals(LONGER, changed.orElseThrow().cycle().intervalTicks());
    assertEquals(
        LONGEST,
        change
            .interval(OWNER, module, timer, LONGEST + LONGER, LIMITS)
            .orElseThrow()
            .cycle()
            .intervalTicks());
  }

  @Test
  void theOwnerChangesThePulseWithinTheInterval() {
    Optional<SignalTimer> changed = change.pulse(OWNER, module, timer, SHORTER_PULSE);

    assertEquals(SHORTER_PULSE, changed.orElseThrow().cycle().pulseTicks());
  }

  @Test
  void aStrangerChangesNothing() {
    assertEquals(Optional.empty(), change.toggle(STRANGER, module, timer));
    assertEquals(Optional.empty(), change.interval(STRANGER, module, timer, LONGER, LIMITS));
    assertEquals(Optional.empty(), change.pulse(STRANGER, module, timer, SHORTER_PULSE));
  }

  private record Limits(int minIntervalTicks, int maxIntervalTicks) implements TimerLimits {}
}
