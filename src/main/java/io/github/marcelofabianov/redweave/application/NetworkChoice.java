package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Objects;

/** One row of the network choice: the color, the name and the node count (answer 202). */
public record NetworkChoice(
    NetworkId id, String name, ChannelColor color, int nodes, boolean lastUsed) {
  private static final String MISSING = "A network choice needs the ID, the name and the color";

  public NetworkChoice {
    Objects.requireNonNull(id, MISSING);
    Objects.requireNonNull(name, MISSING);
    Objects.requireNonNull(color, MISSING);
  }
}
