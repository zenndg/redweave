package io.github.marcelofabianov.redweave.domain.network;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The rules a snapshot keeps to become a network. The border drops what is invalid in the saves
 * before the domain (answer 92), so a snapshot that breaks them is a programming error. The limits
 * of the config are not checked, because they only hold for new changes (answer 93).
 */
final class SnapshotRules {
  private static final String INVALID_NAME = "A saved name is never blank nor padded";
  private static final String REPEATED_CHANNEL = "A saved channel appears only once";
  private static final String REPEATED_NODE = "A saved node appears only once";
  private static final String STRAY_READING = "A saved reading belongs to an enabled input";

  private SnapshotRules() {}

  static void check(NetworkSnapshot snapshot) {
    requireName(snapshot.name());
    checkChannels(snapshot.channels());
    Map<NodeId, Node> nodes = nodesById(snapshot.nodes());
    snapshot.readings().forEach(reading -> requireInput(nodes.get(reading.node()), reading));
  }

  private static void requireName(String name) {
    if (!Names.isKept(name)) {
      throw new IllegalArgumentException(INVALID_NAME);
    }
  }

  private static void checkChannels(List<Channel> channels) {
    Set<ChannelColor> seen = new HashSet<>();
    for (Channel channel : channels) {
      if (!seen.add(channel.color())) {
        throw new IllegalArgumentException(REPEATED_CHANNEL);
      }
      channel.name().ifPresent(SnapshotRules::requireName);
    }
  }

  private static Map<NodeId, Node> nodesById(List<Node> nodes) {
    Map<NodeId, Node> byId = new HashMap<>();
    for (Node node : nodes) {
      if (byId.put(node.id(), node) != null) {
        throw new IllegalArgumentException(REPEATED_NODE);
      }
      node.name().ifPresent(SnapshotRules::requireName);
      node.slots().stream()
          .flatMap(Optional::stream)
          .forEach(slot -> slot.name().ifPresent(SnapshotRules::requireName));
    }
    return byId;
  }

  private static void requireInput(Node node, SlotReading reading) {
    boolean input =
        node != null
            && node.hasSlot(reading.index())
            && node.slots().get(reading.index()).filter(ChannelSlot::reads).isPresent();
    if (!input) {
      throw new IllegalArgumentException(STRAY_READING);
    }
  }
}
