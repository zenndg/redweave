package io.github.zenndg.redweave.domain.network;

import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.Objects;

/**
 * A loaded Signal Module that writes into a channel this tick: where its plate is, for the range,
 * the channel and the signal, on or off (answers 375 and 387).
 */
public record ModuleInput(
    ModuleId module, BlockLocation location, ChannelColor channel, Signal signal) {
  private static final String MISSING = "A module input needs all its parts";

  public ModuleInput {
    Objects.requireNonNull(module, MISSING);
    Objects.requireNonNull(location, MISSING);
    Objects.requireNonNull(channel, MISSING);
    Objects.requireNonNull(signal, MISSING);
  }
}
