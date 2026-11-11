package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Everything of a network that goes to the saves, and nothing of what it last emitted, so a
 * restored network starts pending (answers 38 and 90). Only the channels with a name are kept, and
 * offline tells whether its owner turned it off (answer 210).
 */
public record NetworkSnapshot(
    NetworkId id,
    PlayerId owner,
    String name,
    ChannelColor color,
    Optional<BlockLocation> controller,
    boolean offline,
    List<Channel> channels,
    List<Node> nodes,
    List<SlotReading> readings) {
  private static final String MISSING = "A snapshot needs every part of the network";

  public NetworkSnapshot {
    Objects.requireNonNull(id, MISSING);
    Objects.requireNonNull(owner, MISSING);
    Objects.requireNonNull(name, MISSING);
    Objects.requireNonNull(color, MISSING);
    Objects.requireNonNull(controller, MISSING);
    channels = List.copyOf(channels);
    nodes = List.copyOf(nodes);
    readings = List.copyOf(readings);
  }
}
