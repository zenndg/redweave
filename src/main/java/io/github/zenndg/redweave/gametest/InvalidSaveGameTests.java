package io.github.zenndg.redweave.gametest;

import static io.github.zenndg.redweave.gametest.SaveTags.BLACK;
import static io.github.zenndg.redweave.gametest.SaveTags.CYAN;
import static io.github.zenndg.redweave.gametest.SaveTags.EAST;
import static io.github.zenndg.redweave.gametest.SaveTags.INPUT;
import static io.github.zenndg.redweave.gametest.SaveTags.OUTPUT;
import static io.github.zenndg.redweave.gametest.SaveTags.RED;
import static io.github.zenndg.redweave.gametest.SaveTags.WEST;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.config.NeoForgeNetworkLimits;
import io.github.zenndg.redweave.domain.network.ChannelSlot;
import io.github.zenndg.redweave.domain.network.Direction;
import io.github.zenndg.redweave.domain.network.Face;
import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.NetworkState;
import io.github.zenndg.redweave.domain.network.Node;
import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.server.SavedDataNetworks;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** An invalid save loses only its invalid parts, and an unknown one is kept aside (answer 92). */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class InvalidSaveGameTests {
  private static final String EMPTY = "empty";
  private static final String NETWORK_UUID = "6f1c2b0e-3a4d-4e5f-8a9b-0c1d2e3f4a5b";
  private static final String OTHER_NETWORK_UUID = "7a2d3c1f-4b5e-4f60-9bac-1d2e3f4a5b6c";
  private static final String BLANK_NETWORK_UUID = "8b3e4d20-5c6f-4071-acbd-2e3f4a5b6c7d";
  private static final String OWNER_UUID = "0a1b2c3d-4e5f-4a6b-8c7d-9e0f1a2b3c4d";
  private static final String NODE_UUID = "1b2c3d4e-5f6a-4b7c-8d9e-0f1a2b3c4d5e";
  private static final String OTHER_NODE_UUID = "2c3d4e5f-6a7b-4c8d-9e0f-1a2b3c4d5e6f";
  private static final UUID NETWORK = UUID.fromString(NETWORK_UUID);
  private static final UUID OTHER_NETWORK = UUID.fromString(OTHER_NETWORK_UUID);
  private static final UUID BLANK_NETWORK = UUID.fromString(BLANK_NETWORK_UUID);
  private static final UUID OWNER = UUID.fromString(OWNER_UUID);
  private static final UUID NODE = UUID.fromString(NODE_UUID);
  private static final UUID OTHER_NODE = UUID.fromString(OTHER_NODE_UUID);
  private static final String NAME = "Base door";
  private static final String BLANK = "  ";
  private static final String SIDEWAYS = "sideways";
  private static final String BACKUP = SavedDataNetworks.NAME + ".v2.backup.dat";
  private static final String UNREADABLE_MARK = ".unreadable.backup.dat";
  private static final String UNREADABLE_BACKUP = SavedDataNetworks.NAME + UNREADABLE_MARK;
  private static final String FILE = SavedDataNetworks.NAME + ".dat";
  private static final String TEMPORARY = "redweave-save";
  private static final byte[] CONTENT = {1, 2, 3};
  private static final int VERSION = 1;
  private static final int NEWER_VERSION = 2;
  private static final int NODE_X = 8;
  private static final int READ = 9;
  private static final Path NO_FILE = Path.of(SavedDataNetworks.NAME);
  private static final ChannelSlot KEPT_INPUT =
      new ChannelSlot(Direction.INPUT, Face.EAST, ChannelColor.RED);
  private static final ChannelSlot KEPT_OUTPUT =
      new ChannelSlot(Direction.OUTPUT, Face.WEST, ChannelColor.BLACK);
  private static final String WRONG_SLOTS = "Only the valid first slot of a channel should stay";
  private static final String STRAY_READING = "A reading of an output should be dropped";
  private static final String WRONG_NODES = "Only the valid nodes should stay";
  private static final String WRONG_NETWORKS = "Only the valid networks should stay";
  private static final String WRONG_LAST_USED = "A last used network should be a loaded one";
  private static final String WRONG_NODE_NAME = "Only the invalid name should leave the node";
  private static final String WRONG_OFFLINE = "An invalid offline key should leave the network on";
  private static final String NOT_BACKED_UP = "An unknown version should be kept aside";
  private static final String NOT_EMPTY = "An unknown version should start with no networks";
  private static final String NOT_KEPT_UNREADABLE = "An unreadable file should be kept aside";

  private InvalidSaveGameTests() {}

  private static SavedDataNetworks load(ListTag networks, ListTag current, Path file) {
    return SavedDataNetworks.load(
        SaveTags.save(VERSION, networks, current), new NeoForgeNetworkLimits(), file);
  }

  private static Network only(ListTag nodes) {
    ListTag networks =
        SaveTags.list(SaveTags.network(NETWORK, OWNER, NAME, SaveTags.list(), nodes));
    return load(networks, SaveTags.list(), NO_FILE).find(new NetworkId(NETWORK)).orElseThrow();
  }

  /**
   * A slot that does not decode, and one whose channel repeats on the node, are dropped; the first
   * slot of a channel stays in the place of that channel (answers 92 and 224).
   */
  @GameTest(template = EMPTY)
  public static void invalidAndRepeatedSlotsAreDropped(GameTestHelper helper) {
    CompoundTag withoutActive = SaveTags.slot(CYAN, INPUT, EAST);
    withoutActive.remove(SaveTags.ACTIVE);
    ListTag slots =
        SaveTags.list(
            SaveTags.slot(RED, INPUT, EAST),
            SaveTags.slot(SIDEWAYS, INPUT, EAST),
            SaveTags.slot(CYAN, SIDEWAYS, EAST),
            withoutActive,
            SaveTags.slot(RED, OUTPUT, EAST),
            SaveTags.reading(SaveTags.slot(BLACK, OUTPUT, WEST), READ));
    Network network = only(SaveTags.list(SaveTags.node(NODE, NODE_X, slots)));

    List<Optional<ChannelSlot>> expected =
        new ArrayList<>(Collections.nCopies(Node.SLOT_COUNT, Optional.empty()));
    expected.set(KEPT_INPUT.channel().ordinal(), Optional.of(KEPT_INPUT));
    expected.set(KEPT_OUTPUT.channel().ordinal(), Optional.of(KEPT_OUTPUT));
    helper.assertTrue(
        network.node(new NodeId(NODE)).orElseThrow().slots().equals(expected), WRONG_SLOTS);
    helper.assertTrue(network.snapshot().readings().isEmpty(), STRAY_READING);
    helper.succeed();
  }

  /** The smallest part goes: the network stays, turned on (answers 92 and 210). */
  @GameTest(template = EMPTY)
  public static void anOfflineKeyThatIsNotABooleanIsDropped(GameTestHelper helper) {
    CompoundTag network = SaveTags.network(NETWORK, OWNER, NAME, SaveTags.list(), SaveTags.list());
    network.putString(SaveTags.OFFLINE, SIDEWAYS);

    Network loaded =
        load(SaveTags.list(network), SaveTags.list(), NO_FILE)
            .find(new NetworkId(NETWORK))
            .orElseThrow();

    helper.assertTrue(loaded.state() == NetworkState.ONLINE, WRONG_OFFLINE);
    helper.succeed();
  }

  /** The smallest part goes: the node stays, with the default name (answers 92 and 206). */
  @GameTest(template = EMPTY)
  public static void aNodeNameThatDoesNotFitIsDropped(GameTestHelper helper) {
    ListTag nodes =
        SaveTags.list(SaveTags.named(SaveTags.node(NODE, NODE_X, SaveTags.list()), BLANK));

    Network network = only(nodes);

    helper.assertTrue(
        network.node(new NodeId(NODE)).orElseThrow().name().isEmpty(), WRONG_NODE_NAME);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void nodesThatDoNotFitAreDropped(GameTestHelper helper) {
    CompoundTag withoutId = SaveTags.node(OTHER_NODE, NODE_X, SaveTags.list());
    withoutId.remove(SaveTags.ID);
    CompoundTag withoutSlots = SaveTags.node(NODE, NODE_X, SaveTags.list());
    withoutSlots.remove(SaveTags.SLOTS);
    ListTag nodes =
        SaveTags.list(withoutSlots, withoutId, SaveTags.node(OTHER_NODE, NODE_X, SaveTags.list()));

    Network network = only(nodes);

    helper.assertTrue(network.nodes().size() == 1, WRONG_NODES);
    helper.assertTrue(network.node(new NodeId(OTHER_NODE)).isPresent(), WRONG_NODES);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void networksThatDoNotFitAreDropped(GameTestHelper helper) {
    ListTag channels = SaveTags.list(SaveTags.channel(RED, BLANK));
    ListTag node = SaveTags.list(SaveTags.node(NODE, NODE_X, SaveTags.list()));
    ListTag networks =
        SaveTags.list(
            SaveTags.network(NETWORK, OWNER, NAME, channels, node),
            SaveTags.network(BLANK_NETWORK, OWNER, BLANK, SaveTags.list(), SaveTags.list()),
            SaveTags.network(OTHER_NETWORK, OWNER, NAME, SaveTags.list(), node.copy()),
            SaveTags.network(NETWORK, OWNER, NAME, SaveTags.list(), SaveTags.list()));
    ListTag current = SaveTags.list(SaveTags.lastUsed(OWNER, BLANK_NETWORK));

    SavedDataNetworks data = load(networks, current, NO_FILE);

    Network first = data.find(new NetworkId(NETWORK)).orElseThrow();
    helper.assertTrue(data.all().size() == 2, WRONG_NETWORKS);
    helper.assertTrue(first.channel(ChannelColor.RED).name().isEmpty(), WRONG_NETWORKS);
    helper.assertTrue(first.node(new NodeId(NODE)).isPresent(), WRONG_NETWORKS);
    helper.assertTrue(
        data.find(new NetworkId(OTHER_NETWORK)).orElseThrow().nodes().isEmpty(), WRONG_NETWORKS);
    helper.assertTrue(data.lastUsedOf(new PlayerId(OWNER)).isEmpty(), WRONG_LAST_USED);
    helper.succeed();
  }

  /** A save from a newer version goes aside, so going back to it finds the networks (92). */
  @GameTest(template = EMPTY)
  public static void anUnknownVersionIsKeptAside(GameTestHelper helper) throws IOException {
    Path folder = Files.createTempDirectory(TEMPORARY);
    Path file = Files.write(folder.resolve(FILE), CONTENT);
    ListTag networks =
        SaveTags.list(SaveTags.network(NETWORK, OWNER, NAME, SaveTags.list(), SaveTags.list()));
    CompoundTag newer = SaveTags.save(NEWER_VERSION, networks, SaveTags.list());

    SavedDataNetworks data = SavedDataNetworks.load(newer, new NeoForgeNetworkLimits(), file);

    Path backup = folder.resolve(BACKUP);
    helper.assertTrue(Arrays.equals(CONTENT, Files.readAllBytes(backup)), NOT_BACKED_UP);
    helper.assertTrue(data.all().isEmpty(), NOT_EMPTY);
    Files.delete(backup);
    Files.delete(file);
    Files.delete(folder);
    helper.succeed();
  }

  /** A file the game cannot even read is kept aside before it is written over (answer 97). */
  @GameTest(template = EMPTY)
  public static void anUnreadableFileIsKeptAside(GameTestHelper helper) throws IOException {
    Path folder = Files.createTempDirectory(TEMPORARY);
    Path file = Files.write(folder.resolve(FILE), CONTENT);
    MinecraftServer server = helper.getLevel().getServer();
    DimensionDataStorage storage =
        new DimensionDataStorage(folder.toFile(), server.getFixerUpper(), server.registryAccess());

    SavedDataNetworks data = SavedDataNetworks.in(storage, file, new NeoForgeNetworkLimits());

    Path backup = folder.resolve(UNREADABLE_BACKUP);
    helper.assertTrue(Arrays.equals(CONTENT, Files.readAllBytes(backup)), NOT_KEPT_UNREADABLE);
    helper.assertTrue(data.all().isEmpty(), NOT_EMPTY);
    Files.delete(backup);
    Files.delete(file);
    Files.delete(folder);
    helper.succeed();
  }
}
