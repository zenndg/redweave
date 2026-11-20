package io.github.zenndg.redweave.domain.module;

import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.Objects;

/**
 * The rule of the Signal Randomizer, one tick at a time: stopped at first; started, it draws at
 * once and then at every interval, and holds the value until the next draw; stopped, its output is
 * off (answers 476, 478, 480 and 482). A new mode while running draws again at once, and a new
 * interval restarts it (answers 492 and 493). All of it is saved, so it goes on from where it was
 * (answer 481).
 */
public record SignalRandomizer(
    RandomizerMode mode, Interval interval, boolean running, int elapsedTicks, Signal value) {
  private static final int INITIAL_INTERVAL = 20;
  private static final String INVALID = "The progress goes from 0 to %d, got %d";

  public SignalRandomizer {
    Objects.requireNonNull(mode);
    Objects.requireNonNull(interval);
    Objects.requireNonNull(value);
    if (elapsedTicks < 0 || elapsedTicks >= interval.ticks()) {
      throw new IllegalArgumentException(
          String.format(INVALID, interval.ticks() - 1, elapsedTicks));
    }
  }

  /** A new randomizer: stopped, binary, every second, within the limits (answer 482). */
  public static SignalRandomizer placed(RandomizerLimits limits) {
    return new SignalRandomizer(
        RandomizerMode.BINARY, Interval.within(INITIAL_INTERVAL, limits), false, 0, Signal.OFF);
  }

  /** Starts it with a draw at once, or stops it with the output off (answers 480 and 482). */
  public SignalRandomizer toggle(Draws draws) {
    if (running) {
      return new SignalRandomizer(mode, interval, false, 0, Signal.OFF);
    }
    return new SignalRandomizer(mode, interval, true, 0, mode.draw(draws));
  }

  /** One tick: at the end of the interval, a new draw (answer 478). */
  public SignalRandomizer tick(Draws draws) {
    if (!running) {
      return this;
    }
    int elapsed = elapsedTicks + 1;
    if (elapsed < interval.ticks()) {
      return new SignalRandomizer(mode, interval, true, elapsed, value);
    }
    return new SignalRandomizer(mode, interval, true, 0, mode.draw(draws));
  }

  /** Another mode; while running, a draw in it at once and the interval again (answer 492). */
  public SignalRandomizer withMode(RandomizerMode next, Draws draws) {
    if (!running) {
      return new SignalRandomizer(next, interval, false, 0, value);
    }
    return new SignalRandomizer(next, interval, true, 0, next.draw(draws));
  }

  /** Another interval, clamped to the limits, which restarts it and keeps the value (493). */
  public SignalRandomizer withInterval(int ticks, RandomizerLimits limits) {
    return new SignalRandomizer(mode, Interval.within(ticks, limits), running, 0, value);
  }

  /** Whether the interval is within the limits, as a saved randomizer after the config changed. */
  public boolean fits(RandomizerLimits limits) {
    return interval.fits(limits);
  }

  /** The same randomizer within the limits, restarted, after the config changed (answer 425). */
  public SignalRandomizer fittedTo(RandomizerLimits limits) {
    return new SignalRandomizer(mode, interval.fittedTo(limits), running, 0, value);
  }

  public Signal output() {
    return running ? value : Signal.OFF;
  }

  public RandomizerState state() {
    return running ? RandomizerState.RUNNING : RandomizerState.STOPPED;
  }
}
