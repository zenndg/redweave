package io.github.marcelofabianov.redweave.domain.module;

import static io.github.marcelofabianov.redweave.domain.module.TestDelayLimits.STARTING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * The delayed output of a module: what its rule decides now comes out after its delay; the first
 * tick with nothing waiting comes out at once (answers 830 to 854).
 */
class DelayedOutputTest {
  private static final OutputDelay ONE_SECOND = new OutputDelay(OutputDelay.STEP_TICKS);
  private static final int SECOND = ONE_SECOND.ticks();
  private static final Signal SEVEN = new Signal(7);
  private static final int WAITING = 3;

  private static DelayedOutput run(DelayedOutput output, Signal value, int ticks) {
    DelayedOutput now = output;
    for (int tick = 0; tick < ticks; tick++) {
      now = now.tick(value, STARTING);
    }
    return now;
  }

  @Test
  void aNewOutputHasNoDelayAndFollowsAtOnce() {
    DelayedOutput first = DelayedOutput.NONE.tick(Signal.MAX, STARTING);

    assertEquals(OutputDelay.NONE, DelayedOutput.NONE.delay());
    assertEquals(Signal.MAX, first.output());
  }

  @Test
  void theFirstTickWithADelayAndNothingWaitingComesOutAtOnce() {
    DelayedOutput loaded = DelayedOutput.restore(ONE_SECOND, DelayLine.EMPTY, SEVEN);

    assertEquals(SEVEN, loaded.tick(SEVEN, STARTING).output());
  }

  @Test
  void aChangeAfterTheFirstTickWaitsForTheDelay() {
    DelayedOutput steady = DelayedOutput.NONE.withDelay(ONE_SECOND).tick(Signal.OFF, STARTING);

    DelayedOutput almost = run(steady, Signal.MAX, SECOND);
    DelayedOutput through = almost.tick(Signal.MAX, STARTING);

    assertEquals(Signal.OFF, almost.output());
    assertEquals(Signal.MAX, through.output());
  }

  @Test
  void aSavedLineGoesOnFromWhereItStopped() {
    DelayLine saved = DelayLine.restore(List.of(new DelayLine.Pending(Signal.OFF, WAITING)));
    DelayedOutput loaded = DelayedOutput.restore(ONE_SECOND, saved, SEVEN);

    assertEquals(SEVEN, loaded.output());
    assertEquals(SEVEN, run(loaded, Signal.OFF, WAITING - 1).output());
    assertEquals(Signal.OFF, run(loaded, Signal.OFF, WAITING).output());
  }

  @Test
  void aNewStrengthChangesTheOutputAndWhatWaitsAtOnce() {
    DelayedOutput on = DelayedOutput.NONE.withDelay(ONE_SECOND).tick(Signal.MAX, STARTING);
    DelayedOutput waiting = on.tick(Signal.OFF, STARTING).tick(Signal.MAX, STARTING);

    DelayedOutput weaker = waiting.withStrength(SEVEN);

    assertEquals(SEVEN, weaker.output());
    assertEquals(SEVEN, weaker.line().latest(Signal.OFF));
  }

  @Test
  void aNewDelayKeepsWhatWaits() {
    DelayedOutput waiting = DelayedOutput.NONE.withDelay(ONE_SECOND).tick(Signal.OFF, STARTING);
    waiting = waiting.tick(Signal.MAX, STARTING);

    DelayedOutput longer = waiting.withDelay(OutputDelay.NONE);

    assertEquals(waiting.line(), longer.line());
    assertTrue(!longer.line().isEmpty());
  }

  @Test
  void theSameStrengthChangesNothing() {
    DelayedOutput on = DelayedOutput.NONE.tick(Signal.MAX, STARTING);
    DelayedOutput off = DelayedOutput.NONE.tick(Signal.OFF, STARTING);

    assertSame(on, on.withStrength(Signal.MAX));
    assertSame(off, off.withStrength(SEVEN));
    assertEquals(SEVEN, on.withStrength(SEVEN).output());
  }

  @Test
  void anOffOutputStaysOffWithANewStrengthWhileWhatWaitsTakesIt() {
    DelayedOutput off = DelayedOutput.NONE.withDelay(ONE_SECOND).tick(Signal.OFF, STARTING);
    DelayedOutput waiting = off.tick(Signal.MAX, STARTING);

    DelayedOutput weaker = waiting.withStrength(SEVEN);

    assertEquals(Signal.OFF, weaker.output());
    assertEquals(SEVEN, weaker.line().latest(Signal.OFF));
  }
}
