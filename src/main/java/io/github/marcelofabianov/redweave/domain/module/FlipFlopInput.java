package io.github.marcelofabianov.redweave.domain.module;

import io.github.marcelofabianov.redweave.domain.signal.Signal;

/**
 * The last reading of the input of a Signal Flip-Flop, to see only a rising edge (answer 879).
 * `UNREAD` until its first reading, when placed or loaded with no `input_on`, so an input already
 * on is never taken for an edge (answers 881 and 893).
 */
public enum FlipFlopInput {
  UNREAD,
  OFF,
  ON;

  /** A reading from the saves, where `input_on` exists only once the input was read (893). */
  public static FlipFlopInput read(boolean on) {
    return on ? ON : OFF;
  }

  /** The reading of a signal: on above 0, as every connection (answer 322). */
  static FlipFlopInput of(Signal signal) {
    return read(signal.isOn());
  }

  /** Whether a signal is a rising edge after this reading; never before the first one. */
  boolean risesTo(FlipFlopInput next) {
    return this == OFF && next == ON;
  }
}
