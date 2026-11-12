package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * One line of the log of the Controller Screen: an input or an output that changed value. The names
 * are the ones of the moment, so renaming later leaves the line as it was (answers 147 and 245). A
 * channel without a name of its own shows the name of its color.
 */
public record SignalChange(
    LocalDateTime time,
    String node,
    ChannelColor channel,
    Optional<String> channelName,
    Direction direction,
    Signal value) {
  private static final String MISSING = "A signal change needs every field";

  public SignalChange {
    Objects.requireNonNull(time, MISSING);
    Objects.requireNonNull(node, MISSING);
    Objects.requireNonNull(channel, MISSING);
    Objects.requireNonNull(channelName, MISSING);
    Objects.requireNonNull(direction, MISSING);
    Objects.requireNonNull(value, MISSING);
  }
}
