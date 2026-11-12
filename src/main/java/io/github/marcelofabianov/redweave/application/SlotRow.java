package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Objects;

/**
 * An enabled slot under an open row of the node list: its index, channel and direction (answers 179
 * and 234). It has no name of its own (answer 277).
 */
public record SlotRow(int index, ChannelColor channel, Direction direction) {
  private static final String MISSING = "A slot row needs a channel and a direction";

  public SlotRow {
    Objects.requireNonNull(channel, MISSING);
    Objects.requireNonNull(direction, MISSING);
  }
}
