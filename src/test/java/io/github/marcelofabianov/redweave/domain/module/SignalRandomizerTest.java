package io.github.marcelofabianov.redweave.domain.module;

import static io.github.marcelofabianov.redweave.domain.module.TestRandomizerLimits.STARTING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import org.junit.jupiter.api.Test;

/**
 * The rule of the Signal Randomizer: stopped at first, a draw when it starts and at every interval,
 * the value held between draws, and the draw again when the mode changes (answers 476, 478, 480,
 * 482, 492 and 493).
 */
class SignalRandomizerTest {
  private static final int ONE_SECOND = 20;
  private static final int SHORT = 4;
  private static final int SEVEN = 7;
  private static final int NINE = 9;
  private static final int HEADS = 1;
  private static final int TAILS = 0;
  private static final int LONGER = 40;
  private static final int RAISED_MINIMUM = 100;
  private static final int NEGATIVE = -1;
  private static final RandomizerLimits RAISED =
      new TestRandomizerLimits(RAISED_MINIMUM, RAISED_MINIMUM);

  private static SignalRandomizer running(RandomizerMode mode, int interval, Draws draws) {
    return SignalRandomizer.placed(STARTING)
        .withMode(mode, draws)
        .withInterval(interval, STARTING)
        .toggle(draws);
  }

  private static SignalRandomizer ticks(SignalRandomizer randomizer, Draws draws, int count) {
    SignalRandomizer now = randomizer;
    for (int tick = 0; tick < count; tick++) {
      now = now.tick(draws);
    }
    return now;
  }

  @Test
  void aPlacedRandomizerIsStoppedBinaryEverySecond() {
    SignalRandomizer placed = SignalRandomizer.placed(STARTING);

    assertEquals(RandomizerMode.BINARY, placed.mode());
    assertEquals(new Interval(ONE_SECOND), placed.interval());
    assertEquals(RandomizerState.STOPPED, placed.state());
    assertEquals(Signal.OFF, placed.output());
  }

  @Test
  void startingDrawsAtOnce() {
    SignalRandomizer started = SignalRandomizer.placed(STARTING).toggle(new FixedDraws(HEADS));

    assertEquals(RandomizerState.RUNNING, started.state());
    assertEquals(Signal.MAX, started.output());
  }

  @Test
  void theBinaryModeDrawsOffOrFifteen() {
    SignalRandomizer started = SignalRandomizer.placed(STARTING).toggle(new FixedDraws(TAILS));

    assertEquals(Signal.OFF, started.output());
  }

  @Test
  void theAnalogModeDrawsAnyStrength() {
    FixedDraws draws = new FixedDraws(SEVEN);

    SignalRandomizer started =
        SignalRandomizer.placed(STARTING).withMode(RandomizerMode.ANALOG, draws).toggle(draws);

    assertEquals(new Signal(SEVEN), started.output());
  }

  @Test
  void theValueHoldsUntilTheNextDraw() {
    FixedDraws draws = new FixedDraws(SEVEN, NINE);
    SignalRandomizer started = running(RandomizerMode.ANALOG, SHORT, draws);

    SignalRandomizer before = ticks(started, draws, SHORT - 1);
    SignalRandomizer after = before.tick(draws);

    assertEquals(new Signal(SEVEN), before.output());
    assertEquals(new Signal(NINE), after.output());
    assertEquals(0, after.elapsedTicks());
  }

  @Test
  void stoppingTurnsTheOutputOff() {
    FixedDraws draws = new FixedDraws(HEADS);
    SignalRandomizer stopped = SignalRandomizer.placed(STARTING).toggle(draws).toggle(draws);

    assertEquals(RandomizerState.STOPPED, stopped.state());
    assertEquals(Signal.OFF, stopped.output());
    assertEquals(stopped, stopped.tick(draws));
  }

  @Test
  void aNewModeWhileRunningDrawsAtOnce() {
    FixedDraws draws = new FixedDraws(SEVEN, HEADS);
    SignalRandomizer analog = running(RandomizerMode.ANALOG, SHORT, draws).tick(draws);

    SignalRandomizer binary = analog.withMode(RandomizerMode.BINARY, draws);

    assertEquals(Signal.MAX, binary.output());
    assertEquals(0, binary.elapsedTicks());
  }

  @Test
  void aNewModeWhileStoppedOnlyChangesTheMode() {
    SignalRandomizer analog =
        SignalRandomizer.placed(STARTING).withMode(RandomizerMode.ANALOG, new FixedDraws());

    assertEquals(RandomizerMode.ANALOG, analog.mode());
    assertEquals(Signal.OFF, analog.output());
  }

  @Test
  void aNewIntervalRestartsItAndKeepsTheValue() {
    FixedDraws draws = new FixedDraws(SEVEN);
    SignalRandomizer started = running(RandomizerMode.ANALOG, SHORT, draws).tick(draws);

    SignalRandomizer longer = started.withInterval(LONGER, STARTING);

    assertEquals(new Interval(LONGER), longer.interval());
    assertEquals(0, longer.elapsedTicks());
    assertEquals(new Signal(SEVEN), longer.output());
  }

  @Test
  void aSavedIntervalFitsTheLimitsOfNow() {
    SignalRandomizer saved = SignalRandomizer.placed(STARTING);

    assertTrue(saved.fits(STARTING));
    assertFalse(saved.fits(RAISED));
    assertEquals(new Interval(RAISED_MINIMUM), saved.fittedTo(RAISED).interval());
  }

  @Test
  void theProgressStaysWithinTheInterval() {
    Interval interval = new Interval(SHORT);

    assertThrows(
        IllegalArgumentException.class,
        () -> new SignalRandomizer(RandomizerMode.BINARY, interval, true, SHORT, Signal.OFF));
    assertThrows(
        IllegalArgumentException.class,
        () -> new SignalRandomizer(RandomizerMode.BINARY, interval, true, NEGATIVE, Signal.OFF));
  }
}
