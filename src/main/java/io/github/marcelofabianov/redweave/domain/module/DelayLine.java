package io.github.marcelofabianov.redweave.domain.module;

import java.util.ArrayList;
import java.util.List;

/**
 * The changes an output still waits for, oldest first, each with the ticks it still waits: only the
 * changes, on or off, never the signal of every tick (answers 831, 832 and 843, rule 18). A change
 * never comes out before the one ahead of it, also after the delay changed (answer 839), and the
 * ticks count only while the module runs, so a line saved with an unloaded chunk goes on where it
 * stopped (answers 836 and 838).
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
      ordered.add(new Pending(change.on(), floor));
    }
    return new DelayLine(ordered);
  }

  /**
   * One tick: what waits comes one tick nearer, the value enters when it differs from the latest,
   * and the changes due come out. With no delay and nothing waiting, it comes out at once.
   */
  public Passage pass(boolean on, OutputDelay delay, boolean output) {
    List<Pending> next = new ArrayList<>();
    pending.forEach(change -> next.add(new Pending(change.on(), change.ticks() - 1)));
    boolean entered = on != latest(output);
    if (entered) {
      int last = next.isEmpty() ? 0 : next.getLast().ticks();
      next.add(new Pending(on, Math.max(delay.ticks(), last)));
    }
    boolean out = output;
    while (!next.isEmpty() && next.getFirst().ticks() == 0) {
      out = next.removeFirst().on();
    }
    return new Passage(new DelayLine(next), out, entered);
  }

  /** The value the output will have when the line is through: its last change, or the output. */
  public boolean latest(boolean output) {
    return pending.isEmpty() ? output : pending.getLast().on();
  }

  /** The output while the first change waits, as a line from the saves starts: its opposite. */
  public boolean outputBefore(boolean output) {
    return pending.isEmpty() ? output : !pending.getFirst().on();
  }

  public boolean isEmpty() {
    return pending.isEmpty();
  }

  public List<Pending> pending() {
    return pending;
  }

  /** A change waiting to come out: on or off, and the ticks it still waits. */
  public record Pending(boolean on, int ticks) {
    private static final String INVALID = "A change cannot wait %d ticks";

    public Pending {
      if (ticks < 0) {
        throw new IllegalArgumentException(String.format(INVALID, ticks));
      }
    }
  }

  /** A line after a tick: what still waits, the output now, and whether a change entered. */
  public record Passage(DelayLine line, boolean output, boolean entered) {}
}
