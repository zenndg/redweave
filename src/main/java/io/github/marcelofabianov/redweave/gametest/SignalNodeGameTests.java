package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.SignalNodeBlock;
import io.github.marcelofabianov.redweave.block.SignalNodeBlockEntity;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** The Signal Node in the world: _docs/funcionalidades/no-de-sinal.md (answer 77). */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SignalNodeGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos LEVER_BLOCK = new BlockPos(0, 1, 0);
  private static final BlockPos LEVER = LEVER_BLOCK.above();
  private static final BlockPos INPUT_NODE = LEVER_BLOCK.east();
  private static final BlockPos LAMP = new BlockPos(2, 1, 2);
  private static final BlockPos OUTPUT_NODE = LAMP.above();
  private static final BlockPos LAMP_BESIDE_THE_STONE = LAMP.north();
  private static final BlockPos LAMP_BESIDE_THE_NODE = OUTPUT_NODE.north();
  private static final ChannelColor LEVER_CHANNEL = ChannelColor.WHITE;
  private static final ChannelColor ECHO_CHANNEL = ChannelColor.ORANGE;
  private static final int LEVER_SLOT = LEVER_CHANNEL.ordinal();
  private static final int ECHO_SLOT = ECHO_CHANNEL.ordinal();
  private static final int SETTLE_TICKS = 10;
  private static final String NO_NODE = "The node should keep its ID";
  private static final String STILL_IN = "The node should have left its network";
  private static final String STRANGER_BREAKS = "Only the owner should break the node";
  private static final String NOT_IN = "The node should stay in its network";
  private static final String HAS_NETWORK = "A node with no network to pick has none";
  private static final String NOT_ON = "The channel should follow the lever";
  private static final String ECHO = "An input should not read the output of its own node";

  private SignalNodeGameTests() {}

  /** Lever, node, network, node and lamp: the first circuit of the mod. */
  @GameTest(template = EMPTY)
  public static void aLeverLightsALampThroughTheNetwork(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    leverAndLamp(helper, owner);

    helper
        .startSequence()
        .thenExecute(() -> helper.pullLever(LEVER))
        .thenWaitUntil(() -> helper.assertBlockProperty(LAMP, RedstoneLampBlock.LIT, true))
        .thenExecute(() -> helper.pullLever(LEVER))
        .thenWaitUntil(() -> helper.assertBlockProperty(LAMP, RedstoneLampBlock.LIT, false))
        .thenSucceed();
  }

  /**
   * An output of a node feeds its block, and an input of that node on the same face must not read
   * it back: one node reads the lever into a channel, and the other emits that channel into its
   * stone while one of its inputs reads the stone.
   */
  @GameTest(template = EMPTY)
  public static void aNodeDoesNotReadItsOwnOutput(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    placeController(helper, owner);
    placeLever(helper);
    NodeId reader = placeNode(helper, owner, LEVER_BLOCK, net.minecraft.core.Direction.EAST);
    NodeScene.enableSlot(owner, reader, LEVER_SLOT);
    helper.setBlock(LAMP, Blocks.STONE);
    NodeId node = placeNode(helper, owner, LAMP, net.minecraft.core.Direction.UP);
    Network network = networkOf(node);
    PlayerId ownerId = new PlayerId(owner.getUUID());
    network.configureSlot(
        ownerId, node, LEVER_SLOT, new ChannelSlot(Direction.OUTPUT, Face.UP, LEVER_CHANNEL));
    network.configureSlot(
        ownerId, node, ECHO_SLOT, new ChannelSlot(Direction.INPUT, Face.UP, ECHO_CHANNEL));

    helper
        .startSequence()
        .thenExecute(() -> helper.pullLever(LEVER))
        .thenWaitUntil(() -> helper.assertTrue(network.channelValue(LEVER_CHANNEL).isOn(), NOT_ON))
        .thenWaitUntil(() -> helper.assertBlockProperty(OUTPUT_NODE, SignalNodeBlock.POWERED, true))
        .thenIdle(SETTLE_TICKS)
        .thenExecute(() -> helper.assertFalse(network.channelValue(ECHO_CHANNEL).isOn(), ECHO))
        .thenSucceed();
  }

  /**
   * The node feeds its block with strength, so the block lights a lamp next to it, which the node
   * does not touch (answers 71 and 262).
   */
  @GameTest(template = EMPTY)
  public static void aNodeFeedsItsBlock(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    leverAndLamp(helper, owner);
    helper.setBlock(LAMP, Blocks.STONE);
    helper.setBlock(LAMP_BESIDE_THE_STONE, Blocks.REDSTONE_LAMP);

    helper
        .startSequence()
        .thenExecute(() -> helper.pullLever(LEVER))
        .thenWaitUntil(
            () -> helper.assertBlockProperty(LAMP_BESIDE_THE_STONE, RedstoneLampBlock.LIT, true))
        .thenSucceed();
  }

  /**
   * Unlike a lever, the node gives no weak signal to the neighbors of its own space: a lamp next to
   * the node that does not touch its block stays off, as two lamps with a node between them (answer
   * 262).
   */
  @GameTest(template = EMPTY)
  public static void aNodePowersOnlyItsBlock(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    leverAndLamp(helper, owner);
    helper.setBlock(LAMP, Blocks.STONE);
    helper.setBlock(LAMP_BESIDE_THE_STONE, Blocks.REDSTONE_LAMP);
    helper.setBlock(LAMP_BESIDE_THE_NODE, Blocks.REDSTONE_LAMP);

    helper
        .startSequence()
        .thenExecute(() -> helper.pullLever(LEVER))
        .thenWaitUntil(
            () -> helper.assertBlockProperty(LAMP_BESIDE_THE_STONE, RedstoneLampBlock.LIT, true))
        .thenIdle(SETTLE_TICKS)
        .thenExecute(
            () -> helper.assertBlockProperty(LAMP_BESIDE_THE_NODE, RedstoneLampBlock.LIT, false))
        .thenSucceed();
  }

  @GameTest(template = EMPTY)
  public static void aNodeWithNoNetworkToPickStaysInTheWorld(GameTestHelper helper) {
    Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);
    helper.setBlock(LEVER_BLOCK, Blocks.STONE);

    NodeId node = placeNode(helper, stranger, LEVER_BLOCK, net.minecraft.core.Direction.EAST);

    helper.assertBlockPresent(RedweaveBlocks.SIGNAL_NODE.get(), INPUT_NODE);
    helper.assertTrue(server().networkOfNode(node).isEmpty(), HAS_NETWORK);
    helper.succeed();
  }

  /** In survival, only the owner of its network breaks a node (answer 74). */
  @GameTest(template = EMPTY)
  public static void onlyTheOwnerBreaksANode(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);
    placeController(helper, owner);
    helper.setBlock(LEVER_BLOCK, Blocks.STONE);
    NodeId node = placeNode(helper, owner, LEVER_BLOCK, net.minecraft.core.Direction.EAST);

    helper.assertFalse(GameTestActions.breakBy(helper, stranger, INPUT_NODE), STRANGER_BREAKS);
    helper.assertTrue(server().networkOfNode(node).isPresent(), NOT_IN);
    helper.assertTrue(GameTestActions.breakBy(helper, owner, INPUT_NODE), STRANGER_BREAKS);
    helper.assertTrue(server().networkOfNode(node).isEmpty(), STILL_IN);
    helper.succeed();
  }

  /** The block under the node is gone, so the node falls and leaves its network (answer 74). */
  @GameTest(template = EMPTY)
  public static void aNodeWhoseBlockIsGoneLeavesItsNetwork(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    placeController(helper, owner);
    helper.setBlock(LEVER_BLOCK, Blocks.STONE);
    NodeId node = placeNode(helper, owner, LEVER_BLOCK, net.minecraft.core.Direction.EAST);

    helper.destroyBlock(LEVER_BLOCK);

    helper.assertBlockNotPresent(RedweaveBlocks.SIGNAL_NODE.get(), INPUT_NODE);
    helper.assertTrue(server().networkOfNode(node).isEmpty(), STILL_IN);
    helper.succeed();
  }

  /** The input node reads a lever on a stone, and the output node sits on top of a lamp. */
  private static void leverAndLamp(GameTestHelper helper, Player owner) {
    placeController(helper, owner);
    placeLever(helper);
    NodeId input = placeNode(helper, owner, LEVER_BLOCK, net.minecraft.core.Direction.EAST);
    NodeScene.enableSlot(owner, input, LEVER_SLOT);
    helper.setBlock(LAMP, Blocks.REDSTONE_LAMP);
    NodeId output = placeNode(helper, owner, LAMP, net.minecraft.core.Direction.UP);
    networkOf(input)
        .configureSlot(
            new PlayerId(owner.getUUID()),
            output,
            LEVER_SLOT,
            new ChannelSlot(Direction.OUTPUT, Face.UP, LEVER_CHANNEL));
  }

  private static void placeController(GameTestHelper helper, Player owner) {
    GameTestActions.placeOn(
        helper,
        owner,
        new ItemStack(RedweaveItems.NETWORK_CONTROLLER.get()),
        CONTROLLER_FLOOR,
        net.minecraft.core.Direction.UP);
  }

  /** A stone with a lever on top, off: the lever powers the stone when it is pulled. */
  private static void placeLever(GameTestHelper helper) {
    helper.setBlock(LEVER_BLOCK, Blocks.STONE);
    BlockState lever = Blocks.LEVER.defaultBlockState().setValue(LeverBlock.FACE, AttachFace.FLOOR);
    helper.setBlock(LEVER, lever);
  }

  /** Places the node and picks the network, as its placer does on the Node Screen (139). */
  private static NodeId placeNode(
      GameTestHelper helper, Player player, BlockPos block, net.minecraft.core.Direction face) {
    GameTestActions.placeOn(
        helper, player, new ItemStack(RedweaveItems.SIGNAL_NODE.get()), block, face);
    SignalNodeBlockEntity node = helper.getBlockEntity(block.relative(face));
    Optional<NodeId> id = node.node();
    helper.assertTrue(id.isPresent(), NO_NODE);
    NodeScene.choose(helper, player, block.relative(face), NodeScene.FIRST_CHOICE);
    return id.orElseThrow();
  }

  private static Network networkOf(NodeId node) {
    return server().networkOfNode(node).orElseThrow();
  }

  private static RedweaveServer server() {
    return RedweaveServer.running().orElseThrow();
  }
}
