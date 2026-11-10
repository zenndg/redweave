package io.github.marcelofabianov.redweave.server;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NetworkLimits;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntConsumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import org.slf4j.Logger;

/**
 * The root of the save of the networks: the version, the networks and the current network of each
 * player (answers 88, 89 and 91). Reading never throws, because the game would then start an empty
 * save and write it over the file (answer 92 and rule 14).
 */
final class NetworkSave {
  static final int VERSION = 1;
  private static final Logger LOGGER = LogUtils.getLogger();
  private static final String VERSION_KEY = "version";
  private static final String NETWORKS = "networks";
  private static final String CURRENT = "current";
  private static final String NETWORK_PART = "network";
  private static final String CURRENT_PART = "current network";
  private static final String REPEATED = "repeated in the save";
  private static final String UNKNOWN_NETWORK = "points to a network that is not in the save";
  private static final String SAVE_VERSION = "The Redweave save has version {},";
  private static final String MOD_VERSION = " and this version of the mod reads {}.";
  private static final String STARTS_EMPTY = " It starts empty.";
  private static final String UNKNOWN_VERSION = SAVE_VERSION + MOD_VERSION + STARTS_EMPTY;
  private static final String UNREADABLE = "The Redweave save could not be read. It starts empty.";

  private static final Codec<Root> ROOT =
      RecordCodecBuilder.create(instance ->
          instance
              .group(
                  Lenient.list(SavedNetwork.CODEC, NETWORK_PART)
                      .optionalFieldOf(NETWORKS, List.of())
                      .forGetter(Root::networks),
                  Lenient.list(CurrentChoice.CODEC, CURRENT_PART)
                      .optionalFieldOf(CURRENT, List.of())
                      .forGetter(Root::current))
              .apply(instance, Root::new));

  private NetworkSave() {}

  /** What the save holds, in the order it was written. */
  record Contents(Map<NetworkId, Network> networks, Map<PlayerId, NetworkId> current) {
    static Contents empty() {
      return new Contents(new LinkedHashMap<>(), new LinkedHashMap<>());
    }
  }

  private record Root(List<SavedNetwork> networks, List<CurrentChoice> current) {}

  static CompoundTag write(
      CompoundTag tag, Collection<Network> networks, Map<PlayerId, NetworkId> current) {
    List<SavedNetwork> saved =
        networks.stream().map(network -> SavedNetwork.of(network.snapshot())).toList();
    List<CurrentChoice> choices =
        current.entrySet().stream()
            .map(entry -> new CurrentChoice(entry.getKey(), entry.getValue()))
            .toList();
    tag.merge(
        (CompoundTag) ROOT.encodeStart(NbtOps.INSTANCE, new Root(saved, choices)).getOrThrow());
    tag.putInt(VERSION_KEY, VERSION);
    return tag;
  }

  /**
   * Reads the save. An unknown version, or a save that cannot be read at all, is kept aside by the
   * backup and the mod starts with no networks, so nothing is lost (answer 92).
   */
  static Contents read(CompoundTag tag, NetworkLimits limits, IntConsumer backup) {
    int version = tag.getInt(VERSION_KEY);
    if (version != VERSION) {
      LOGGER.error(UNKNOWN_VERSION, version, VERSION);
      backup.accept(version);
      return Contents.empty();
    }
    Optional<Root> root = ROOT.parse(NbtOps.INSTANCE, tag).resultOrPartial(LOGGER::error);
    if (root.isEmpty()) {
      LOGGER.error(UNREADABLE);
      backup.accept(version);
      return Contents.empty();
    }
    Map<NetworkId, Network> networks = restore(root.get().networks(), limits);
    return new Contents(networks, current(root.get().current(), networks));
  }

  private static Map<NetworkId, Network> restore(List<SavedNetwork> saved, NetworkLimits limits) {
    Map<NetworkId, Network> networks = new LinkedHashMap<>();
    Set<NodeId> taken = new HashSet<>();
    for (SavedNetwork network : saved) {
      if (networks.containsKey(network.id())) {
        Lenient.dropped(NETWORK_PART, REPEATED);
        continue;
      }
      restore(network, limits, taken)
          .ifPresent(restored -> {
            networks.put(restored.id(), restored);
            restored.nodes().forEach(node -> taken.add(node.id()));
          });
    }
    return networks;
  }

  /** The domain refusing a snapshot is a hole in the checks above, so the network is dropped. */
  private static Optional<Network> restore(
      SavedNetwork network, NetworkLimits limits, Set<NodeId> taken) {
    try {
      return Optional.of(Network.restore(network.snapshot(taken::contains), limits));
    } catch (IllegalArgumentException refused) {
      Lenient.dropped(NETWORK_PART, refused.getMessage());
      return Optional.empty();
    }
  }

  private static Map<PlayerId, NetworkId> current(
      List<CurrentChoice> choices, Map<NetworkId, Network> networks) {
    Map<PlayerId, NetworkId> current = new LinkedHashMap<>();
    for (CurrentChoice choice : choices) {
      if (!networks.containsKey(choice.network())) {
        Lenient.dropped(CURRENT_PART, UNKNOWN_NETWORK);
      } else if (current.putIfAbsent(choice.player(), choice.network()) != null) {
        Lenient.dropped(CURRENT_PART, REPEATED);
      }
    }
    return current;
  }
}
