package io.github.zenndg.redweave.domain.network;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * The last changes of one network, for the log of the Controller Screen. A circular buffer with the
 * size of the config, only in memory: it starts empty when the server starts (answers 147 and 242).
 * Each line has a growing sequence, so an open screen receives only the new lines.
 */
public final class SignalLog {
  private static final String TOO_SMALL = "A signal log holds at least one line";
  private static final long NONE = 0;

  private final int capacity;
  private final Deque<Line> lines = new ArrayDeque<>();
  private long lastSequence = NONE;
  private long clears = NONE;

  /** A line of the log and its place in the order of the changes. */
  public record Line(long sequence, SignalChange change) {}

  public SignalLog(int capacity) {
    if (capacity < 1) {
      throw new IllegalArgumentException(TOO_SMALL);
    }
    this.capacity = capacity;
  }

  /** Keeps a change on top, and the oldest line goes when the log is full. */
  public void record(SignalChange change) {
    lastSequence++;
    lines.addFirst(new Line(lastSequence, change));
    if (lines.size() > capacity) {
      lines.removeLast();
    }
  }

  /**
   * The owner clears the log (answer 264): every line goes, and the sequence goes on, so a screen
   * that already had lines receives none of them again. The count tells the open screens.
   */
  public void clear() {
    lines.clear();
    clears++;
  }

  /** How many times the log was cleared, so an open screen knows to start over. */
  public long clears() {
    return clears;
  }

  public List<Line> newestFirst() {
    return List.copyOf(lines);
  }

  /** The lines newer than a sequence, newest first, for a screen that already has the rest. */
  public List<Line> after(long sequence) {
    return lines.stream().filter(line -> line.sequence() > sequence).toList();
  }

  /** The sequence of the newest line, or 0 before the first one. */
  public long lastSequence() {
    return lastSequence;
  }
}
