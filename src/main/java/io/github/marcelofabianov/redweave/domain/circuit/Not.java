package io.github.marcelofabianov.redweave.domain.circuit;

import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Objects;

/**
 * The NOT rule, used by the Signal Inverter (question 310). It has one input, so it is not a {@link
 * Gate}. Like a redstone torch, any input above 0 turns it off, and no input gives 15.
 */
public final class Not {
  private static final String MISSING = "The NOT rule needs its input";

  private Not() {}

  public static Signal output(Signal input) {
    Objects.requireNonNull(input, MISSING);
    return BinaryOutput.of(!input.isOn());
  }
}
