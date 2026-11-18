package io.github.marcelofabianov.redweave.domain.signal;

import static io.github.marcelofabianov.redweave.domain.signal.TestDelayLimits.STARTING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * The delay line of an output: only the changes, each with its signal and the ticks it still waits,
 * in order, with short pulses whole and a cap on how many wait (answers 831, 832, 838, 839, 843,
 * 847 and 854).
 */
class DelayLineTest {
  private static final OutputDelay ONE_SECOND = new OutputDelay(OutputDelay.STEP_TICKS);
  private static final int DELAY = ONE_SECOND.ticks();
  private static final OutputDelay LONGER = new OutputDelay(2 * OutputDelay.STEP_TICKS);
  private static final int NEGATIVE = -1;
  private static final int SHORT_PULSE = 3;
  private static final int TWO_CHANGES = 2;
  private static final String ON_MARK = "1";
  private static final String OFF_MARK = "0";
  private static final Signal FIVE = new Signal(5);
  private static final Signal NINE = new Signal(9);
  private static final Signal SEVEN = new Signal(7);
  private static final TestDelayLimits TWO = new TestDelayLimits(DELAY, TWO_CHANGES);

  private static DelayLine.Passage run(DelayLine line, Signal value, Signal output, int ticks) {
    DelayLine.Passage now = new DelayLine.Passage(line, output, false);
    for (int tick = 0; tick < ticks; tick++) {
      now = now.line().pass(value, ONE_SECOND, now.output(), STARTING);
    }
    return now;
  }

  @Test
  void anEmptyLineWithNoDelayPassesAtOnce() {
    DelayLine.Passage passage =
        DelayLine.EMPTY.pass(Signal.MAX, OutputDelay.NONE, Signal.OFF, STARTING);

    assertEquals(Signal.MAX, passage.output());
    assertTrue(passage.entered());
    assertTrue(passage.line().isEmpty());
  }

  @Test
  void theSameValueEntersNothing() {
    DelayLine.Passage passage = DelayLine.EMPTY.pass(Signal.OFF, ONE_SECOND, Signal.OFF, STARTING);

    assertFalse(passage.entered());
    assertTrue(passage.line().isEmpty());
  }

  @Test
  void aChangeComesOutAfterTheDelay() {
    DelayLine.Passage entered = DelayLine.EMPTY.pass(Signal.MAX, ONE_SECOND, Signal.OFF, STARTING);
    DelayLine.Passage waiting = run(entered.line(), Signal.MAX, Signal.OFF, DELAY - 1);
    DelayLine.Passage out = run(entered.line(), Signal.MAX, Signal.OFF, DELAY);

    assertTrue(entered.entered());
    assertEquals(Signal.OFF, entered.output());
    assertEquals(Signal.OFF, waiting.output());
    assertEquals(Signal.MAX, out.output());
    assertTrue(out.line().isEmpty());
  }

  @Test
  void turningOffIsDelayedToo() {
    DelayLine.Passage entered = DelayLine.EMPTY.pass(Signal.OFF, ONE_SECOND, Signal.MAX, STARTING);

    assertEquals(Signal.MAX, run(entered.line(), Signal.OFF, Signal.MAX, DELAY - 1).output());
    assertEquals(Signal.OFF, run(entered.line(), Signal.OFF, Signal.MAX, DELAY).output());
  }

  @Test
  void aChangeOfStrengthIsAChangeToo() {
    DelayLine.Passage entered = DelayLine.EMPTY.pass(NINE, ONE_SECOND, FIVE, STARTING);

    assertTrue(entered.entered());
    assertEquals(NINE, run(entered.line(), NINE, FIVE, DELAY).output());
  }

  @Test
  void aShortPulsePassesWholeAndLate() {
    DelayLine.Passage now = new DelayLine.Passage(DelayLine.EMPTY, Signal.OFF, false);
    StringBuilder seen = new StringBuilder();
    for (int tick = 0; tick < DELAY + SHORT_PULSE + 1; tick++) {
      Signal input = tick < SHORT_PULSE ? Signal.MAX : Signal.OFF;
      now = now.line().pass(input, ONE_SECOND, now.output(), STARTING);
      seen.append(now.output().isOn() ? ON_MARK : OFF_MARK);
    }

    String expected = OFF_MARK.repeat(DELAY) + ON_MARK.repeat(SHORT_PULSE) + OFF_MARK;
    assertEquals(expected, seen.toString());
  }

  @Test
  void aShorterDelayNeverOvertakesAPendingChange() {
    DelayLine.Passage first = DelayLine.EMPTY.pass(Signal.MAX, LONGER, Signal.OFF, STARTING);

    DelayLine.Passage second =
        first.line().pass(Signal.OFF, OutputDelay.NONE, Signal.OFF, STARTING);

    assertEquals(Signal.OFF, second.output());
    assertEquals(
        List.of(
            new DelayLine.Pending(Signal.MAX, LONGER.ticks() - 1),
            new DelayLine.Pending(Signal.OFF, LONGER.ticks() - 1)),
        second.line().pending());
  }

