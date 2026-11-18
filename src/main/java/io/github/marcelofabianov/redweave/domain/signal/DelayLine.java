package io.github.marcelofabianov.redweave.domain.signal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The changes an output still waits for, oldest first, each with its signal and the ticks it still
 * waits: only the changes, never the signal of every tick (answers 831, 832 and 854, rule 18). A
 * change never comes out before the one ahead of it, also after the delay changed (answer 839); a
 * full line lets the next change wait outside until there is room (answer 847); and the ticks count
 * only while the module runs, so a line saved with an unloaded chunk goes on where it stopped
 * (answers 836 and 838).
 */
public final class DelayLine {
  /** A line with nothing waiting. */
  public static final DelayLine EMPTY = new DelayLine(List.of());

  private static final int NEXT_TICK = 1;

  private final List<Pending> pending;

  private DelayLine(List<Pending> pending) {
    this.pending = List.copyOf(pending);
  }

  /**
   * A line from the saves, kept in order: no change waits less than the one ahead of it, nor less
   * than the next tick, when a change still waiting always is.
   */
  public static DelayLine restore(List<Pending> saved) {
    List<Pending> ordered = new ArrayList<>();
    int floor = NEXT_TICK;
    for (Pending change : saved) {
      floor = Math.max(floor, change.ticks());
      ordered.add(new Pending(change.signal(), floor));
    }
    return new DelayLine(ordered);
  }

  /**
   * One tick: what waits comes one tick nearer, the value enters when it differs from the latest
   * and there is room, and the changes due come out. With no delay and nothing waiting, it comes
   * out at once.
   */
  public Passage pass(Signal value, OutputDelay delay, Signal output, DelayLineLimits limits) {
    List<Pending> next = new ArrayList<>();
    pending.forEach(change -> next.add(new Pending(change.signal(), change.ticks() - 1)));
    boolean entered = !value.equals(latest(output)) && next.size() < limits.maxDelayChanges();
    if (entered) {
      int last = next.isEmpty() ? 0 : next.getLast().ticks();
      next.add(new Pending(value, Math.max(delay.ticks(), last)));
    }
    Signal out = output;
    while (!next.isEmpty() && next.getFirst().ticks() == 0) {
      out = next.removeFirst().signal();
    }
    return new Passage(new DelayLine(next), out, entered);
  }

  /** The value the output will have when the line is through: its last change, or the output. */
  public Signal latest(Signal output) {
    return pending.isEmpty() ? output : pending.getLast().signal();
  }

  /**
   * The output while the first change waits, as a line from the saves starts: off before a change
   * that turns on, and the strength before one that turns off.
   */
  public Signal outputBefore(Signal strength) {
    if (pending.isEmpty() || !pending.getFirst().signal().isOn()) {
      return strength;
    }
    return Signal.OFF;
  }

  /** The same line with every change that turns on at the new strength (answers 843 and 854). */
  public DelayLine withStrength(Signal strength) {
    boolean same =
        pending.stream()
            .allMatch(change -> !change.signal().isOn() || change.signal().equals(strength));
    if (same) {
      return this;
    }
    List<Pending> next = new ArrayList<>();
    pending.forEach(change ->
        next.add(new Pending(change.signal().isOn() ? strength : Signal.OFF, change.ticks())));
    return new DelayLine(next);
  }

  public boolean isEmpty() {
    return pending.isEmpty();
  }

  public List<Pending> pending() {
    return pending;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof DelayLine line && line.pending.equals(pending);
  }

  @Override
  public int hashCode() {
    return pending.hashCode();
  }

  /** A change waiting to come out: its signal and the ticks it still waits. */
  public record Pending(Signal signal, int ticks) {
    private static final String INVALID = "A change cannot wait %d ticks";

    public Pending {
      Objects.requireNonNull(signal);
      if (ticks < 0) {
        throw new IllegalArgumentException(String.format(INVALID, ticks));
      }
    }
  }

  /** A line after a tick: what still waits, the output now, and whether a change entered. */
  public record Passage(DelayLine line, Signal output, boolean entered) {}
}
