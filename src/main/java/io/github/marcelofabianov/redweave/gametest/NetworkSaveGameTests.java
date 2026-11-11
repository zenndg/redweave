package io.github.marcelofabianov.redweave.gametest;

import static io.github.marcelofabianov.redweave.gametest.SaveTags.BLACK;
import static io.github.marcelofabianov.redweave.gametest.SaveTags.CYAN;
import static io.github.marcelofabianov.redweave.gametest.SaveTags.EAST;
import static io.github.marcelofabianov.redweave.gametest.SaveTags.INPUT;
import static io.github.marcelofabianov.redweave.gametest.SaveTags.OUTPUT;
import static io.github.marcelofabianov.redweave.gametest.SaveTags.RED;
import static io.github.marcelofabianov.redweave.gametest.SaveTags.WEST;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.NetworkControllerBlockEntity;
import io.github.marcelofabianov.redweave.config.NeoForgeNetworkLimits;
import io.github.marcelofabianov.redweave.config.RedweaveConfig;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NetworkState;
import io.github.marcelofabianov.redweave.domain.network.Node;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import io.github.marcelofabianov.redweave.server.SavedDataNetworks;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** The networks in the save of the world: _docs/funcionalidades/rede-e-canais.md (answer 94). */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NetworkSaveGameTests {
  private static final String EMPTY = "empty";
  private static final String NETWORK_UUID = "6f1c2b0e-3a4d-4e5f-8a9b-0c1d2e3f4a5b";
  private static final String OWNER_UUID = "0a1b2c3d-4e5f-4a6b-8c7d-9e0f1a2b3c4d";
  private static final String NODE_UUID = "1b2c3d4e-5f6a-4b7c-8d9e-0f1a2b3c4d5e";
  private static final String IDLE_NODE_UUID = "2c3d4e5f-6a7b-4c8d-9e0f-1a2b3c4d5e6f";
  private static final UUID NETWORK = UUID.fromString(NETWORK_UUID);
  private static final UUID OWNER = UUID.fromString(OWNER_UUID);
  private static final UUID NODE = UUID.fromString(NODE_UUID);
  private static final UUID IDLE_NODE = UUID.fromString(IDLE_NODE_UUID);
  private static final String NAME = "Base door";
  private static final String CHANNEL_NAME = "Door";
  private static final String NODE_NAME = "Lever";
  private static final String LETTER = "x";
  private static final int VERSION = 1;
  private static final String SLOT_NAME = "Door lever";
  private static final int RED_SLOT = ChannelColor.RED.ordinal();
  private static final int LONG_NAME_LENGTH = RedweaveConfig.MAX_NAME;
  private static final int NODE_X = 8;
  private static final int IDLE_NODE_X = -8;
  private static final int READ = 12;
  private static final BlockPos FLOOR = new BlockPos(1, 0, 1);
  private static final Path NO_FILE = Path.of(SavedDataNetworks.NAME);
  private static final String NOT_SAME = "The save should come back as it was written";
  private static final String NOT_KEPT = "The network should keep its reading, names, last used";
  private static final String NOT_ALLOWED = "The save should keep what the config no longer allows";
  private static final String NOT_DIRTY = "A saved change should mark the save as changed";
  private static final String NOT_WRITTEN = "The change should be in what the save writes";
  private static final String NOT_OFF = "A network turned off should come back off, the same";

  private NetworkSaveGameTests() {}

  private static CompoundTag fullSave() {
    ListTag slots =
        SaveTags.list(
            SaveTags.disabled(SaveTags.slot(CYAN, OUTPUT, EAST)),
            SaveTags.named(SaveTags.reading(SaveTags.slot(RED, INPUT, EAST), READ), SLOT_NAME),
            SaveTags.slot(BLACK, OUTPUT, WEST));
    ListTag nodes =
        SaveTags.list(
            SaveTags.named(SaveTags.node(NODE, NODE_X, slots), NODE_NAME),
            SaveTags.node(IDLE_NODE, IDLE_NODE_X, SaveTags.list()));
    ListTag channels = SaveTags.list(SaveTags.channel(RED, CHANNEL_NAME));
    return SaveTags.save(
        VERSION,
        SaveTags.list(SaveTags.network(NETWORK, OWNER, NAME, channels, nodes)),
        SaveTags.list(SaveTags.lastUsed(OWNER, NETWORK)));
  }

  private static SavedDataNetworks load(CompoundTag tag) {
    return SavedDataNetworks.load(tag, new NeoForgeNetworkLimits(), NO_FILE);
  }

  @GameTest(template = EMPTY)
  public static void aSaveComesBackAsItWasWritten(GameTestHelper helper) {
    CompoundTag written =
        load(fullSave()).save(new CompoundTag(), helper.getLevel().registryAccess());

    helper.assertTrue(fullSave().equals(written), NOT_SAME);
    helper.succeed();
  }

  /** The offline key goes to the saves only for a network turned off (answer 210). */
  @GameTest(template = EMPTY)
  public static void aNetworkTurnedOffComesBackOff(GameTestHelper helper) {
    CompoundTag saved =
        SaveTags.save(
            VERSION,
            SaveTags.list(
                SaveTags.turnedOff(
                    SaveTags.network(NETWORK, OWNER, NAME, SaveTags.list(), SaveTags.list()))),
            SaveTags.list(SaveTags.lastUsed(OWNER, NETWORK)));

    SavedDataNetworks data = load(saved);
    CompoundTag written = data.save(new CompoundTag(), helper.getLevel().registryAccess());

    Network network = data.find(new NetworkId(NETWORK)).orElseThrow();
    helper.assertTrue(network.state() == NetworkState.OFFLINE, NOT_OFF);
    helper.assertTrue(saved.equals(written), NOT_OFF);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void aLoadedNetworkKeepsItsReadingNamesAndLastUsed(GameTestHelper helper) {
    SavedDataNetworks data = load(fullSave());
    Network network = data.find(new NetworkId(NETWORK)).orElseThrow();

    helper.assertTrue(network.channelValue(ChannelColor.RED).equals(new Signal(READ)), NOT_KEPT);
    helper.assertTrue(network.name().equals(NAME), NOT_KEPT);
    helper.assertTrue(
        network.channel(ChannelColor.RED).name().equals(Optional.of(CHANNEL_NAME)), NOT_KEPT);
    Node node = network.node(new NodeId(NODE)).orElseThrow();
    helper.assertTrue(node.displayName().equals(NODE_NAME), NOT_KEPT);
    ChannelSlot named = node.slots().get(ChannelColor.RED.ordinal()).orElseThrow();
    helper.assertTrue(named.name().equals(Optional.of(SLOT_NAME)), NOT_KEPT);
    helper.assertFalse(
        node.slots().get(ChannelColor.CYAN.ordinal()).orElseThrow().active(), NOT_KEPT);
    helper.assertTrue(network.isPending(), NOT_KEPT);
    helper.assertTrue(
        data.lastUsedOf(new PlayerId(OWNER)).equals(Optional.of(network.id())), NOT_KEPT);
    helper.succeed();
  }

  /** The limits of the config only hold for the next changes (answer 93). */
  @GameTest(template = EMPTY)
  public static void aNetworkKeepsWhatTheConfigNoLongerAllows(GameTestHelper helper) {
    String longName = LETTER.repeat(LONG_NAME_LENGTH);
    ListTag slots = SaveTags.list(SaveTags.named(SaveTags.slot(RED, INPUT, EAST), longName));
    ListTag nodes = SaveTags.list(SaveTags.node(NODE, NODE_X, slots));
    CompoundTag tag =
        SaveTags.save(
            VERSION,
            SaveTags.list(SaveTags.network(NETWORK, OWNER, longName, SaveTags.list(), nodes)),
            SaveTags.list());

    Network network = load(tag).find(new NetworkId(NETWORK)).orElseThrow();

    helper.assertTrue(network.name().equals(longName), NOT_ALLOWED);
    ChannelSlot slot =
        network.node(new NodeId(NODE)).orElseThrow().slots().get(RED_SLOT).orElseThrow();
    helper.assertTrue(slot.name().equals(Optional.of(longName)), NOT_ALLOWED);
    helper.succeed();
  }

  /**
   * The running server keeps its networks in the save of the Overworld, and every saved change
   * marks it to be written (answer 88). Losing the controller only goes through the saver.
   */
  @GameTest(template = EMPTY)
  public static void aChangedNetworkGoesToTheSaveOfTheWorld(GameTestHelper helper) {
    SavedDataNetworks data =
        SavedDataNetworks.of(helper.getLevel().getServer(), new NeoForgeNetworkLimits());
    NetworkId network = placeController(helper);
    data.setDirty(false);

    RedweaveServer.running().orElseThrow().loseController().lose(network);

    helper.assertTrue(data.isDirty(), NOT_DIRTY);
    CompoundTag written = data.save(new CompoundTag(), helper.getLevel().registryAccess());
    helper.assertTrue(!load(written).find(network).orElseThrow().isOnline(), NOT_WRITTEN);
    helper.succeed();
  }

  private static NetworkId placeController(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    ItemStack controller = new ItemStack(RedweaveItems.NETWORK_CONTROLLER.get());
    GameTestActions.placeOn(helper, owner, controller, FLOOR, Direction.UP);
    NetworkControllerBlockEntity block = helper.getBlockEntity(FLOOR.above());
    return block.network().orElseThrow();
  }
}
