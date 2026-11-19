package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.Objects;
import java.util.Optional;

/**
 * One channel of the Signals tab: the color, its own name, if any, the current value and how many
 * input and output slots use it (rede-e-canais.md, tabs of the Controller Screen).
 */
public record ChannelRow(
    ChannelColor color, Optional<String> name, Signal value, int inputs, int outputs) {
  private static final String MISSING = "A channel row needs a color, a name and a value";

  public ChannelRow {
    Objects.requireNonNull(color, MISSING);
    Objects.requireNonNull(name, MISSING);
    Objects.requireNonNull(value, MISSING);
  }

  /** A channel with a slot of some node; the others are dimmer on the screen. */
  public boolean inUse() {
    return inputs + outputs > 0;
  }
}
