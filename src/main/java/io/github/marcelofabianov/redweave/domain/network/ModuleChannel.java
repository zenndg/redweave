package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Objects;

/** A channel a registered Signal Module reads or writes, for its row in the list (answer 810). */
public record ModuleChannel(ChannelColor channel, Direction direction) {
  private static final String MISSING = "A module channel needs its color and its direction";

  public ModuleChannel {
    Objects.requireNonNull(channel, MISSING);
    Objects.requireNonNull(direction, MISSING);
  }
}
