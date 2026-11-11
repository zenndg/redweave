package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Objects;
import java.util.Optional;

/**
 * What a node does with the channel of one of its 16 slots: whether it is enabled, the direction,
 * the face of the block, the channel and the name the owner gave it (answers 224, 227 and 228). A
 * disabled slot keeps its configuration and neither reads nor emits. The logic of Phase 2 arrives
 * as one more field, and until then an output repeats the channel.
 */
public record ChannelSlot(
    Direction direction, Face face, ChannelColor channel, boolean active, Optional<String> name) {
  private static final String MISSING = "A slot needs a direction, a face, a channel and a name";

  public ChannelSlot {
    Objects.requireNonNull(direction, MISSING);
    Objects.requireNonNull(face, MISSING);
    Objects.requireNonNull(channel, MISSING);
    Objects.requireNonNull(name, MISSING);
  }

  /** An enabled slot with no name. */
  public ChannelSlot(Direction direction, Face face, ChannelColor channel) {
    this(direction, face, channel, true, Optional.empty());
  }

  /** Whether the slot reads its block into the channel: enabled and an input. */
  public boolean reads() {
    return active && direction == Direction.INPUT;
  }

  /** Whether the slot emits the channel into its block: enabled and an output. */
  public boolean emits() {
    return active && direction == Direction.OUTPUT;
  }

  public ChannelSlot named(Optional<String> newName) {
    return new ChannelSlot(direction, face, channel, active, newName);
  }

  public ChannelSlot enabled(boolean enabled) {
    return new ChannelSlot(direction, face, channel, enabled, name);
  }
}