  @Test
  void pendingChangesKeepTheirTickWhenTheDelayChanges() {
    DelayLine.Passage first = DelayLine.EMPTY.pass(Signal.MAX, LONGER, Signal.OFF, STARTING);

    DelayLine.Passage still = first.line().pass(Signal.MAX, ONE_SECOND, Signal.OFF, STARTING);

    assertEquals(
        List.of(new DelayLine.Pending(Signal.MAX, LONGER.ticks() - 1)), still.line().pending());
  }

  @Test
  void aFullLineLetsTheNextChangeWaitOutside() {
    DelayLine line = DelayLine.EMPTY.pass(Signal.MAX, ONE_SECOND, Signal.OFF, TWO).line();
    line = line.pass(Signal.OFF, ONE_SECOND, Signal.OFF, TWO).line();

    DelayLine.Passage full = line.pass(Signal.MAX, ONE_SECOND, Signal.OFF, TWO);

    assertFalse(full.entered());
    assertEquals(TWO_CHANGES, full.line().pending().size());
  }

  @Test
  void aChangeLeftOutsideEntersWhenThereIsRoom() {
    DelayLine.Passage now = new DelayLine.Passage(DelayLine.EMPTY, Signal.OFF, false);
    Signal[] inputs = {Signal.MAX, Signal.OFF, Signal.MAX};
    for (Signal input : inputs) {
      now = now.line().pass(input, ONE_SECOND, now.output(), TWO);
    }
    for (int tick = 0; tick < DELAY * TWO_CHANGES; tick++) {
      now = now.line().pass(Signal.MAX, ONE_SECOND, now.output(), TWO);
    }

    assertEquals(Signal.MAX, now.output());
    assertTrue(now.line().isEmpty());
  }

  @Test
  void theLatestValueIsTheLastPendingOrTheOutput() {
    DelayLine line = DelayLine.EMPTY.pass(Signal.MAX, ONE_SECOND, Signal.OFF, STARTING).line();

    assertEquals(Signal.MAX, line.latest(Signal.OFF));
    assertEquals(Signal.OFF, DelayLine.EMPTY.latest(Signal.OFF));
  }

  @Test
  void theOutputBeforeASavedLineIsOffBeforeAnOnAndTheStrengthBeforeAnOff() {
    DelayLine turningOn = DelayLine.restore(List.of(new DelayLine.Pending(Signal.MAX, DELAY)));
    DelayLine turningOff = DelayLine.restore(List.of(new DelayLine.Pending(Signal.OFF, DELAY)));

    assertEquals(Signal.OFF, turningOn.outputBefore(SEVEN));
    assertEquals(SEVEN, turningOff.outputBefore(SEVEN));
    assertEquals(SEVEN, DelayLine.EMPTY.outputBefore(SEVEN));
  }

  @Test
  void aNewStrengthRewritesWhatWaitsOn() {
    DelayLine line =
        DelayLine.restore(
            List.of(new DelayLine.Pending(FIVE, DELAY), new DelayLine.Pending(Signal.OFF, DELAY)));

    assertEquals(
        List.of(new DelayLine.Pending(SEVEN, DELAY), new DelayLine.Pending(Signal.OFF, DELAY)),
        line.withStrength(SEVEN).pending());
  }

  @Test
  void aRestoredLineKeepsItsOrder() {
    DelayLine line =
        DelayLine.restore(
            List.of(
                new DelayLine.Pending(Signal.MAX, DELAY), new DelayLine.Pending(Signal.OFF, 1)));

    assertEquals(
        List.of(new DelayLine.Pending(Signal.MAX, DELAY), new DelayLine.Pending(Signal.OFF, DELAY)),
        line.pending());
  }

  @Test
  void aSavedChangeOfNoTicksComesOutOnTheNextTick() {
    DelayLine line = DelayLine.restore(List.of(new DelayLine.Pending(Signal.MAX, 0)));

    DelayLine.Passage passage = line.pass(Signal.MAX, ONE_SECOND, Signal.OFF, STARTING);

    assertEquals(Signal.MAX, passage.output());
    assertTrue(passage.line().isEmpty());
  }

  @Test
  void aPendingChangeCannotWaitNegativeTicksNorMissItsSignal() {
    assertThrows(IllegalArgumentException.class, () -> new DelayLine.Pending(Signal.MAX, NEGATIVE));
    assertThrows(NullPointerException.class, () -> new DelayLine.Pending(null, DELAY));
  }

  @Test
  void aLineAtTheSameStrengthIsTheSameLine() {
    DelayLine line =
        DelayLine.restore(
            List.of(new DelayLine.Pending(SEVEN, DELAY), new DelayLine.Pending(Signal.OFF, DELAY)));

    assertSame(line, line.withStrength(SEVEN));
  }

  @Test
  void twoLinesWithTheSameChangesAreEqual() {
    List<DelayLine.Pending> changes = List.of(new DelayLine.Pending(SEVEN, DELAY));
    DelayLine line = DelayLine.restore(changes);

    assertEquals(line, DelayLine.restore(changes));
    assertEquals(line.hashCode(), DelayLine.restore(changes).hashCode());
    assertNotEquals(line, DelayLine.EMPTY);
    assertNotEquals(line, changes);
  }
}
