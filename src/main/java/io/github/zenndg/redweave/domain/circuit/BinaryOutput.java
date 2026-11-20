package io.github.zenndg.redweave.domain.circuit;

import io.github.zenndg.redweave.domain.signal.Signal;

/** What a binary rule emits: 15 when it is on and 0 when it is off, like a torch (question 322). */
final class BinaryOutput {
  private BinaryOutput() {}

  static Signal of(boolean on) {
    return on ? Signal.MAX : Signal.OFF;
  }
}
