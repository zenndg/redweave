package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.module.Draws;
import io.github.marcelofabianov.redweave.domain.module.Interval;
import io.github.marcelofabianov.redweave.domain.module.RandomizerLimits;
import io.github.marcelofabianov.redweave.domain.module.RandomizerMode;
import io.github.marcelofabianov.redweave.domain.module.RandomizerState;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.module.SignalRandomizer;
import io.github.marcelofabianov.redweave.domain.network.ModuleId;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Only the owner changes a Signal Randomizer, within the limits (answers 378, 477 and 480). */
class ChangeRandomizerTest {
  private static final int SHORTEST = 4;
  private static final int LONGEST = 6000;
  private static final int LONGER = 40;
  private static final RandomizerLimits LIMITS = new Limits(SHORTEST, LONGEST);
  private static final Draws HEADS = bound -> 1;

  private final ChangeRandomizer change = new ChangeRandomizer();
  private final SignalModule module = SignalModule.placed(new ModuleId(NODE_ID), OWNER);
  private final SignalRandomizer randomizer = SignalRandomizer.placed(LIMITS);

  @Test
  void theOwnerStartsTheRandomizer() {
    Optional<SignalRandomizer> started = change.toggle(OWNER, module, randomizer, HEADS);

    assertEquals(RandomizerState.RUNNING, started.orElseThrow().state());
  }

  @Test
  void theOwnerPicksAMode() {
    Optional<SignalRandomizer> analog =
        change.mode(OWNER, module, randomizer, RandomizerMode.ANALOG, HEADS);

    assertEquals(RandomizerMode.ANALOG, analog.orElseThrow().mode());
  }

  @Test
  void theOwnerChangesTheIntervalWithinTheLimits() {
    assertEquals(
        new Interval(LONGER),
        change.interval(OWNER, module, randomizer, LONGER, LIMITS).orElseThrow().interval());
    assertEquals(
        new Interval(LONGEST),
        change
            .interval(OWNER, module, randomizer, LONGEST + LONGER, LIMITS)
            .orElseThrow()
            .interval());
  }

  @Test
  void aStrangerChangesNothing() {
    assertTrue(change.toggle(STRANGER, module, randomizer, HEADS).isEmpty());
    assertTrue(change.mode(STRANGER, module, randomizer, RandomizerMode.ANALOG, HEADS).isEmpty());
    assertTrue(change.interval(STRANGER, module, randomizer, LONGER, LIMITS).isEmpty());
  }

  private record Limits(int minIntervalTicks, int maxIntervalTicks) implements RandomizerLimits {}
}
