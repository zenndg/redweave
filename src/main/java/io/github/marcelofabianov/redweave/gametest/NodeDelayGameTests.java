package io.github.marcelofabianov.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.NodeSignals;
import io.github.marcelofabianov.redweave.block.part.NodePart;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.Node;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.OutputDelay;
import io.github.marcelofabianov.redweave.item.ConfiguratorMode;
import io.github.marcelofabianov.redweave.menu.DelaySteps;
import io.github.marcelofabianov.redweave.menu.NodeAction;
import io.github.marcelofabianov.redweave.menu.NodeMenu;
import io.github.marcelofabianov.redweave.menu.NodeScreenCodes;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The output delay of the Signal Node in the world: the lens at once and the redstone after the
 * delay, the cut when the node stops counting, the save, the copy and the screen
 * (_docs/funcionalidades/no-de-sinal.md, answers 859 to 875).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NodeDelayGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos LEVER_BLOCK = new BlockPos(0, 1, 0);
  private static final BlockPos LEVER = LEVER_BLOCK.above();
  private static final BlockPos STONE = new BlockPos(2, 1, 2);
  private static final BlockPos OUTPUT_NODE = STONE.above();
  private static final BlockPos OTHER_STONE = new BlockPos(2, 1, 0);
  private static final BlockPos OTHER_NODE = OTHER_STONE.above();
  private static final ChannelColor CHANNEL = ChannelColor.WHITE;
  private static final int SLOT = CHANNEL.ordinal();
  private static final ChannelSlot OUTPUT = new ChannelSlot(Direction.OUTPUT, Face.UP, CHANNEL);
  private static final OutputDelay ONE_SECOND = new OutputDelay(OutputDelay.STEP_TICKS);
  private static final int BEFORE_THE_DELAY = 10;
  private static final int SETTLE_TICKS = 3;
  private static final int MENU_ID = 1;
  private static final int FULL = 15;
  private static final String STILL = "The output of the node should wait for the delay";
  private static final String NOT_THROUGH = "The output of the node should follow after the delay";
  private static final String NOT_CUT = "A node that stops counting should emit 0 at once";
  private static final String WAITING = "A cut node should have nothing waiting";
  private static final String NOT_SAVED = "What waits should come back from the save of the node";
  private static final String NOT_EMPTY = "A node saved before the delay has nothing waiting";
  private static final String NOT_STEPPED = "Only the owner should step the delay of the node";
  private static final int NO_CHANNEL = -1;
  private static final String NOT_A_SLOT = "Only an index of the node should give a slot";
  private static final String NOT_PASTED = "The paste should give the delay, never what waits";

  private NodeDelayGameTests() {}

  /** The lens follows the channel at once, and the redstone waits for the delay (860, 863). */
  @GameTest(template = EMPTY)
  public static void theLensFollowsAtOnceAndTheOutputWaits(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeId node = leverAndOutput(helper, owner);
    NodePart part = NodeParts.only(helper, OUTPUT_NODE);
    helper
        .startSequence()
        .thenIdle(SETTLE_TICKS)
        .thenExecute(() -> setDelay(helper, owner, node, ONE_SECOND))
        .thenExecute(() -> helper.pullLever(LEVER))
        .thenWaitUntil(() -> NodeParts.assertPowered(helper, OUTPUT_NODE, true))
        .thenIdle(BEFORE_THE_DELAY)
        .thenExecute(() -> helper.assertValueEqual(part.emitted(), 0, STILL))
        .thenWaitUntil(() -> helper.assertValueEqual(part.emitted(), FULL, NOT_THROUGH))
        .thenSucceed();
  }

  /**
   * With the lever back off and that change still waiting, the network is turned off: no face of it
   * changes, and the node emits 0 at once, with nothing waiting (answer 862).
   */
  @GameTest(template = EMPTY)
  public static void aNodeThatStopsCountingIsCutAtOnce(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeId node = leverAndOutput(helper, owner);
    NodePart part = NodeParts.only(helper, OUTPUT_NODE);
    helper
        .startSequence()
        .thenExecute(() -> helper.pullLever(LEVER))
        .thenWaitUntil(() -> helper.assertValueEqual(part.emitted(), FULL, NOT_THROUGH))
        .thenExecute(() -> setDelay(helper, owner, node, ONE_SECOND))
        .thenExecute(() -> helper.pullLever(LEVER))
        .thenWaitUntil(() -> NodeParts.assertPowered(helper, OUTPUT_NODE, false))
        .thenExecute(() -> helper.assertValueEqual(part.emitted(), FULL, STILL))
        .thenExecute(() -> networkOf(node).terminal().turnOff(idOf(owner)))
        .thenIdle(SETTLE_TICKS)
        .thenExecute(() -> helper.assertValueEqual(part.emitted(), 0, NOT_CUT))
        .thenExecute(() -> helper.assertTrue(part.delayed().line().isEmpty(), WAITING))
        .thenSucceed();
  }

  /** What waits and what the node gave before it come back from its save (869 and 873). */
  @GameTest(template = EMPTY)
  public static void whatWaitsComesBackFromTheSave(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeId node = leverAndOutput(helper, owner);
    NodePart part = NodeParts.only(helper, OUTPUT_NODE);
    helper
        .startSequence()
        .thenExecute(() -> helper.pullLever(LEVER))
        .thenWaitUntil(() -> helper.assertValueEqual(part.emitted(), FULL, NOT_THROUGH))
        .thenExecute(() -> setDelay(helper, owner, node, ONE_SECOND))
        .thenExecute(() -> helper.pullLever(LEVER))
        .thenWaitUntil(() -> helper.assertFalse(part.delayed().line().isEmpty(), NOT_SAVED))
        .thenExecute(() -> {
          NodePart loaded = NodePart.load(part.face(), part.save());
          helper.assertValueEqual(loaded.delayed().line(), part.delayed().line(), NOT_SAVED);
          helper.assertValueEqual(loaded.delayed().output(), part.delayed().output(), NOT_SAVED);
        })
        .thenSucceed();
  }

  /** A node saved before the #177 has nothing waiting, with no migration (869, rule 15). */
  @GameTest(template = EMPTY)
  public static void anOldSaveHasNothingWaiting(GameTestHelper helper) {
    NodePart node = NodePart.load(net.minecraft.core.Direction.DOWN, new CompoundTag());

    helper.assertTrue(node.delayed().line().isEmpty(), NOT_EMPTY);
    helper.assertValueEqual(node.delayed().delay(), OutputDelay.NONE, NOT_EMPTY);
    helper.succeed();
  }

  /** The owner of the network steps the delay on the screen; anyone else changes nothing (866). */
  @GameTest(template = EMPTY)
  public static void onlyTheOwnerStepsTheDelay(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeId node = leverAndOutput(helper, owner);
    ServerPlayer viewer = viewerAs(helper, owner);
    ServerPlayer stranger = viewerAs(helper, helper.makeMockPlayer(GameType.SURVIVAL));
    NodeMenu ownerMenu = new NodeMenu(MENU_ID, viewer, NodeParts.spotOf(helper, OUTPUT_NODE));
    NodeMenu strangerMenu = new NodeMenu(MENU_ID, stranger, NodeParts.spotOf(helper, OUTPUT_NODE));
    int longer = NodeScreenCodes.button(NodeAction.delay(DelaySteps.LONGER));

    strangerMenu.clickMenuButton(stranger, longer);
    helper.assertValueEqual(delayOf(node), OutputDelay.NONE, NOT_STEPPED);
    ownerMenu.clickMenuButton(viewer, longer);
    helper.assertValueEqual(delayOf(node), ONE_SECOND, NOT_STEPPED);
    helper.assertValueEqual(ownerMenu.delaySeconds(), ONE_SECOND.seconds(), NOT_STEPPED);
    helper.succeed();
  }

  /**
   * The menu gives no slot for an index outside the 16 of the node, as the screen asks with no
   * channel open: it once read the delay there, and the game crashed (#179).
   */
  @GameTest(template = EMPTY)
  public static void anIndexOutsideTheSlotsIsNoSlot(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeId node = leverAndOutput(helper, owner);
    setDelay(helper, owner, node, ONE_SECOND);
    NodeMenu menu =
        new NodeMenu(MENU_ID, viewerAs(helper, owner), NodeParts.spotOf(helper, OUTPUT_NODE));

    helper.assertTrue(menu.slot(NO_CHANNEL).isEmpty(), NOT_A_SLOT);
    helper.assertTrue(menu.slot(Node.SLOT_COUNT).isEmpty(), NOT_A_SLOT);
    helper.assertValueEqual(menu.signal(NO_CHANNEL), 0, NOT_A_SLOT);
    helper.assertTrue(menu.slot(SLOT).isPresent(), NOT_A_SLOT);
    helper.succeed();
  }

  /** The Configurator pastes the delay with the slots, never what waits (answer 868). */
  @GameTest(template = EMPTY)
  public static void theCopyTakesTheDelay(GameTestHelper helper) {
    Player owner = CopyScene.copier(helper, new ArrayList<>());
    NodeId from = leverAndOutput(helper, owner);
    NodeId to = NodeScene.placeNode(helper, owner, OTHER_STONE, net.minecraft.core.Direction.UP);
    setDelay(helper, owner, from, ONE_SECOND);

    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.COPY);
    NodeScene.click(helper, owner, OUTPUT_NODE);
    NodeScene.shiftClick(helper, owner, OTHER_NODE);

    helper.assertValueEqual(delayOf(to), ONE_SECOND, NOT_PASTED);
    helper.assertTrue(NodeParts.only(helper, OTHER_NODE).delayed().line().isEmpty(), NOT_PASTED);
    helper.succeed();
  }

  /** A lever read into the channel by one node, and another node emitting it into its stone. */
  private static NodeId leverAndOutput(GameTestHelper helper, Player owner) {
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    helper.setBlock(LEVER_BLOCK, Blocks.STONE);
    BlockState lever = Blocks.LEVER.defaultBlockState().setValue(LeverBlock.FACE, AttachFace.FLOOR);
    helper.setBlock(LEVER, lever);
    NodeId input =
        NodeScene.placeNode(helper, owner, LEVER_BLOCK, net.minecraft.core.Direction.EAST);
    NodeScene.enableSlot(owner, input, SLOT);
    NodeId output = NodeScene.placeNode(helper, owner, STONE, net.minecraft.core.Direction.UP);
    networkOf(output).configureSlot(idOf(owner), output, SLOT, OUTPUT);
    return output;
  }

  /** The delay as the screen sets it: on the node, and its part hears of it at once. */
  private static void setDelay(GameTestHelper helper, Player owner, NodeId node, OutputDelay to) {
    networkOf(node).nodeSettings().delay(idOf(owner), node, to);
    NodeSignals.refreshEmission(
        RedweaveServer.running().orElseThrow(),
        helper.getLevel(),
        NodeParts.spotOf(helper, OUTPUT_NODE));
  }

  private static OutputDelay delayOf(NodeId node) {
    return networkOf(node).node(node).orElseThrow().delay();
  }

  private static Network networkOf(NodeId node) {
    return NodeScene.networkOf(node);
  }

  private static PlayerId idOf(Player player) {
    return new PlayerId(player.getUUID());
  }

  private static ServerPlayer viewerAs(GameTestHelper helper, Player player) {
    ServerPlayer viewer =
        FakePlayerFactory.get(
            helper.getLevel(), new GameProfile(player.getUUID(), player.getScoreboardName()));
    viewer.setPos(Vec3.atCenterOf(helper.absolutePos(OUTPUT_NODE.above())));
    return viewer;
  }
}
