package io.github.marcelofabianov.redweave.domain.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * The delay line of an output: only the changes, each with the ticks it still waits, on and off
 * alike, in order, with short pulses whole (answers 831, 832, 838, 839 and 843).
 */
class DelayLineTest {
  private static final OutputDelay ONE_SECOND = new OutputDelay(OutputDelay.STEP_TICKS);
  private static final int DELAY = ONE_SECOND.ticks();
  private static final OutputDelay LONGER = new OutputDelay(2 * OutputDelay.STEP_TICKS);
  private static final int NEGATIVE = -1;
  private static final int SHORT_PULSE = 3;
  private static final String ON_MARK = "1";
  private static final String OFF_MARK = "0";

  private static DelayLine.Passage run(DelayLine line, boolean on, boolean output, int ticks) {
    DelayLine.Passage now = new DelayLine.Passage(line, output, false);
    for (int tick = 0; tick < ticks; tick++) {
      now = now.line().pass(on, ONE_SECOND, now.output());
    }
    return now;
  }

  @Test
  void anEmptyLineWithNoDelayPassesAtOnce() {
    DelayLine.Passage passage = DelayLine.EMPTY.pass(true, OutputDelay.NONE, false);

    assertTrue(passage.output());
    assertTrue(passage.entered());
    assertTrue(passage.line().isEmpty());
  }

  @Test
  void theSameValueEntersNothing() {
    DelayLine.Passage passage = DelayLine.EMPTY.pass(false, ONE_SECOND, false);

    assertFalse(passage.entered());
    assertTrue(passage.line().isEmpty());
  }

  @Test
  void aChangeComesOutAfterTheDelay() {
    DelayLine.Passage entered = DelayLine.EMPTY.pass(true, ONE_SECOND, false);
    DelayLine.Passage waiting = run(entered.line(), true, false, DELAY - 1);
    DelayLine.Passage out = run(entered.line(), true, false, DELAY);

    assertTrue(entered.entered());
    assertFalse(entered.output());
    assertFalse(waiting.output());
    assertTrue(out.output());
    assertTrue(out.line().isEmpty());
  }

  @Test
  void turningOffIsDelayedToo() {
    DelayLine.Passage entered = DelayLine.EMPTY.pass(false, ONE_SECOND, true);
    DelayLine.Passage out = run(entered.line(), false, true, DELAY);

    assertTrue(run(entered.line(), false, true, DELAY - 1).output());
    assertFalse(out.output());
  }

  @Test
  void aShortPulsePassesWholeAndLate() {
    DelayLine.Passage now = new DelayLine.Passage(DelayLine.EMPTY, false, false);
    StringBuilder seen = new StringBuilder();
    for (int tick = 0; tick < DELAY + SHORT_PULSE + 1; tick++) {
      boolean input = tick < SHORT_PULSE;
      now = now.line().pass(input, ONE_SECOND, now.output());
      seen.append(now.output() ? ON_MARK : OFF_MARK);
    }

    String expected = OFF_MARK.repeat(DELAY) + ON_MARK.repeat(SHORT_PULSE) + OFF_MARK;
    assertEquals(expected, seen.toString());
  }

  @Test
  void aShorterDelayNeverOvertakesAPendingChange() {
    DelayLine.Passage first = DelayLine.EMPTY.pass(true, LONGER, false);

    DelayLine.Passage second = first.line().pass(false, OutputDelay.NONE, false);

    assertFalse(second.output());
    assertEquals(
        List.of(
            new DelayLine.Pending(true, LONGER.ticks() - 1),
            new DelayLine.Pending(false, LONGER.ticks() - 1)),
        second.line().pending());
  }

  @Test
  void pendingChangesKeepTheirTickWhenTheDelayChanges() {
    DelayLine.Passage first = DelayLine.EMPTY.pass(true, LONGER, false);

    DelayLine.Passage still = first.line().pass(true, ONE_SECOND, false);

    assertEquals(List.of(new DelayLine.Pending(true, LONGER.ticks() - 1)), still.line().pending());
  }

  @Test
  void theLatestValueIsTheLastPendingOrTheOutput() {
    DelayLine line = DelayLine.EMPTY.pass(true, ONE_SECOND, false).line();

    assertTrue(line.latest(false));
    assertFalse(DelayLine.EMPTY.latest(false));
  }

  @Test
  void theOutputBeforeASavedLineIsTheOppositeOfItsFirstChange() {
    DelayLine line = DelayLine.restore(List.of(new DelayLine.Pending(true, DELAY)));

    assertFalse(line.outputBefore(true));
    assertTrue(DelayLine.EMPTY.outputBefore(true));
    assertTrue(DelayLine.restore(List.of(new DelayLine.Pending(false, DELAY))).outputBefore(false));
  }

  @Test
  void aRestoredLineKeepsItsOrder() {
    DelayLine line =
        DelayLine.restore(
            List.of(new DelayLine.Pending(true, DELAY), new DelayLine.Pending(false, 1)));

    assertEquals(
        List.of(new DelayLine.Pending(true, DELAY), new DelayLine.Pending(false, DELAY)),
        line.pending());
  }

  @Test
  void aSavedChangeOfNoTicksComesOutOnTheNextTick() {
    DelayLine line = DelayLine.restore(List.of(new DelayLine.Pending(true, 0)));

    DelayLine.Passage passage = line.pass(true, ONE_SECOND, false);

    assertTrue(passage.output());
    assertTrue(passage.line().isEmpty());
  }

  @Test
  void aPendingChangeCannotWaitNegativeTicks() {
    assertThrows(IllegalArgumentException.class, () -> new DelayLine.Pending(true, NEGATIVE));
  }
}
