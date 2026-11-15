package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.ControllerLink;
import io.github.marcelofabianov.redweave.block.NetworkControllerBlock;
import io.github.marcelofabianov.redweave.config.NeoForgeNetworkLimits;
import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NetworkState;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.item.ConfiguratorMode;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.registry.RedweaveDataComponents;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * A network turned off takes no action from the players but the Wrench, keeps its state while its
 * controller is away, and a player stops at network.max_per_player networks (answers 132, 145, 160,
 * 163, 177 and 178).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NetworkStateGameTests {
  private static final String EMPTY = "empty";
  private static final String OVERWORLD = "minecraft:overworld";
  private static final int FAR_Y = 64;
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos STONE = new BlockPos(0, 1, 0);
  private static final BlockPos NODE = STONE.east();
  private static final String COPIED = "A node of a network turned off should not be copied";
  private static final String NOT_PICKED = "The Wrench should still pick the node up";
  private static final String CORE_ON = "A network turned off should come back with its core off";
  private static final String NOT_OFFLINE = "A network turned off should come back off";
  private static final String NEW_NETWORK = "At the limit, no network should be born";

  private NetworkStateGameTests() {}

  @GameTest(template = EMPTY)
  public static void aNetworkTurnedOffIsNotCopiedButTheWrenchStillWorks(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeId node = NodeScene.placeNode(helper, owner, STONE, Direction.EAST);
    networkOf(network).terminal().turnOff(new PlayerId(owner.getUUID()));

    ItemStack configurator = ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.COPY);
    NodeScene.click(helper, owner, NODE);
    helper.assertFalse(configurator.has(RedweaveDataComponents.NODE_SLOTS.get()), COPIED);

    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.WRENCH);
    NodeScene.click(helper, owner, NODE);
    NodeParts.assertAbsent(helper, NODE);
    helper.assertTrue(server().networkOfNode(node).isEmpty(), NOT_PICKED);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void aNetworkTurnedOffComesBackOffWithItsCoreOff(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    networkOf(network).terminal().turnOff(new PlayerId(owner.getUUID()));
    server().loseController().lose(network);
    helper.setBlock(CONTROLLER, Blocks.AIR);

    ItemStack item = new ItemStack(RedweaveItems.NETWORK_CONTROLLER.get());
    item.set(RedweaveDataComponents.NETWORK.get(), network);
    GameTestActions.placeOn(helper, owner, item, CONTROLLER_FLOOR, Direction.UP);

    helper.assertTrue(networkOf(network).state() == NetworkState.OFFLINE, NOT_OFFLINE);
    helper.assertBlockProperty(CONTROLLER, NetworkControllerBlock.LINKED, false);
    helper.succeed();
  }

  /** After the world opens, the core of a network turned off is off (answer 177). */
  @GameTest(template = EMPTY)
  public static void aLoadedControllerShowsItsNetworkTurnedOff(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    networkOf(network).terminal().turnOff(new PlayerId(owner.getUUID()));

    ControllerLink.refreshCore(server(), helper.getLevel(), helper.absolutePos(CONTROLLER));

    helper.assertBlockProperty(CONTROLLER, NetworkControllerBlock.LINKED, false);
    helper.succeed();
  }

  /** Every network of the player counts, wherever it is (answer 145). */
  @GameTest(template = EMPTY)
  public static void atTheLimitANewControllerIsNotPlaced(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    PlayerId player = new PlayerId(owner.getUUID());
    int limit = new NeoForgeNetworkLimits().networksPerPlayer();
    for (int network = 0; network < limit; network++) {
      BlockLocation far = new BlockLocation(OVERWORLD, network, FAR_Y, 0);
      server().placeController().place(player, Optional.empty(), far);
    }

    GameTestActions.placeOn(
        helper,
        owner,
        new ItemStack(RedweaveItems.NETWORK_CONTROLLER.get()),
        CONTROLLER_FLOOR,
        Direction.UP);

    helper.assertBlockNotPresent(RedweaveBlocks.NETWORK_CONTROLLER.get(), CONTROLLER);
    helper.assertTrue(server().networkChoices().of(player).size() == limit, NEW_NETWORK);
    helper.succeed();
  }

  private static Network networkOf(NetworkId network) {
    return server().network(network).orElseThrow();
  }

  private static RedweaveServer server() {
    return RedweaveServer.running().orElseThrow();
  }
}
