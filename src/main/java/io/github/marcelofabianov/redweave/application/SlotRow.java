package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Objects;
import java.util.Optional;

/**
 * An enabled slot under an open row of the node list: its index, channel, direction and the name
 * the owner gave it (answers 179 and 234).
 */
public record SlotRow(int index, ChannelColor channel, Direction direction, Optional<String> name) {
  private static final String MISSING = "A slot row needs a channel, a direction and a name";

  public SlotRow {
    Objects.requireNonNull(channel, MISSING);
    Objects.requireNonNull(direction, MISSING);
    Objects.requireNonNull(name, MISSING);
  }
}
