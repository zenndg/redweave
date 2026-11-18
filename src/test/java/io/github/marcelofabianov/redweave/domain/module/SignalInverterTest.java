package io.github.marcelofabianov.redweave.domain.module;

import static io.github.marcelofabianov.redweave.domain.module.TestInverterLimits.STARTING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.signal.DelayLine;
import io.github.marcelofabianov.redweave.domain.signal.OutputDelay;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.domain.signal.TestDelayLimits;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * The rule of the Signal Inverter: the NOT of the input with the delay of a torch, and the burnout
 * of a loop (answers 296, 442, 444, 445, 446 and 448).
 */
class SignalInverterTest {
  private static final int EDGE = SignalInverter.EDGE_DELAY;
  private static final int CHANNEL = SignalInverter.CHANNEL_DELAY;
  private static final int SLOW = 10;
  private static final int NEGATIVE = -1;
  private static final int WEAK = 4;
  private static final TestDelayLimits LINE = TestDelayLimits.STARTING;
  private static final int BOTH_CHANGES = 2;
  private static final Signal WEAK_SIGNAL = new Signal(WEAK);
  private static final OutputDelay ONE_SECOND = new OutputDelay(OutputDelay.STEP_TICKS);
  private static final int SECOND = ONE_SECOND.ticks();

  private static SignalInverter delayed() {
    return run(SignalInverter.placed().withDelay(ONE_SECOND), Signal.OFF, EDGE, SLOW);
  }

  private static SignalInverter run(SignalInverter inverter, Signal input, int delay, int ticks) {
    SignalInverter now = inverter;
    for (int tick = 0; tick < ticks; tick++) {
      now = now.tick(input, delay, STARTING, LINE);
    }
    return now;
  }

  @Test
  void aPlacedInverterWithNoInputEmitsAtOnce() {
    SignalInverter first = SignalInverter.placed().tick(Signal.OFF, EDGE, STARTING, LINE);

    assertEquals(Signal.MAX, first.output());
    assertEquals(InverterState.ON, first.state());
  }

  @Test
  void aPlacedInverterWithAnInputIsOffAtOnce() {
    SignalInverter first = SignalInverter.placed().tick(Signal.MAX, EDGE, STARTING, LINE);

    assertEquals(Signal.OFF, first.output());
    assertEquals(InverterState.OFF, first.state());
  }

  @Test
  void anInputOnTheEdgeShowsTwoTicksLater() {
    SignalInverter steady = run(SignalInverter.placed(), Signal.OFF, EDGE, SLOW);

    SignalInverter one = steady.tick(Signal.MAX, EDGE, STARTING, LINE);
    SignalInverter two = one.tick(Signal.MAX, EDGE, STARTING, LINE);
    SignalInverter three = two.tick(Signal.MAX, EDGE, STARTING, LINE);

    assertEquals(Signal.MAX, one.output());
    assertEquals(Signal.MAX, two.output());
    assertEquals(Signal.OFF, three.output());
  }

  @Test
  void anInputOnAChannelShowsOneTickLaterAsItIsAlreadyOneOld() {
    SignalInverter steady = run(SignalInverter.placed(), Signal.OFF, CHANNEL, SLOW);

    SignalInverter one = steady.tick(Signal.MAX, CHANNEL, STARTING, LINE);
    SignalInverter two = one.tick(Signal.MAX, CHANNEL, STARTING, LINE);

    assertEquals(Signal.MAX, one.output());
    assertEquals(Signal.OFF, two.output());
  }

  @Test
  void aConnectionThatChangesItsDelayReadsTheInputAgain() {
    SignalInverter onTheEdge = run(SignalInverter.placed(), Signal.OFF, EDGE, SLOW);

    SignalInverter onAChannel = onTheEdge.tick(Signal.MAX, CHANNEL, STARTING, LINE);

    assertEquals(Signal.OFF, onAChannel.output());
  }

  @Test
  void aLoopBurnsOutAndComesBackAlone() {
    SignalInverter now = run(SignalInverter.placed(), Signal.OFF, CHANNEL, 1);
    for (int tick = 0; tick < STARTING.burnoutWindowTicks(); tick++) {
      if (now.state() == InverterState.BURNT_OUT) {
        break;
      }
      now = now.tick(now.output(), CHANNEL, STARTING, LINE);
    }

    assertEquals(InverterState.BURNT_OUT, now.state());
    assertEquals(Signal.OFF, now.output());
    assertEquals(STARTING.burnoutTicks(), now.burnoutTicks());
    SignalInverter cooled = run(now, Signal.OFF, CHANNEL, STARTING.burnoutTicks());
    assertEquals(InverterState.OFF, cooled.state());
    assertEquals(Signal.MAX, cooled.tick(Signal.OFF, CHANNEL, STARTING, LINE).output());
  }

  @Test
  void slowChangesDoNotBurnOut() {
    SignalInverter now = run(SignalInverter.placed(), Signal.OFF, EDGE, 1);
    Signal input = Signal.OFF;
    for (int change = 0; change <= STARTING.burnoutToggles(); change++) {
      input = input.isOn() ? Signal.OFF : Signal.MAX;
      now = run(now, input, EDGE, SLOW);
    }

    assertEquals(InverterState.OFF, now.state());
  }

