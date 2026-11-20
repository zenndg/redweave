package io.github.zenndg.redweave.domain.signal;

import java.util.Objects;

/**
 * The output of a module after its delay: what the rule of the module decides now enters the delay
 * line, and what comes out of it is the output (answers 830 to 854). The rule, the state and the
 * light follow the module at once; only the output waits (answers 845, 851 and 852). The first tick
 * with nothing waiting, after placing or loading, comes out at once, as the module did before the
 * delay.
 */
public final class DelayedOutput {
  /** A new module: no delay, nothing waiting, off until its first tick. */
  public static final DelayedOutput NONE =
      new DelayedOutput(OutputDelay.NONE, DelayLine.EMPTY, Signal.OFF, true);

  private final OutputDelay delay;
  private final DelayLine line;
  private final Signal output;
  private final boolean fresh;

  private DelayedOutput(OutputDelay delay, DelayLine line, Signal output, boolean fresh) {
    this.delay = Objects.requireNonNull(delay);
    this.line = Objects.requireNonNull(line);
    this.output = Objects.requireNonNull(output);
    this.fresh = fresh;
  }

  /**
   * An output from the saves, with its delay and what waits, at the strength of the module until
   * the first change comes out (answers 836 and 854).
   */
  public static DelayedOutput restore(OutputDelay delay, DelayLine line, Signal strength) {
    return new DelayedOutput(delay, line, line.outputBefore(strength), true);
  }

  /**
   * An output from the saves with what it gave while its changes waited, as a node has no strength
   * of its own to tell it (answer 873).
   */
  public static DelayedOutput resumed(OutputDelay delay, DelayLine line, Signal output) {
    return new DelayedOutput(delay, line, output, true);
  }

  /** One tick, with what the rule of the module decides now. */
  public DelayedOutput tick(Signal value, DelayLineLimits limits) {
    Objects.requireNonNull(value);
    if (fresh && line.isEmpty()) {
      return new DelayedOutput(delay, line, value, false);
    }
    DelayLine.Passage passage = line.pass(value, delay, output, limits);
    return new DelayedOutput(delay, passage.line(), passage.output(), false);
  }

  /** The same output with a new delay; what waits keeps its tick (answer 839). */
  public DelayedOutput withDelay(OutputDelay next) {
    return next.equals(delay) ? this : new DelayedOutput(next, line, output, fresh);
  }

  /**
   * Off at once, with nothing waiting, as a node that stops counting (answer 862). What comes next
   * is a change like any other and waits for the delay (answer 874).
   */
  public DelayedOutput cut() {
    if (!fresh && line.isEmpty() && !output.isOn()) {
      return this;
    }
    return new DelayedOutput(delay, DelayLine.EMPTY, Signal.OFF, false);
  }

  /** Whether the value is already out with nothing waiting, so a tick would change nothing. */
  public boolean follows(Signal value) {
    return !fresh && line.isEmpty() && output.equals(value);
  }

  /** The output and what waits on, at a new strength at once (answers 843 and 854). */
  public DelayedOutput withStrength(Signal strength) {
    Signal now = output.isOn() ? strength : Signal.OFF;
    DelayLine next = line.withStrength(strength);
    if (now.equals(output) && next == line) {
      return this;
    }
    return new DelayedOutput(delay, next, now, fresh);
  }

  public Signal output() {
    return output;
  }

  public OutputDelay delay() {
    return delay;
  }

  /** The changes still waiting, saved with the module (answer 836). */
  public DelayLine line() {
    return line;
  }
}
