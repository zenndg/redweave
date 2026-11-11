package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The 16 channels of a network, one per color, from its creation on. It left the network for the
 * aggregate to keep one responsibility per part.
 */
final class Channels {
  private final Map<ChannelColor, Channel> byColor = new EnumMap<>(ChannelColor.class);

  Channels() {
    for (ChannelColor color : ChannelColor.values()) {
      byColor.put(color, Channel.unnamed(color));
    }
  }

  /**
   * Names a channel. A blank name goes back to the name of the color, because the name of a channel
   * is optional (answers 26 and 34).
   */
  Outcome rename(ChannelColor color, String newName, int maxLength) {
    if (newName.isBlank()) {
      byColor.put(color, Channel.unnamed(color));
      return Outcome.ACCEPTED;
    }
    Optional<String> valid = Names.valid(newName, maxLength);
    if (valid.isEmpty()) {
      return Outcome.INVALID_NAME;
    }
    byColor.put(color, new Channel(color, valid));
    return Outcome.ACCEPTED;
  }

  /** Puts back a channel from the saves, already checked by the restore. */
  void restore(Channel channel) {
    byColor.put(channel.color(), channel);
  }

  /** The 16 channels, in the order of the colors. */
  List<Channel> all() {
    return List.copyOf(byColor.values());
  }

  /** Only the channels with a name, the ones that go to the saves (answer 89). */
  List<Channel> named() {
    return byColor.values().stream().filter(channel -> channel.name().isPresent()).toList();
  }

  Channel of(ChannelColor color) {
    return byColor.get(color);
  }

  /** Every channel off, the start of the strongest value of each one. */
  Map<ChannelColor, Signal> allOff() {
    Map<ChannelColor, Signal> values = new EnumMap<>(ChannelColor.class);
    byColor.keySet().forEach(color -> values.put(color, Signal.OFF));
    return values;
  }
}
