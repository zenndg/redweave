package io.github.marcelofabianov.redweave.domain.network;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** The log of the Controller Screen keeps the last changes of a network (answers 147 and 242). */
class SignalLogTest {
  private static final int CAPACITY = 2;
  private static final int NO_LINES = 0;
  private static final LocalDateTime NOON = LocalDateTime.of(2026, 11, 11, 12, 0, 0);
  private static final String LEVER = "Lever";
  private static final String DOOR = "Iron Door";
  private static final String PISTON = "Piston";

  private static SignalChange change(String node) {
    return new SignalChange(
        NOON, node, ChannelColor.RED, Optional.empty(), Direction.INPUT, Signal.MAX);
  }

  private static List<String> nodes(List<SignalLog.Line> lines) {
    return lines.stream().map(line -> line.change().node()).toList();
  }

  @Test
  void theNewestLineComesFirst() {
    SignalLog log = new SignalLog(CAPACITY);

    log.record(change(LEVER));
    log.record(change(DOOR));

    assertEquals(List.of(DOOR, LEVER), nodes(log.newestFirst()));
  }

  /** A circular buffer: past the size of the config, the oldest line goes (answer 147). */
  @Test
  void aFullLogDropsTheOldestLine() {
    SignalLog log = new SignalLog(CAPACITY);

    log.record(change(LEVER));
    log.record(change(DOOR));
    log.record(change(PISTON));

    assertEquals(List.of(PISTON, DOOR), nodes(log.newestFirst()));
  }

  /** Only the new lines go to the open screen, so the screen asks for those after the last one. */
  @Test
  void theLinesAfterASequenceAreOnlyTheNewOnes() {
    SignalLog log = new SignalLog(CAPACITY);
    log.record(change(LEVER));
    long seen = log.lastSequence();

    log.record(change(DOOR));

    assertEquals(List.of(DOOR), nodes(log.after(seen)));
  }

  @Test
  void anEmptyLogHasNoLines() {
    SignalLog log = new SignalLog(CAPACITY);

    assertEquals(NO_LINES, log.newestFirst().size());
    assertEquals(NO_LINES, log.after(log.lastSequence()).size());
  }

  /** The owner clears the log; the sequence goes on, so no old line comes back (answer 264). */
  @Test
  void clearingTakesEveryLineAndCountsTheClearing() {
    SignalLog log = new SignalLog(CAPACITY);
    log.record(change(LEVER));
    long clearsBefore = log.clears();

    log.clear();
    log.record(change(DOOR));

    assertEquals(List.of(DOOR), nodes(log.newestFirst()));
    assertEquals(clearsBefore + 1, log.clears());
  }

  @Test
  void aLogHoldsAtLeastOneLine() {
    assertThrows(IllegalArgumentException.class, () -> new SignalLog(NO_LINES));
  }
}
