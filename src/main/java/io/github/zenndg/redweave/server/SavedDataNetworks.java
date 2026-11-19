package io.github.zenndg.redweave.server;

import io.github.zenndg.redweave.application.AllNetworksFinder;
import io.github.zenndg.redweave.application.LastUsedNetworkFinder;
import io.github.zenndg.redweave.application.LastUsedNetworkRecorder;
import io.github.zenndg.redweave.application.NetworkFinder;
import io.github.zenndg.redweave.application.NetworkRemover;
import io.github.zenndg.redweave.application.NetworkSaver;
import io.github.zenndg.redweave.application.NodeNetworkFinder;
import io.github.zenndg.redweave.application.OwnerNetworkFinder;
import io.github.zenndg.redweave.application.TerminalFinder;
import io.github.zenndg.redweave.application.TerminalForgetter;
import io.github.zenndg.redweave.application.TerminalRecorder;
import io.github.zenndg.redweave.domain.network.BlockLocation;
import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.NetworkLimits;
import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

/**
 * The networks and the network each player last put a node in, saved in the world, in the data of
 * the Overworld, and not in the blocks (answers 24, 88 and 91). A saved change only marks the data
 * as changed, and the game writes it at the autosave and when the world closes.
 */
public final class SavedDataNetworks extends SavedData
    implements AllNetworksFinder,
        NetworkFinder,
        NetworkSaver,
        NodeNetworkFinder,
        OwnerNetworkFinder,
        LastUsedNetworkFinder,
        LastUsedNetworkRecorder,
        NetworkRemover,
        TerminalFinder,
        TerminalRecorder,
        TerminalForgetter {
  public static final String NAME = "redweave_networks";

  private final Map<NetworkId, Network> networks;
  private final Map<PlayerId, NetworkId> lastUsed;
  private final Map<PlayerId, BlockLocation> terminals;

  private SavedDataNetworks(NetworkSave.Contents contents) {
    networks = contents.networks();
    lastUsed = contents.lastUsed();
    terminals = contents.terminals();
  }

  /** The data of the save of this server, read from its file once and kept by the game. */
  public static SavedDataNetworks of(MinecraftServer server, NetworkLimits limits) {
    return in(server.overworld().getDataStorage(), SaveBackup.fileOf(server, NAME), limits);
  }

  /** The data kept by one storage of the game, whose file is the given one. */
  public static SavedDataNetworks in(
      DimensionDataStorage storage, Path file, NetworkLimits limits) {
    return storage.computeIfAbsent(factory(limits, file), NAME);
  }

  /**
   * How the game creates the data in a new world and reads it from the file in an old one. The game
   * creates it also when it fails to read the file, so a file that is there is kept aside first,
   * because the empty data would be written over it (answer 97).
   */
  private static SavedData.Factory<SavedDataNetworks> factory(NetworkLimits limits, Path file) {
    return new SavedData.Factory<>(
        () -> {
          SaveBackup.copyIfUnreadable(file, NAME);
          return new SavedDataNetworks(NetworkSave.Contents.empty());
        },
        (tag, registries) -> load(tag, limits, file));
  }

  /**
   * Reads the data from the tag of its file. A save the mod cannot read is copied aside from the
   * file before the mod starts with no networks (answer 92).
   */
  public static SavedDataNetworks load(CompoundTag tag, NetworkLimits limits, Path file) {
    return new SavedDataNetworks(
        NetworkSave.read(tag, limits, version -> SaveBackup.copy(file, NAME, version)));
  }

  @Override
  public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
    return new NetworkSave.Contents(networks, lastUsed, terminals).writeInto(tag);
  }

  @Override
  public List<Network> all() {
    return List.copyOf(networks.values());
  }

  @Override
  public Optional<Network> find(NetworkId id) {
    return Optional.ofNullable(networks.get(id));
  }

  @Override
  public void save(Network network) {
    networks.put(network.id(), network);
    setDirty();
  }

  @Override
  public Optional<Network> findByNode(NodeId node) {
    return networks.values().stream().filter(network -> network.node(node).isPresent()).findFirst();
  }

  @Override
  public List<Network> ownedBy(PlayerId owner) {
    return networks.values().stream().filter(network -> network.isOwner(owner)).toList();
  }

  @Override
  public Optional<NetworkId> lastUsedOf(PlayerId player) {
    return Optional.ofNullable(lastUsed.get(player));
  }

  @Override
  public void record(PlayerId player, NetworkId network) {
    lastUsed.put(player, network);
    setDirty();
  }

  /** A deleted network leaves the saves, and the players who last used it lose it (166). */
  @Override
  public void remove(NetworkId id) {
    networks.remove(id);
    lastUsed.values().removeIf(id::equals);
    setDirty();
  }

  @Override
  public Optional<BlockLocation> terminalOf(PlayerId player) {
    return Optional.ofNullable(terminals.get(player));
  }

  @Override
  public void record(PlayerId player, BlockLocation terminal) {
    terminals.put(player, terminal);
    setDirty();
  }

  @Override
  public void forget(PlayerId player) {
    terminals.remove(player);
    setDirty();
  }
}
