package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.registry.RedweaveDataComponents;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
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
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    ItemStack configurator = holdConfigurator(owner);

    NodeScene.click(helper, owner, CONTROLLER);

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
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    ItemStack configurator = holdConfigurator(stranger);

    NodeScene.click(helper, stranger, CONTROLLER);

    helper.assertFalse(configurator.has(RedweaveDataComponents.NETWORK.get()), STORED);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void aClickLinksANodeToTheStoredNetwork(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId stored = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeScene.placeController(helper, owner, OTHER_CONTROLLER_FLOOR, OTHER_CONTROLLER);
    NodeId node = NodeScene.placeNode(helper, owner, SOURCE_STONE, Direction.EAST);
    holdConfigurator(owner);

    NodeScene.click(helper, owner, CONTROLLER);
    NodeScene.click(helper, owner, SOURCE_NODE);

    helper.assertTrue(NodeScene.networkOf(node).id().equals(stored), NOT_LINKED);
    helper.succeed();
  }

  /** A slot on the face the source sticks to goes to the face of the target (answer 105). */
  @GameTest(template = EMPTY)
  public static void aPasteFollowsTheFaceTheTargetSticksTo(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeId source = configuredSource(helper, owner);
    NodeId target = NodeScene.placeNode(helper, owner, TARGET_STONE, Direction.NORTH);
    ItemStack configurator = holdConfigurator(owner);

    NodeScene.shiftClick(helper, owner, SOURCE_NODE);
    NodeScene.click(helper, owner, TARGET_NODE);

    helper.assertTrue(configurator.has(RedweaveDataComponents.NODE_SLOTS.get()), NOT_COPIED);
    List<Optional<ChannelSlot>> slots =
        NodeScene.networkOf(target).node(target).orElseThrow().slots();
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
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    configuredSource(helper, owner);
    NodeId target = NodeScene.placeNode(helper, owner, TARGET_STONE, Direction.NORTH);
    List<Optional<ChannelSlot>> before =
        NodeScene.networkOf(target).node(target).orElseThrow().slots();
    holdConfigurator(stranger);

    NodeScene.shiftClick(helper, stranger, SOURCE_NODE);
    NodeScene.click(helper, stranger, TARGET_NODE);

    helper.assertTrue(
        before.equals(NodeScene.networkOf(target).node(target).orElseThrow().slots()), PASTED);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void shiftAndClickInTheAirClearsTheCopy(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    configuredSource(helper, owner);
    ItemStack configurator = holdConfigurator(owner);
    NodeScene.shiftClick(helper, owner, SOURCE_NODE);

    owner.setShiftKeyDown(true);
    configurator.use(helper.getLevel(), owner, InteractionHand.MAIN_HAND);

    helper.assertFalse(configurator.has(RedweaveDataComponents.NODE_SLOTS.get()), NOT_CLEARED);
    helper.succeed();
  }

  /** A node with an input on the face it sticks to and an output on another face. */
  private static NodeId configuredSource(GameTestHelper helper, Player owner) {
    NodeId node = NodeScene.placeNode(helper, owner, SOURCE_STONE, Direction.EAST);
    NodeScene.networkOf(node)
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
    return NodeScene.slotsOf(node).get(index);
  }

  private static ItemStack holdConfigurator(Player player) {
    ItemStack configurator = new ItemStack(RedweaveItems.CONFIGURATOR.get());
    player.setItemInHand(InteractionHand.MAIN_HAND, configurator);
    return configurator;
  }
}
