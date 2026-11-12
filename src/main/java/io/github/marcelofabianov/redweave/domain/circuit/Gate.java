package io.github.marcelofabianov.redweave.domain.circuit;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Objects;

/**
 * A two-input logic gate. Each input is on above 0 (question 321), and the output is 15 or 0
 * (question 322). The declaration order is the Shift + scroll order (question 326).
 */
public enum Gate {
  /** On only when both inputs are on. */
  AND {
    @Override
    boolean rule(boolean a, boolean b) {
      return a && b;
    }
  },
  /** On when any input is on. */
  OR {
    @Override
    boolean rule(boolean a, boolean b) {
      return a || b;
    }
  },
  /** On when exactly one input is on. */
  XOR {
    @Override
    boolean rule(boolean a, boolean b) {
      return a != b;
    }
  },
  /** Off only when both inputs are on. */
  NAND {
    @Override
    boolean rule(boolean a, boolean b) {
      return !(a && b);
    }
  };

  private static final String MISSING = "A gate needs both inputs";

  public Signal output(Signal a, Signal b) {
    Objects.requireNonNull(a, MISSING);
    Objects.requireNonNull(b, MISSING);
    return BinaryOutput.of(rule(a.isOn(), b.isOn()));
  }

  abstract boolean rule(boolean a, boolean b);
}
