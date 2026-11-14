package io.github.marcelofabianov.redweave.domain.module;

import static io.github.marcelofabianov.redweave.domain.module.TestInverterLimits.STARTING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
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

  private static SignalInverter run(SignalInverter inverter, Signal input, int delay, int ticks) {
    SignalInverter now = inverter;
    for (int tick = 0; tick < ticks; tick++) {
      now = now.tick(input, delay, STARTING);
    }
    return now;
  }

  @Test
  void aPlacedInverterWithNoInputEmitsAtOnce() {
    SignalInverter first = SignalInverter.placed().tick(Signal.OFF, EDGE, STARTING);

    assertEquals(Signal.MAX, first.output());
    assertEquals(InverterState.ON, first.state());
  }

  @Test
  void aPlacedInverterWithAnInputIsOffAtOnce() {
    SignalInverter first = SignalInverter.placed().tick(Signal.MAX, EDGE, STARTING);

    assertEquals(Signal.OFF, first.output());
    assertEquals(InverterState.OFF, first.state());
  }

  @Test
  void anInputOnTheEdgeShowsTwoTicksLater() {
    SignalInverter steady = run(SignalInverter.placed(), Signal.OFF, EDGE, SLOW);

    SignalInverter one = steady.tick(Signal.MAX, EDGE, STARTING);
    SignalInverter two = one.tick(Signal.MAX, EDGE, STARTING);
    SignalInverter three = two.tick(Signal.MAX, EDGE, STARTING);

    assertEquals(Signal.MAX, one.output());
    assertEquals(Signal.MAX, two.output());
    assertEquals(Signal.OFF, three.output());
  }

  @Test
  void anInputOnAChannelShowsOneTickLaterAsItIsAlreadyOneOld() {
    SignalInverter steady = run(SignalInverter.placed(), Signal.OFF, CHANNEL, SLOW);

    SignalInverter one = steady.tick(Signal.MAX, CHANNEL, STARTING);
    SignalInverter two = one.tick(Signal.MAX, CHANNEL, STARTING);

    assertEquals(Signal.MAX, one.output());
    assertEquals(Signal.OFF, two.output());
  }

  @Test
  void aConnectionThatChangesItsDelayReadsTheInputAgain() {
    SignalInverter onTheEdge = run(SignalInverter.placed(), Signal.OFF, EDGE, SLOW);

    SignalInverter onAChannel = onTheEdge.tick(Signal.MAX, CHANNEL, STARTING);

    assertEquals(Signal.OFF, onAChannel.output());
  }

  @Test
  void aLoopBurnsOutAndComesBackAlone() {
    SignalInverter now = run(SignalInverter.placed(), Signal.OFF, CHANNEL, 1);
    for (int tick = 0; tick < STARTING.burnoutWindowTicks(); tick++) {
      if (now.state() == InverterState.BURNT_OUT) {
        break;
      }
      now = now.tick(now.output(), CHANNEL, STARTING);
    }

    assertEquals(InverterState.BURNT_OUT, now.state());
    assertEquals(Signal.OFF, now.output());
    assertEquals(STARTING.burnoutTicks(), now.burnoutTicks());
    SignalInverter cooled = run(now, Signal.OFF, CHANNEL, STARTING.burnoutTicks());
    assertEquals(InverterState.OFF, cooled.state());
    assertEquals(Signal.MAX, cooled.tick(Signal.OFF, CHANNEL, STARTING).output());
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
    SignalInverter restored = SignalInverter.restore(STARTING.burnoutTicks());

    assertEquals(InverterState.BURNT_OUT, restored.state());
    assertEquals(
        STARTING.burnoutTicks() - 1, restored.tick(Signal.OFF, EDGE, STARTING).burnoutTicks());
    assertThrows(IllegalArgumentException.class, () -> SignalInverter.restore(NEGATIVE));
    assertThrows(
        IllegalArgumentException.class,
        () -> SignalInverter.placed().tick(Signal.OFF, 0, STARTING));
  }
}
