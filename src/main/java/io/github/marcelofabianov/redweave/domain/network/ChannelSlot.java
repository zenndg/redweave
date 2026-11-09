package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Objects;

/**
 * What a node does with one channel: the direction, the face of the block and the channel. The
 * logic of Phase 2 arrives as one more field, and until then an output repeats the channel.
 */
public record ChannelSlot(Direction direction, Face face, ChannelColor channel) {
  private static final String MISSING = "A slot needs a direction, a face and a channel";

  public ChannelSlot {
    Objects.requireNonNull(direction, MISSING);
    Objects.requireNonNull(face, MISSING);
    Objects.requireNonNull(channel, MISSING);
  }
}
