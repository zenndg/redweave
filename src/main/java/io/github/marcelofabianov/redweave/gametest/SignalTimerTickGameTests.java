package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.SignalTimerBlock;
import io.github.marcelofabianov.redweave.block.SignalTimerBlockEntity;
import io.github.marcelofabianov.redweave.block.part.NodePart;
import io.github.marcelofabianov.redweave.domain.module.SignalModule;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The tick of the Signal Timer in the world: its front edge, its `Pause` and its output on a
 * channel (answers 380, 387, 407, 408 and 412). The Configurator that starts a timer comes later,
 * so the tests start it in its NBT, as a `/data merge` would.
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SignalTimerTickGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos TIMER = STONE.above();
  private static final BlockPos LAMP_IN_FRONT = TIMER.north();
  private static final BlockPos BEHIND = TIMER.south();
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(2, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos NODE_LAMP = new BlockPos(0, 1, 0);
  private static final float LOOK_NORTH = 180;
  private static final ChannelColor CHANNEL = ChannelColor.RED;
  private static final int SETTLE_TICKS = 30;
  private static final UUID DELETED = UUID.fromString("5f6a7b8c-9d0e-4f1a-8b2c-3d4e5f6a7b8c");
  private static final String ONLY_FRONT = "Only the block in front should get the pulse";
  private static final String STILL_LINKED = "A timer should let a deleted network go";
  private static final String RUNNING = "running";
  private static final String NETWORK = "network";
  private static final String OUTPUT = "output";

  private SignalTimerTickGameTests() {}

  /** Started, the timer lights the lamp in front at once and puts it out after the pulse. */
  @GameTest(template = EMPTY)
  public static void aRunningTimerPulsesItsFrontEdge(GameTestHelper helper) {
    placeTimer(helper, helper.makeMockPlayer(GameType.SURVIVAL));
    helper.setBlock(LAMP_IN_FRONT, Blocks.REDSTONE_LAMP);

    start(helper, tag -> {});

    helper
        .startSequence()
        .thenWaitUntil(() -> lamp(helper, LAMP_IN_FRONT, true))
        .thenWaitUntil(() -> helper.assertBlockProperty(TIMER, SignalTimerBlock.POWERED, true))
        .thenWaitUntil(() -> lamp(helper, LAMP_IN_FRONT, false))
        .thenSucceed();
  }

  /** A signal behind pauses it, off; its release restarts the cycle with the pulse (408, 412). */
  @GameTest(template = EMPTY)
  public static void aSignalBehindPausesTheTimer(GameTestHelper helper) {
    placeTimer(helper, helper.makeMockPlayer(GameType.SURVIVAL));
    helper.setBlock(LAMP_IN_FRONT, Blocks.REDSTONE_LAMP);
    helper.setBlock(BEHIND, Blocks.REDSTONE_BLOCK);

    start(helper, tag -> {});

    helper
        .startSequence()
        .thenWaitUntil(() -> helper.assertBlockProperty(TIMER, SignalTimerBlock.RUNNING, true))
        .thenIdle(SETTLE_TICKS)
        .thenExecute(() -> helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, false))
        .thenExecute(() -> helper.assertBlockProperty(TIMER, SignalTimerBlock.POWERED, false))
        .thenExecute(() -> helper.setBlock(BEHIND, Blocks.AIR))
        .thenWaitUntil(() -> lamp(helper, LAMP_IN_FRONT, true))
        .thenSucceed();
  }

  /** With its output on a channel, a node of the network gives the pulse to its lamp (375). */
  @GameTest(template = EMPTY)
  public static void aTimerOnAChannelReachesANode(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeId node = placeNodeOnALamp(helper, owner);
    NodeScene.networkOf(node)
        .configureSlot(
            new PlayerId(owner.getUUID()),
            node,
            CHANNEL.ordinal(),
            new ChannelSlot(Direction.OUTPUT, Face.UP, CHANNEL));
    placeTimer(helper, owner);
    helper.setBlock(LAMP_IN_FRONT, Blocks.REDSTONE_LAMP);

    start(
        helper,
        tag -> {
          tag.putUUID(NETWORK, network.value());
          tag.putString(OUTPUT, CHANNEL.key());
        });

    helper.succeedWhen(() -> {
      lamp(helper, NODE_LAMP, true);
      helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, false);
    });
  }

  /**
   * On every face, the running timer feeds only the block in front of its front edge, as the game
   * asks it from that block, and gives 0 to the others (answers 406 and 407).
   */
  @GameTest(template = EMPTY)
  public static void aTimerOnEveryFaceFeedsOnlyItsFront(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    helper.setBlock(STONE, Blocks.STONE);
    for (net.minecraft.core.Direction face : net.minecraft.core.Direction.values()) {
      helper.setBlock(STONE.relative(face), Blocks.AIR);
      GameTestActions.placeOn(
          helper, owner, new ItemStack(RedweaveItems.SIGNAL_TIMER.get()), STONE, face);
      startAt(helper, STONE.relative(face));
    }
    helper.succeedWhen(() -> {
      for (net.minecraft.core.Direction face : net.minecraft.core.Direction.values()) {
        BlockPos plate = helper.absolutePos(STONE.relative(face));
        net.minecraft.core.Direction front =
            helper.getLevel().getBlockState(plate).getValue(SignalTimerBlock.FRONT);
        for (net.minecraft.core.Direction side : net.minecraft.core.Direction.values()) {
          int expected = side == front.getOpposite() ? Signal.MAX.strength() : 0;
          helper.assertValueEqual(helper.getLevel().getSignal(plate, side), expected, ONLY_FRONT);
        }
      }
    });
  }

  /** A timer whose network was deleted lets it go, until its owner picks another (385). */
  @GameTest(template = EMPTY)
  public static void aTimerLetsADeletedNetworkGo(GameTestHelper helper) {
    placeTimer(helper, helper.makeMockPlayer(GameType.SURVIVAL));

    start(helper, tag -> tag.putUUID(NETWORK, DELETED));

    SignalTimerBlockEntity timer = helper.getBlockEntity(TIMER);
    helper.succeedWhen(() ->
        helper.assertValueEqual(
            timer.module().flatMap(SignalModule::network), Optional.empty(), STILL_LINKED));
  }

  private static void placeTimer(GameTestHelper helper, Player player) {
    helper.setBlock(STONE, Blocks.STONE);
    player.setYHeadRot(LOOK_NORTH);
    GameTestActions.placeOn(
        helper,
        player,
        new ItemStack(RedweaveItems.SIGNAL_TIMER.get()),
        STONE,
        net.minecraft.core.Direction.UP);
  }

  /** A node on top of a lamp, in the network, which feeds the lamp under it (answer 71). */
  private static NodeId placeNodeOnALamp(GameTestHelper helper, Player owner) {
    helper.setBlock(NODE_LAMP, Blocks.REDSTONE_LAMP);
    GameTestActions.placeOn(
        helper,
        owner,
        new ItemStack(RedweaveItems.SIGNAL_NODE.get()),
        NODE_LAMP,
        net.minecraft.core.Direction.UP);
    NodeScene.choose(helper, owner, NODE_LAMP.above(), NodeScene.FIRST_CHOICE);
    NodePart block = NodeParts.only(helper, NODE_LAMP.above());
    return block.node().orElseThrow();
  }

  /** Starts the timer in its NBT, with the changes of the test. */
  private static void start(GameTestHelper helper, Consumer<CompoundTag> changes) {
    startAt(helper, TIMER, changes);
  }

  private static void startAt(GameTestHelper helper, BlockPos at) {
    startAt(helper, at, tag -> {});
  }

  private static void startAt(GameTestHelper helper, BlockPos at, Consumer<CompoundTag> changes) {
    SignalTimerBlockEntity timer = helper.getBlockEntity(at);
    RegistryAccess registries = helper.getLevel().registryAccess();
    CompoundTag tag = timer.saveWithoutMetadata(registries);
    tag.putBoolean(RUNNING, true);
    changes.accept(tag);
    timer.loadWithComponents(tag, registries);
  }

  private static void lamp(GameTestHelper helper, BlockPos lamp, boolean lit) {
    helper.assertBlockProperty(lamp, RedstoneLampBlock.LIT, lit);
  }
}