  @Test
  void aSavedBurnoutGoesOnAndAnInvalidOneIsRefused() {
    SignalInverter restored =
        SignalInverter.restore(STARTING.burnoutTicks(), InverterSettings.DEFAULT, DelayLine.EMPTY);

    assertEquals(InverterState.BURNT_OUT, restored.state());
    assertEquals(
        STARTING.burnoutTicks() - 1,
        restored.tick(Signal.OFF, EDGE, STARTING, LINE).burnoutTicks());
    assertThrows(
        IllegalArgumentException.class,
        () -> SignalInverter.restore(NEGATIVE, InverterSettings.DEFAULT, DelayLine.EMPTY));
    assertThrows(
        IllegalArgumentException.class,
        () -> SignalInverter.placed().tick(Signal.OFF, 0, STARTING, LINE));
  }

  @Test
  void aPlacedInverterGivesFifteenAndNoDelay() {
    SignalInverter placed = SignalInverter.placed();

    assertEquals(Signal.MAX, placed.settings().strength());
    assertEquals(OutputDelay.NONE, placed.settings().delay());
  }

  @Test
  void theOutputHasTheChosenStrength() {
    SignalInverter weak = SignalInverter.placed().withStrength(WEAK);

    assertEquals(WEAK_SIGNAL, weak.tick(Signal.OFF, EDGE, STARTING, LINE).output());
  }

  @Test
  void aStrengthOutOfRangeStaysFromOneToFifteen() {
    assertEquals(new Signal(1), SignalInverter.placed().withStrength(0).settings().strength());
    assertEquals(Signal.MAX, SignalInverter.placed().withStrength(SLOW * 2).settings().strength());
  }

  @Test
  void aNewStrengthChangesTheOutputAtOnceWithChangesWaiting() {
    SignalInverter waiting = delayed().tick(Signal.MAX, EDGE, STARTING, LINE);
    waiting = run(waiting, Signal.MAX, EDGE, EDGE);

    SignalInverter weaker = waiting.withStrength(WEAK);

    assertEquals(WEAK_SIGNAL, weaker.output());
    assertTrue(!weaker.line().isEmpty());
  }

  @Test
  void theDelayAddsToTheTicksOfTheTorch() {
    SignalInverter now = delayed();

    SignalInverter almost = run(now, Signal.MAX, EDGE, EDGE + SECOND);
    SignalInverter through = almost.tick(Signal.MAX, EDGE, STARTING, LINE);

    assertEquals(Signal.MAX, almost.output());
    assertEquals(Signal.OFF, through.output());
  }

  @Test
  void turningBackOnIsDelayedToo() {
    SignalInverter off = run(delayed(), Signal.MAX, EDGE, SLOW + SECOND);

    SignalInverter almost = run(off, Signal.OFF, EDGE, EDGE + SECOND);

    assertEquals(Signal.OFF, almost.output());
    assertEquals(Signal.MAX, almost.tick(Signal.OFF, EDGE, STARTING, LINE).output());
  }

  @Test
  void aNewDelayKeepsWhatIsWaiting() {
    SignalInverter waiting = run(delayed(), Signal.MAX, EDGE, EDGE + 1);

    SignalInverter longer = waiting.withDelay(new OutputDelay(SECOND * 2));

    assertEquals(waiting.line(), longer.line());
    assertEquals(new OutputDelay(SECOND * 2), longer.settings().delay());
  }

  @Test
  void aLoopWithADelayBurnsOutAsFastAndEmptiesTheLine() {
    SignalInverter now = run(SignalInverter.placed().withDelay(ONE_SECOND), Signal.OFF, CHANNEL, 1);
    Signal input = Signal.OFF;
    for (int tick = 0; tick < STARTING.burnoutWindowTicks(); tick++) {
      if (now.state() == InverterState.BURNT_OUT) {
        break;
      }
      input = input.isOn() ? Signal.OFF : Signal.MAX;
      now = now.tick(input, CHANNEL, STARTING, LINE);
    }

    assertEquals(InverterState.BURNT_OUT, now.state());
    assertTrue(now.line().isEmpty());
  }

  @Test
  void aSavedLineGoesOnFromWhereItStopped() {
    DelayLine saved = DelayLine.restore(List.of(new DelayLine.Pending(Signal.MAX, 1)));
    SignalInverter restored = SignalInverter.restore(0, InverterSettings.DEFAULT, saved);

    SignalInverter first = restored.tick(Signal.OFF, EDGE, STARTING, LINE);

    assertEquals(Signal.OFF, restored.output());
    assertEquals(Signal.MAX, first.output());
  }

  @Test
  void theFirstTickAfterLoadingQueuesBehindTheSavedLineWithoutCountingForTheBurnout() {
    DelayLine saved = DelayLine.restore(List.of(new DelayLine.Pending(Signal.MAX, SECOND)));
    SignalInverter restored = SignalInverter.restore(0, InverterSettings.DEFAULT, saved);

    SignalInverter first = restored.tick(Signal.MAX, EDGE, STARTING, LINE);

    assertEquals(Signal.OFF, first.output());
    assertEquals(BOTH_CHANGES, first.line().pending().size());
  }

  @Test
  void aConnectionThatChangesWithADelayWaitsForIt() {
    SignalInverter onTheEdge = delayed();

    SignalInverter onAChannel = onTheEdge.tick(Signal.MAX, CHANNEL, STARTING, LINE);

    assertEquals(Signal.MAX, onAChannel.output());
    assertTrue(!onAChannel.line().isEmpty());
  }

  @Test
  void settingsRefuseMissingValues() {
    assertThrows(NullPointerException.class, () -> new InverterSettings(null, OutputDelay.NONE));
    assertThrows(NullPointerException.class, () -> new InverterSettings(Signal.MAX, null));
  }

  @Test
  void anOffInverterStaysOffWithANewStrength() {
    SignalInverter off = SignalInverter.placed().tick(Signal.MAX, EDGE, STARTING, LINE);

    assertEquals(Signal.OFF, off.withStrength(WEAK).output());
  }
}
