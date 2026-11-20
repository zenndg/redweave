package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.part.NodePart;
import io.github.zenndg.redweave.domain.network.ChannelSlot;
import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.Node;
import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.NodePosition;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * A node is born with no network, and only whoever placed it picks one on the Node Screen; its
 * owner switches it later, and the node keeps its name (answers 139, 141, 157, 171, 172 and 200).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NetworkChoiceGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos OTHER_CONTROLLER_FLOOR = new BlockPos(2, 0, 2);
  private static final BlockPos OTHER_CONTROLLER = OTHER_CONTROLLER_FLOOR.above();
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos NODE = STONE.above();
  private static final int SECOND_CHOICE = 1;
  private static final String NODE_NAME = "Gate lever";
  private static final String HAS_NETWORK = "A placed node should have no network";
  private static final String NOT_JOINED = "The placer should put the node in the network";
  private static final String ENABLED = "A node should join with no enabled slot";
  private static final String NOT_LAST_USED = "The picked network should be the last used one";
  private static final String NOT_PLACER = "Only whoever placed the node should pick its network";
  private static final String NOT_SWITCHED = "The owner should move the node to the other network";
  private static final String NAME_LOST = "The node should keep its name in the new network";

  private NetworkChoiceGameTests() {}

  @GameTest(template = EMPTY)
  public static void thePlacerPicksTheNetworkOfANewNode(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeId node = NodeScene.placeLooseNode(helper, owner, STONE, net.minecraft.core.Direction.UP);
    helper.assertTrue(server().networkOfNode(node).isEmpty(), HAS_NETWORK);

    NodeScene.choose(helper, owner, NODE, NodeScene.FIRST_CHOICE);

    helper.assertTrue(networkIdOf(node).equals(Optional.of(network)), NOT_JOINED);
    Optional<NetworkId> lastUsed = server().lastUsedOf(new PlayerId(owner.getUUID()));
    helper.assertTrue(lastUsed.equals(Optional.of(network)), NOT_LAST_USED);
    helper.succeed();
  }

  /** A node joins with its 16 slots empty: no channel is enabled for it (answer 224). */
  @GameTest(template = EMPTY)
  public static void aNodeJoinsWithNoEnabledSlot(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);

    NodeId node = NodeScene.placeNode(helper, owner, STONE, net.minecraft.core.Direction.UP);

    List<Optional<ChannelSlot>> slots = NodeScene.slotsOf(node);
    helper.assertTrue(slots.size() == Node.SLOT_COUNT, ENABLED);
    helper.assertTrue(slots.stream().allMatch(Optional::isEmpty), ENABLED);
    helper.assertTrue(
        NodeScene.networkOf(node).node(node).orElseThrow().enabledChannels().isEmpty(), ENABLED);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void onlyThePlacerPicksTheNetworkOfANewNode(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    Player placer = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeId node = NodeScene.placeLooseNode(helper, placer, STONE, net.minecraft.core.Direction.UP);

    NodeScene.choose(helper, owner, NODE, NodeScene.FIRST_CHOICE);

    helper.assertTrue(server().networkOfNode(node).isEmpty(), NOT_PLACER);
    helper.succeed();
  }

  /** The last used network comes first, so the other one is the second row (answer 203). */
  @GameTest(template = EMPTY)
  public static void theOwnerSwitchesTheNodeAndItKeepsItsName(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId first = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NetworkId other =
        NodeScene.placeController(helper, owner, OTHER_CONTROLLER_FLOOR, OTHER_CONTROLLER);
    NodeId node = NodeScene.placeLooseNode(helper, owner, STONE, net.minecraft.core.Direction.UP);
    server().linkNode().link(new PlayerId(owner.getUUID()), node, positionOf(helper), first);
    server().renameNode().rename(new PlayerId(owner.getUUID()), node, NODE_NAME);

    NodeScene.choose(helper, owner, NODE, SECOND_CHOICE);

    helper.assertTrue(networkIdOf(node).equals(Optional.of(other)), NOT_SWITCHED);
    String name = NodeScene.networkOf(node).node(node).orElseThrow().displayName();
    helper.assertTrue(name.equals(NODE_NAME), NAME_LOST);
    helper.succeed();
  }

  private static NodePosition positionOf(GameTestHelper helper) {
    return NodePart.positionOf(helper.getLevel(), NodeParts.spotOf(helper, NODE));
  }

  private static Optional<NetworkId> networkIdOf(NodeId node) {
    return server().networkOfNode(node).map(Network::id);
  }

  private static RedweaveServer server() {
    return RedweaveServer.running().orElseThrow();
  }
}
