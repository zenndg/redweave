package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Objects;

/** A slot in use under an open row of the node list: its index, channel and direction (179). */
public record SlotRow(int index, ChannelColor channel, Direction direction) {
  private static final String MISSING = "A slot row needs a channel and a direction";

  public SlotRow {
    Objects.requireNonNull(channel, MISSING);
    Objects.requireNonNull(direction, MISSING);
  }
}
