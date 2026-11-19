package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.domain.network.ChannelSlot;
import io.github.zenndg.redweave.domain.network.Face;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.item.ConfiguratorItem;
import io.github.zenndg.redweave.item.ConfiguratorMode;
import io.github.zenndg.redweave.registry.RedweaveDataComponents;
import io.github.zenndg.redweave.registry.RedweaveItems;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** The modes of the Configurator in the world: configurador.md (answers 143 and 191). */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ConfiguratorGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos SOURCE_STONE = new BlockPos(0, 1, 0);
  private static final BlockPos SOURCE_NODE = SOURCE_STONE.east();
  private static final BlockPos TARGET_STONE = new BlockPos(2, 1, 2);
  private static final BlockPos TARGET_NODE = TARGET_STONE.north();
  private static final int FIRST_SLOT = ChannelColor.WHITE.ordinal();
  private static final ChannelColor SECOND_CHANNEL = ChannelColor.ORANGE;
  private static final int SECOND_SLOT = SECOND_CHANNEL.ordinal();
  private static final int TARGET_ONLY_SLOT = ChannelColor.RED.ordinal();
  private static final double SCROLL_DOWN = -1;
  private static final double SCROLL_UP = 1;
  private static final String NOT_SWITCHED = "Shift + scroll should switch the mode";
  private static final String SWITCHED = "Shift + scroll should only switch a Configurator";
  private static final String NOT_OPENED = "The Open mode should take the click and copy nothing";
  private static final String NOT_COPIED = "A click in the Copy mode should copy the node";
  private static final String NOT_PASTED = "The copy should follow the face of the target";
  private static final String NOT_REPLACED = "The paste should replace every slot of the target";
  private static final String PASTED = "Another player should not paste";
  private static final String NOT_CLEARED = "Shift + click in the air should clear the copy";
  private static final String NOT_JOINED = "A paste on a node with no network should join it";
  private static final String JOINED = "Only who placed the node should paste a network on it";
  private static final String NOT_TOGGLED = "Toggle should enable and disable the channels";
  private static final String TOGGLED = "Only the owner should toggle the channels";
  private static final String REACTED = "The controller should not take the Configurator";

  private ConfiguratorGameTests() {}

  /** Like the hotbar: down goes forward, up goes back (answer 192). */
  @GameTest(template = EMPTY)
  public static void theShiftScrollSwitchesTheModeOnTheServer(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    ItemStack configurator = holdConfigurator(owner, ConfiguratorMode.FIRST);

    ConfiguratorItem.switchMode(owner, SCROLL_DOWN);
    helper.assertTrue(
        ConfiguratorItem.modeOf(configurator) == ConfiguratorMode.ROTATE, NOT_SWITCHED);
    ConfiguratorItem.switchMode(owner, SCROLL_UP);
    ConfiguratorItem.switchMode(owner, SCROLL_UP);
    helper.assertTrue(
        ConfiguratorItem.modeOf(configurator) == ConfiguratorMode.WRENCH, NOT_SWITCHED);
    helper.succeed();
  }

  /** The server checks the item in the hand, whatever the client sends (rule 17). */
  @GameTest(template = EMPTY)
  public static void theShiftScrollDoesNotSwitchAnotherItem(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    ItemStack stick = new ItemStack(Items.STICK);
    owner.setItemInHand(InteractionHand.MAIN_HAND, stick);

    ConfiguratorItem.switchMode(owner, SCROLL_DOWN);

    helper.assertFalse(stick.has(RedweaveDataComponents.CONFIGURATOR_MODE.get()), SWITCHED);
    helper.succeed();
  }

  /**
   * The mock player cannot receive a screen, so the test checks that the Open mode takes the click
   * and copies nothing; the screen itself is checked in the manual test script (answer 140).
   */
  @GameTest(template = EMPTY)
  public static void theOpenModeTakesTheClickWithoutCopying(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeScene.placeNode(helper, owner, SOURCE_STONE, Direction.EAST);
    ItemStack configurator = holdConfigurator(owner, ConfiguratorMode.OPEN);

    InteractionResult result = NodeScene.click(helper, owner, SOURCE_NODE);

    helper.assertTrue(result.consumesAction(), NOT_OPENED);
    helper.assertFalse(configurator.has(RedweaveDataComponents.NODE_SLOTS.get()), NOT_OPENED);
    helper.succeed();
  }

  /**
   * A slot on the face the source sticks to goes to the face of the target (answer 105), and the
   * paste replaces all 16 slots, so a slot only the target had is gone.
   */
  @GameTest(template = EMPTY)
  public static void aPasteFollowsTheFaceTheTargetSticksTo(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeId source = configuredSource(helper, owner);
    NodeId target = NodeScene.placeNode(helper, owner, TARGET_STONE, Direction.NORTH);
    NodeScene.enableSlot(owner, target, TARGET_ONLY_SLOT);
    ItemStack configurator = holdConfigurator(owner, ConfiguratorMode.COPY);

    NodeScene.click(helper, owner, SOURCE_NODE);
    NodeScene.shiftClick(helper, owner, TARGET_NODE);

    helper.assertTrue(configurator.has(RedweaveDataComponents.NODE_SLOTS.get()), NOT_COPIED);
    List<Optional<ChannelSlot>> slots = NodeScene.slotsOf(target);
    helper.assertTrue(slots.get(FIRST_SLOT).orElseThrow().face() == Face.NORTH, NOT_PASTED);
    helper.assertTrue(
        slots
            .get(FIRST_SLOT)
            .map(ChannelSlot::channel)
            .equals(NodeScene.slotsOf(source).get(FIRST_SLOT).map(ChannelSlot::channel)),
        NOT_PASTED);
    helper.assertTrue(
        slots.get(SECOND_SLOT).equals(NodeScene.slotsOf(source).get(SECOND_SLOT)), NOT_PASTED);
    helper.assertTrue(slots.get(TARGET_ONLY_SLOT).isEmpty(), NOT_REPLACED);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void anotherPlayerDoesNotPaste(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    configuredSource(helper, owner);
    NodeId target = NodeScene.placeNode(helper, owner, TARGET_STONE, Direction.NORTH);
    List<Optional<ChannelSlot>> before = NodeScene.slotsOf(target);
    holdConfigurator(stranger, ConfiguratorMode.COPY);

    NodeScene.click(helper, stranger, SOURCE_NODE);
    NodeScene.shiftClick(helper, stranger, TARGET_NODE);

    helper.assertTrue(before.equals(NodeScene.slotsOf(target)), PASTED);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void shiftAndClickInTheAirClearsTheCopy(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    configuredSource(helper, owner);
    ItemStack configurator = holdConfigurator(owner, ConfiguratorMode.COPY);
    NodeScene.click(helper, owner, SOURCE_NODE);

    owner.setShiftKeyDown(true);
    configurator.use(helper.getLevel(), owner, InteractionHand.MAIN_HAND);

    helper.assertFalse(configurator.has(RedweaveDataComponents.NODE_SLOTS.get()), NOT_CLEARED);
    helper.succeed();
  }

  /** The Configurator no longer keeps a network (answer 126). */
  @GameTest(template = EMPTY)
  public static void theControllerDoesNotTakeTheConfigurator(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    ItemStack configurator = holdConfigurator(owner, ConfiguratorMode.FIRST);

    NodeScene.click(helper, owner, CONTROLLER);

    helper.assertTrue(configurator.getComponentsPatch().isEmpty(), REACTED);
    helper.succeed();
  }

  /** A node with an input on the face it sticks to and an output on another face. */
  /**
   * A paste on a node with no network joins it to the network of the copy and applies the slots,
   * for whoever placed it, owner of that network (answer 238).
   */
  @GameTest(template = EMPTY)
  public static void aPasteOnANodeWithNoNetworkJoinsTheNetworkOfTheCopy(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeId source = configuredSource(helper, owner);
    NodeId target = NodeScene.placeLooseNode(helper, owner, TARGET_STONE, Direction.NORTH);
    holdConfigurator(owner, ConfiguratorMode.COPY);

    NodeScene.click(helper, owner, SOURCE_NODE);
    NodeScene.shiftClick(helper, owner, TARGET_NODE);

    helper.assertTrue(NodeScene.networkOf(target).id().equals(network), NOT_JOINED);
    helper.assertTrue(
        NodeScene.slotsOf(target)
            .get(SECOND_SLOT)
            .equals(NodeScene.slotsOf(source).get(SECOND_SLOT)),
        NOT_JOINED);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void onlyWhoPlacedTheNodePastesANetworkOnIt(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    configuredSource(helper, owner);
    Player placer = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeId target = NodeScene.placeLooseNode(helper, placer, TARGET_STONE, Direction.NORTH);
    holdConfigurator(owner, ConfiguratorMode.COPY);

    NodeScene.click(helper, owner, SOURCE_NODE);
    NodeScene.shiftClick(helper, owner, TARGET_NODE);

    helper.assertTrue(
        RedweaveServer.running().orElseThrow().networkOfNode(target).isEmpty(), JOINED);
    helper.succeed();
  }

  /** A click enables every configured channel, Shift + click disables them (answer 241). */
  @GameTest(template = EMPTY)
  public static void theToggleModeEnablesAndDisablesTheChannels(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeId node = NodeScene.placeNode(helper, owner, TARGET_STONE, Direction.NORTH);
    PlayerId ownerId = new PlayerId(owner.getUUID());
    NodeScene.networkOf(node)
        .setUpSlot(
            ownerId,
            node,
            SECOND_SLOT,
            io.github.zenndg.redweave.domain.network.Direction.INPUT,
            Face.NORTH);
    holdConfigurator(owner, ConfiguratorMode.TOGGLE);

    NodeScene.click(helper, owner, TARGET_NODE);
    helper.assertTrue(NodeScene.slotsOf(node).get(SECOND_SLOT).orElseThrow().active(), NOT_TOGGLED);
    Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);
    holdConfigurator(stranger, ConfiguratorMode.TOGGLE);
    NodeScene.shiftClick(helper, stranger, TARGET_NODE);
    helper.assertTrue(NodeScene.slotsOf(node).get(SECOND_SLOT).orElseThrow().active(), TOGGLED);
    NodeScene.shiftClick(helper, owner, TARGET_NODE);
    helper.assertFalse(
        NodeScene.slotsOf(node).get(SECOND_SLOT).orElseThrow().active(), NOT_TOGGLED);
    helper.succeed();
  }

  private static NodeId configuredSource(GameTestHelper helper, Player owner) {
    NodeId node = NodeScene.placeNode(helper, owner, SOURCE_STONE, Direction.EAST);
    NodeScene.enableSlot(owner, node, FIRST_SLOT);
    NodeScene.networkOf(node)
        .configureSlot(
            new PlayerId(owner.getUUID()),
            node,
            SECOND_SLOT,
            new ChannelSlot(
                io.github.zenndg.redweave.domain.network.Direction.OUTPUT,
                Face.UP,
                SECOND_CHANNEL));
    return node;
  }

  /** Holds a Configurator in the mode; the mode is set only when it is not the first one. */
  static ItemStack holdConfigurator(Player player, ConfiguratorMode mode) {
    ItemStack configurator = new ItemStack(RedweaveItems.CONFIGURATOR.get());
    if (mode != ConfiguratorMode.FIRST) {
      configurator.set(RedweaveDataComponents.CONFIGURATOR_MODE.get(), mode);
    }
    player.setItemInHand(InteractionHand.MAIN_HAND, configurator);
    return configurator;
  }
}
