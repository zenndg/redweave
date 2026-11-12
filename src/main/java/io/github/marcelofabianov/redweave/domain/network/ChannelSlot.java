package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Objects;

/**
 * What a node does with the channel of one of its 16 slots: whether it is enabled, the direction,
 * the face of the block and the channel (answers 224 and 228). A disabled slot keeps its
 * configuration and neither reads nor emits. A slot has no name of its own: the channel of the
 * network and the node already name it (answer 277). The logic of Phase 2 arrives as one more
 * field, and until then an output repeats the channel.
 */
public record ChannelSlot(Direction direction, Face face, ChannelColor channel, boolean active) {
  private static final String MISSING = "A slot needs a direction, a face and a channel";

  public ChannelSlot {
    Objects.requireNonNull(direction, MISSING);
    Objects.requireNonNull(face, MISSING);
    Objects.requireNonNull(channel, MISSING);
  }

  /** An enabled slot. */
  public ChannelSlot(Direction direction, Face face, ChannelColor channel) {
    this(direction, face, channel, true);
  }

  /** Whether the slot reads its block into the channel: enabled and an input. */
  public boolean reads() {
    return active && direction == Direction.INPUT;
  }

  /** Whether the slot emits the channel into its block: enabled and an output. */
  public boolean emits() {
    return active && direction == Direction.OUTPUT;
  }

  public ChannelSlot enabled(boolean enabled) {
    return new ChannelSlot(direction, face, channel, enabled);
  }
}
