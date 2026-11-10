package io.github.marcelofabianov.redweave.server;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.Channel;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NetworkSnapshot;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.network.SlotReading;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/** A network as it is in the saves, and its way to and from the snapshot of the domain (89). */
record SavedNetwork(
    NetworkId id,
    PlayerId owner,
    String name,
    ChannelColor color,
    Optional<BlockLocation> controller,
    List<Channel> channels,
    List<SavedNode> nodes) {
  private static final String ID = "id";
  private static final String OWNER = "owner";
  private static final String CONTROLLER = "controller";
  private static final String CHANNELS = "channels";
  private static final String NODES = "nodes";
  private static final String CHANNEL_PART = "channel";
  private static final String NODE_PART = "node";
  private static final String REPEATED = "repeated in the network";
  private static final String TAKEN = "already in another network";

  static final Codec<SavedNetwork> CODEC =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  SaveCodecs.NETWORK_ID.fieldOf(ID).forGetter(SavedNetwork::id),
                  SaveCodecs.PLAYER_ID.fieldOf(OWNER).forGetter(SavedNetwork::owner),
                  SaveCodecs.SAVED_NAME.fieldOf(SaveCodecs.NAME).forGetter(SavedNetwork::name),
                  SaveCodecs.CHANNEL_COLOR.fieldOf(SaveCodecs.COLOR).forGetter(SavedNetwork::color),
                  Lenient.optionalField(CONTROLLER, SaveCodecs.BLOCK, CONTROLLER)
                      .forGetter(SavedNetwork::controller),
                  Lenient.list(SaveCodecs.CHANNEL, CHANNEL_PART)
                      .fieldOf(CHANNELS)
                      .forGetter(SavedNetwork::channels),
                  Lenient.list(SavedNode.CODEC, NODE_PART)
                      .fieldOf(NODES)
                      .forGetter(SavedNetwork::nodes))
              .apply(instance, SavedNetwork::new));

  SavedNetwork {
    channels = List.copyOf(channels);
    nodes = List.copyOf(nodes);
  }

  static SavedNetwork of(NetworkSnapshot snapshot) {
    Map<NodeId, Map<Integer, Signal>> readings = new HashMap<>();
    for (SlotReading reading : snapshot.readings()) {
      readings
          .computeIfAbsent(reading.node(), node -> new HashMap<>())
          .put(reading.index(), reading.signal());
    }
    List<SavedNode> nodes =
        snapshot.nodes().stream()
            .map(node -> SavedNode.of(node, index -> readingOf(readings, node.id(), index)))
            .toList();
    return new SavedNetwork(
        snapshot.id(),
        snapshot.owner(),
        snapshot.name(),
        snapshot.color(),
        snapshot.controller(),
        snapshot.channels(),
        nodes);
  }

  private static Signal readingOf(
      Map<NodeId, Map<Integer, Signal>> readings, NodeId node, int index) {
    return readings.getOrDefault(node, Map.of()).getOrDefault(index, Signal.OFF);
  }

  /**
   * The snapshot of the domain. A channel or a node that appears twice, or a node already taken by
   * another network, is dropped with a warning, and the first one stays (answer 92).
   */
  NetworkSnapshot snapshot(Predicate<NodeId> takenElsewhere) {
    Set<ChannelColor> colors = new HashSet<>();
    List<Channel> kept =
        channels.stream().filter(channel -> first(colors, channel.color(), CHANNEL_PART)).toList();
    Set<NodeId> ids = new HashSet<>();
    List<SavedNode> keptNodes =
        nodes.stream()
            .filter(node -> free(takenElsewhere, node.id()) && first(ids, node.id(), NODE_PART))
            .toList();
    return new NetworkSnapshot(
        id,
        owner,
        name,
        color,
        controller,
        kept,
        keptNodes.stream().map(SavedNode::node).toList(),
        keptNodes.stream().flatMap(node -> node.readings().stream()).toList());
  }

  private static boolean free(Predicate<NodeId> takenElsewhere, NodeId node) {
    if (!takenElsewhere.test(node)) {
      return true;
    }
    Lenient.dropped(NODE_PART, TAKEN);
    return false;
  }

  private static <T> boolean first(Set<T> seen, T value, String part) {
    if (seen.add(value)) {
      return true;
    }
    Lenient.dropped(part, REPEATED);
    return false;
  }
}
