package io.github.zenndg.redweave.domain.signal;

import static io.github.zenndg.redweave.domain.signal.TestDelayLimits.STARTING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

  /** A node that stops counting emits off at once and drops what waits (answer 862). */
  @Test
  void aCutOutputIsOffAtOnceWithNothingWaiting() {
    DelayedOutput steady = DelayedOutput.NONE.withDelay(ONE_SECOND).tick(SEVEN, STARTING);
    DelayedOutput waiting = steady.tick(Signal.MAX, STARTING);

    DelayedOutput cut = waiting.cut();

    assertEquals(Signal.OFF, cut.output());
    assertTrue(cut.line().isEmpty());
    assertEquals(ONE_SECOND, cut.delay());
    assertSame(cut, cut.cut());
  }

  /** Counting again is a change like any other, so it waits for the delay (answer 874). */
  @Test
  void theFirstValueAfterACutWaitsForTheDelay() {
    DelayedOutput cut = DelayedOutput.NONE.withDelay(ONE_SECOND).tick(SEVEN, STARTING).cut();

    DelayedOutput almost = run(cut, SEVEN, SECOND);

    assertEquals(Signal.OFF, almost.output());
    assertEquals(SEVEN, almost.tick(SEVEN, STARTING).output());
  }

  /** A node loads with the output it had while its changes waited (answer 873). */
  @Test
  void aResumedOutputKeepsWhatItGaveUntilTheFirstChangeComesOut() {
    DelayLine saved = DelayLine.restore(List.of(new DelayLine.Pending(Signal.OFF, WAITING)));

    DelayedOutput resumed = DelayedOutput.resumed(ONE_SECOND, saved, SEVEN);

    assertEquals(SEVEN, resumed.output());
    assertEquals(SEVEN, run(resumed, Signal.OFF, WAITING - 1).output());
    assertEquals(Signal.OFF, run(resumed, Signal.OFF, WAITING).output());
  }

  @Test
  void aResumedOutputWithNothingWaitingFollowsAtOnce() {
    DelayedOutput resumed = DelayedOutput.resumed(ONE_SECOND, DelayLine.EMPTY, Signal.OFF);

    assertEquals(SEVEN, resumed.tick(SEVEN, STARTING).output());
  }

  /** Nothing to do on a tick: the value is out and nothing waits, so a node skips it (18). */
  @Test
  void anOutputFollowsAValueOnlyWhenItIsOutAndNothingWaits() {
    DelayedOutput steady = DelayedOutput.NONE.withDelay(ONE_SECOND).tick(SEVEN, STARTING);

    assertTrue(steady.follows(SEVEN));
    assertFalse(steady.follows(Signal.MAX));
    assertFalse(steady.tick(Signal.MAX, STARTING).follows(Signal.MAX));
    assertFalse(DelayedOutput.NONE.follows(Signal.OFF));
  }

  @Test
  void theSameDelayChangesNothing() {
    DelayedOutput delayed = DelayedOutput.NONE.withDelay(ONE_SECOND);

    assertSame(delayed, delayed.withDelay(ONE_SECOND));
  }
}
