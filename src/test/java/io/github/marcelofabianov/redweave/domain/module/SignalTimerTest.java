package io.github.marcelofabianov.redweave.domain.module;

import static io.github.marcelofabianov.redweave.domain.module.TestTimerLimits.STARTING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import org.junit.jupiter.api.Test;

/**
 * The Signal Timer tick by tick: it starts stopped, starts the cycle from zero with the pulse
 * first, stops and resets while `Pause` is on, and restarts the cycle when its settings change
 * (answers 380, 381, 390, 408 and 409).
 */
class SignalTimerTest {
  private static final int INTERVAL = 6;
  private static final int PULSE = 2;
  private static final TimerCycle CYCLE = new TimerCycle(INTERVAL, PULSE);
  private static final Signal WEAK_PAUSE = new Signal(1);
  private static final int LONGER_INTERVAL = 8;

  private static SignalTimer running() {
    return SignalTimer.stopped(CYCLE).start();
  }

  private static SignalTimer ticked(SignalTimer timer, int ticks) {
    SignalTimer result = timer;
    for (int tick = 0; tick < ticks; tick++) {
      result = result.tick(Signal.OFF);
    }
    return result;
  }

  @Test
  void aNewTimerIsStoppedWithItsOutputOff() {
    SignalTimer timer = SignalTimer.stopped(TimerCycle.initial(STARTING));

    assertEquals(TimerState.STOPPED, timer.state());
    assertEquals(Signal.OFF, timer.output());
    assertFalse(timer.isRunning());
  }

  @Test
  void aStoppedTimerDoesNotMove() {
    SignalTimer timer = SignalTimer.stopped(CYCLE);

    assertEquals(timer, ticked(timer, INTERVAL));
  }

  @Test
  void startingFiresThePulseAtOnce() {
    SignalTimer timer = running();

    assertEquals(TimerState.RUNNING, timer.state());
    assertEquals(Signal.MAX, timer.output());
    assertTrue(timer.isRunning());
  }

  @Test
  void theOutputIsOnForThePulseAndOffForTheRestOfTheInterval() {
    SignalTimer timer = running();

    assertEquals(Signal.MAX, ticked(timer, PULSE - 1).output());
    assertEquals(Signal.OFF, ticked(timer, PULSE).output());
    assertEquals(Signal.OFF, ticked(timer, INTERVAL - 1).output());
  }

  @Test
  void theNextIntervalStartsWithAnotherPulse() {
    assertEquals(Signal.MAX, ticked(running(), INTERVAL).output());
  }

  @Test
  void theRemainingTimeCountsDownToTheNextPulse() {
    assertEquals(INTERVAL, running().remainingTicks());
    assertEquals(1, ticked(running(), INTERVAL - 1).remainingTicks());
  }

  @Test
  void startingARunningTimerChangesNothing() {
    SignalTimer timer = ticked(running(), PULSE);

    assertEquals(timer, timer.start());
  }

  @Test
  void stoppingTurnsTheOutputOffAndResetsTheCycle() {
    SignalTimer timer = ticked(running(), PULSE).stop();

    assertEquals(SignalTimer.stopped(CYCLE), timer);
    assertEquals(Signal.OFF, timer.output());
  }

  @Test
  void toggleStartsAStoppedTimerAndStopsARunningOne() {
    assertEquals(running(), SignalTimer.stopped(CYCLE).toggle());
    assertEquals(SignalTimer.stopped(CYCLE), running().toggle());
  }

  @Test
  void anyPauseSignalPausesTheTimerAndTurnsTheOutputOff() {
    SignalTimer timer = running().tick(WEAK_PAUSE);

    assertEquals(TimerState.PAUSED, timer.state());
    assertEquals(Signal.OFF, timer.output());
    assertTrue(timer.isRunning());
  }

  @Test
  void aPausedTimerDoesNotMove() {
    SignalTimer paused = running().tick(Signal.MAX);

    assertEquals(paused, paused.tick(Signal.MAX));
  }

  @Test
  void releasingThePauseRestartsTheCycleWithThePulse() {
    SignalTimer timer = ticked(running(), PULSE).tick(Signal.MAX).tick(Signal.OFF);

    assertEquals(TimerState.RUNNING, timer.state());
    assertEquals(Signal.MAX, timer.output());
    assertEquals(INTERVAL, timer.remainingTicks());
  }

  @Test
  void stoppingAPausedTimerStopsIt() {
    assertEquals(SignalTimer.stopped(CYCLE), running().tick(Signal.MAX).stop());
  }

  @Test
  void aStoppedTimerIgnoresThePause() {
    SignalTimer timer = SignalTimer.stopped(CYCLE);

    assertEquals(timer, timer.tick(Signal.MAX));
  }

  @Test
  void newSettingsRestartTheCycle() {
    TimerCycle longer = CYCLE.withInterval(LONGER_INTERVAL, STARTING);

    SignalTimer timer = ticked(running(), PULSE).withCycle(longer);

    assertEquals(longer, timer.cycle());
    assertEquals(Signal.MAX, timer.output());
    assertEquals(LONGER_INTERVAL, timer.remainingTicks());
  }

  @Test
  void newSettingsKeepAStoppedTimerStopped() {
    SignalTimer timer = SignalTimer.stopped(CYCLE).withCycle(TimerCycle.initial(STARTING));

    assertEquals(TimerState.STOPPED, timer.state());
  }

  @Test
  void aSavedTimerComesBackWhereItStopped() {
    SignalTimer saved = ticked(running(), PULSE);

    SignalTimer restored = SignalTimer.restore(CYCLE, saved.isRunning(), saved.elapsedTicks());

    assertEquals(saved, restored);
  }

  @Test
  void aSavedStoppedTimerComesBackStoppedFromZero() {
    assertEquals(SignalTimer.stopped(CYCLE), SignalTimer.restore(CYCLE, false, 0));
  }

  @Test
  void restoringRejectsProgressOutsideTheInterval() {
    assertThrows(IllegalArgumentException.class, () -> SignalTimer.restore(CYCLE, true, INTERVAL));
    assertThrows(IllegalArgumentException.class, () -> SignalTimer.restore(CYCLE, true, -1));
  }

  @Test
  void aStoppedTimerCannotKeepProgress() {
    assertThrows(IllegalArgumentException.class, () -> SignalTimer.restore(CYCLE, false, PULSE));
  }
}
