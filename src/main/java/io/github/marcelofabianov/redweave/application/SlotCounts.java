package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Node;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** How many input and output slots use each channel, as in "2 in · 1 out" of the Signals tab. */
final class SlotCounts {
  private final Map<Direction, int[]> byDirection = new EnumMap<>(Direction.class);

  private SlotCounts() {
    for (Direction direction : Direction.values()) {
      byDirection.put(direction, new int[ChannelColor.values().length]);
    }
  }

  static SlotCounts of(List<Node> nodes) {
    SlotCounts counts = new SlotCounts();
    for (Node node : nodes) {
      node.slots().stream().flatMap(Optional::stream).forEach(counts::add);
    }
    return counts;
  }

  private void add(ChannelSlot slot) {
    byDirection.get(slot.direction())[slot.channel().ordinal()]++;
  }

  int of(ChannelColor channel, Direction direction) {
    return byDirection.get(direction)[channel.ordinal()];
  }
}
