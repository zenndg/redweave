package io.github.marcelofabianov.redweave.domain.module;

import io.github.marcelofabianov.redweave.domain.signal.OutputDelay;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Objects;

/**
 * What the owner sets on a Signal Inverter: the strength it gives while on, from 1 to 15, and the
 * delay of its output (answers 830 and 834). The Configurator copies both (answer 841).
 */
public record InverterSettings(Signal strength, OutputDelay delay) {
  /** A new inverter: 15, as a torch, and no delay (answers 834 and 844). */
  public static final InverterSettings DEFAULT = new InverterSettings(Signal.MAX, OutputDelay.NONE);

  public InverterSettings {
    Objects.requireNonNull(strength);
    Objects.requireNonNull(delay);
  }
}
