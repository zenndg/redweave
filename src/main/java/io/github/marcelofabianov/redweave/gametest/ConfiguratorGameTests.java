package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.NetworkControllerBlockEntity;
import io.github.marcelofabianov.redweave.block.SignalNodeBlockEntity;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.registry.RedweaveDataComponents;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** The Configurator in the world: _docs/funcionalidades/configurador.md (answer 109). */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ConfiguratorGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos OTHER_CONTROLLER_FLOOR = new BlockPos(2, 0, 0);
  private static final BlockPos OTHER_CONTROLLER = OTHER_CONTROLLER_FLOOR.above();
  private static final BlockPos SOURCE_STONE = new BlockPos(0, 1, 0);
  private static final BlockPos SOURCE_NODE = SOURCE_STONE.east();
  private static final BlockPos TARGET_STONE = new BlockPos(2, 1, 2);
  private static final BlockPos TARGET_NODE = TARGET_STONE.north();
  private static final int FIRST_SLOT = 0;
  private static final int SECOND_SLOT = 1;
  private static final String NOT_STORED = "The owner should store the network";
  private static final String STORED = "Another player should not store the network";
  private static final String NOT_LINKED = "The node should move to the stored network";
  private static final String NOT_COPIED = "Shift + click should copy the node";
  private static final String NOT_PASTED = "The copy should follow the face of the target";
  private static final String PASTED = "Another player should not paste";
  private static final String NOT_CLEARED = "Shift + click in the air should clear the copy";

  private ConfiguratorGameTests() {}

  @GameTest(template = EMPTY)
  public static void theOwnerStoresTheNetworkOfAController(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId network = placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    ItemStack configurator = holdConfigurator(owner);

    click(helper, owner, CONTROLLER);

    helper.assertTrue(
        network.equals(configurator.get(RedweaveDataComponents.NETWORK.get())), NOT_STORED);
    helper.assertTrue(configurator.has(RedweaveDataComponents.NETWORK_NAME.get()), NOT_STORED);
    helper.succeed();
  }

  /** Only the owner could link nodes to the network, so only they store it (answer 104). */
  @GameTest(template = EMPTY)
  public static void anotherPlayerDoesNotStoreTheNetwork(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);
    placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    ItemStack configurator = holdConfigurator(stranger);

    click(helper, stranger, CONTROLLER);

    helper.assertFalse(configurator.has(RedweaveDataComponents.NETWORK.get()), STORED);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void aClickLinksANodeToTheStoredNetwork(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId stored = placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    placeController(helper, owner, OTHER_CONTROLLER_FLOOR, OTHER_CONTROLLER);
    NodeId node = placeNode(helper, owner, SOURCE_STONE, Direction.EAST);
    holdConfigurator(owner);

    click(helper, owner, CONTROLLER);
    click(helper, owner, SOURCE_NODE);

    helper.assertTrue(networkOf(node).id().equals(stored), NOT_LINKED);
    helper.succeed();
  }

  /** A slot on the face the source sticks to goes to the face of the target (answer 105). */
  @GameTest(template = EMPTY)
  public static void aPasteFollowsTheFaceTheTargetSticksTo(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeId source = configuredSource(helper, owner);
    NodeId target = placeNode(helper, owner, TARGET_STONE, Direction.NORTH);
    ItemStack configurator = holdConfigurator(owner);

    copy(helper, owner, SOURCE_NODE);
    click(helper, owner, TARGET_NODE);

    helper.assertTrue(configurator.has(RedweaveDataComponents.NODE_SLOTS.get()), NOT_COPIED);
    List<Optional<ChannelSlot>> slots = networkOf(target).node(target).orElseThrow().slots();
    Optional<ChannelSlot> sourceInput = slotOf(source, FIRST_SLOT);
    helper.assertTrue(slots.get(FIRST_SLOT).orElseThrow().face() == Face.NORTH, NOT_PASTED);
    helper.assertTrue(
        slots
            .get(FIRST_SLOT)
            .map(ChannelSlot::channel)
            .equals(sourceInput.map(ChannelSlot::channel)),
        NOT_PASTED);
    helper.assertTrue(slots.get(SECOND_SLOT).equals(slotOf(source, SECOND_SLOT)), NOT_PASTED);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void anotherPlayerDoesNotPaste(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);
    placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    configuredSource(helper, owner);
    NodeId target = placeNode(helper, owner, TARGET_STONE, Direction.NORTH);
    List<Optional<ChannelSlot>> before = networkOf(target).node(target).orElseThrow().slots();
    holdConfigurator(stranger);

    copy(helper, stranger, SOURCE_NODE);
    click(helper, stranger, TARGET_NODE);

    helper.assertTrue(before.equals(networkOf(target).node(target).orElseThrow().slots()), PASTED);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void shiftAndClickInTheAirClearsTheCopy(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    configuredSource(helper, owner);
    ItemStack configurator = holdConfigurator(owner);
    copy(helper, owner, SOURCE_NODE);

    owner.setShiftKeyDown(true);
    configurator.use(helper.getLevel(), owner, InteractionHand.MAIN_HAND);

    helper.assertFalse(configurator.has(RedweaveDataComponents.NODE_SLOTS.get()), NOT_CLEARED);
    helper.succeed();
  }

  /** A node with an input on the face it sticks to and an output on another face. */
  private static NodeId configuredSource(GameTestHelper helper, Player owner) {
    NodeId node = placeNode(helper, owner, SOURCE_STONE, Direction.EAST);
    networkOf(node)
        .configureSlot(
            new PlayerId(owner.getUUID()),
            node,
            SECOND_SLOT,
            new ChannelSlot(
                io.github.marcelofabianov.redweave.domain.network.Direction.OUTPUT,
                Face.UP,
                ChannelColor.RED));
    return node;
  }

  private static Optional<ChannelSlot> slotOf(NodeId node, int index) {
    return networkOf(node).node(node).orElseThrow().slots().get(index);
  }

  private static ItemStack holdConfigurator(Player player) {
    ItemStack configurator = new ItemStack(RedweaveItems.CONFIGURATOR.get());
    player.setItemInHand(InteractionHand.MAIN_HAND, configurator);
    return configurator;
  }

  private static void copy(GameTestHelper helper, Player player, BlockPos relative) {
    player.setShiftKeyDown(true);
    click(helper, player, relative);
    player.setShiftKeyDown(false);
  }

  /** What the game does when the player right-clicks the block with the item in the hand. */
  private static void click(GameTestHelper helper, Player player, BlockPos relative) {
    BlockPos pos = helper.absolutePos(relative);
    BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
    player
        .getItemInHand(InteractionHand.MAIN_HAND)
        .useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
  }

  private static NetworkId placeController(
      GameTestHelper helper, Player owner, BlockPos floor, BlockPos controller) {
    ItemStack item = new ItemStack(RedweaveItems.NETWORK_CONTROLLER.get());
    GameTestActions.placeOn(helper, owner, item, floor, Direction.UP);
    NetworkControllerBlockEntity block = helper.getBlockEntity(controller);
    return block.network().orElseThrow();
  }

  private static NodeId placeNode(
      GameTestHelper helper, Player owner, BlockPos stone, Direction face) {
    helper.setBlock(stone, Blocks.STONE);
    ItemStack item = new ItemStack(RedweaveItems.SIGNAL_NODE.get());
    GameTestActions.placeOn(helper, owner, item, stone, face);
    SignalNodeBlockEntity node = helper.getBlockEntity(stone.relative(face));
    return node.node().orElseThrow();
  }

  private static Network networkOf(NodeId node) {
    return RedweaveServer.running().orElseThrow().networkOfNode(node).orElseThrow();
  }
}
