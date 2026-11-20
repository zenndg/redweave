package io.github.zenndg.redweave.domain.module;

import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.Objects;

/**
 * The rule of the Signal Timer, one tick at a time. The cycle starts from zero with the pulse
 * first, so starting fires at once; stopping and pausing reset it (answers 380, 390 and 408). The
 * caller ticks it only while its chunk is loaded, so an unloaded timer freezes where it is (381).
 *
 * @param elapsedTicks the ticks since the cycle started, always 0 when not running
 */
public record SignalTimer(TimerCycle cycle, TimerState state, int elapsedTicks) {
  private static final String MISSING = "A timer needs a cycle and a state";
  private static final String INVALID_PROGRESS = "The progress %d is outside the interval of %d";

  public SignalTimer {
    Objects.requireNonNull(cycle, MISSING);
    Objects.requireNonNull(state, MISSING);
    boolean outside = elapsedTicks < 0 || elapsedTicks >= cycle.intervalTicks();
    if (outside || (state != TimerState.RUNNING && elapsedTicks != 0)) {
      throw new IllegalArgumentException(
          String.format(INVALID_PROGRESS, elapsedTicks, cycle.intervalTicks()));
    }
  }

  /** A new timer, stopped until its owner starts it (answer 390). */
  public static SignalTimer stopped(TimerCycle cycle) {
    return new SignalTimer(cycle, TimerState.STOPPED, 0);
  }

  /**
   * A timer from the saves, which keep whether it runs and its progress, not the pause: the next
   * tick reads the `Pause` input again (answers 381 and 411).
   */
  public static SignalTimer restore(TimerCycle cycle, boolean running, int elapsedTicks) {
    return new SignalTimer(cycle, running ? TimerState.RUNNING : TimerState.STOPPED, elapsedTicks);
  }

  public SignalTimer start() {
    return isRunning() ? this : new SignalTimer(cycle, TimerState.RUNNING, 0);
  }

  public SignalTimer stop() {
    return stopped(cycle);
  }

  /** The `Toggle` of the Configurator and the button of the screen (answers 380 and 395). */
  public SignalTimer toggle() {
    return isRunning() ? stop() : start();
  }

  /** One tick: any `Pause` signal pauses and resets; its release restarts the cycle (408, 412). */
  public SignalTimer tick(Signal pause) {
    if (state == TimerState.STOPPED) {
      return this;
    }
    if (pause.isOn()) {
      return new SignalTimer(cycle, TimerState.PAUSED, 0);
    }
    if (state == TimerState.PAUSED) {
      return new SignalTimer(cycle, TimerState.RUNNING, 0);
    }
    return new SignalTimer(cycle, state, (elapsedTicks + 1) % cycle.intervalTicks());
  }

  /** New settings restart the cycle, keeping whether it runs (answer 409). */
  public SignalTimer withCycle(TimerCycle next) {
    return new SignalTimer(next, state, 0);
  }

  /** On during the pulse at the start of each interval, with the strongest signal (answer 322). */
  public Signal output() {
    return state == TimerState.RUNNING && elapsedTicks < cycle.pulseTicks()
        ? Signal.MAX
        : Signal.OFF;
  }

  /** Whether the owner left it running, paused or not: the `running` LED and save key (401). */
  public boolean isRunning() {
    return state != TimerState.STOPPED;
  }

  /** The ticks to the next pulse, for the clock of the screen (answer 404). */
  public int remainingTicks() {
    return cycle.intervalTicks() - elapsedTicks;
  }
}
