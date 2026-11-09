package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Objects;
import java.util.Optional;

/**
 * One of the 16 channels of a network. Without a name of its own, the screens show the translated
 * name of the color (answer 26).
 */
public record Channel(ChannelColor color, Optional<String> name) {
  private static final String MISSING = "A channel needs a color and a name, even if empty";

  public Channel {
    Objects.requireNonNull(color, MISSING);
    Objects.requireNonNull(name, MISSING);
  }

  static Channel unnamed(ChannelColor color) {
    return new Channel(color, Optional.empty());
  }
}
