package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.part.FlipFlopPart;
import io.github.zenndg.redweave.block.part.LeverPart;
import io.github.zenndg.redweave.domain.module.Connection;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.item.ConfiguratorMode;
import io.github.zenndg.redweave.menu.FlipFlopAction;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The tick of the Signal Flip-Flop in the world: _docs/funcionalidades/signal-flip-flop.md, the
 * rising edge behind it, the input already on, the `Toggle`, the network that comes back, the loop
 * on its own output and the owner (answers 878 to 883, 889 and 892).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SignalFlipFlopGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos FLIP_FLOP = STONE.above();
  private static final BlockPos LAMP_IN_FRONT = FLIP_FLOP.north();
  private static final BlockPos BEHIND = FLIP_FLOP.south();
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(2, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos WRITER_STONE = new BlockPos(0, 1, 0);
  private static final ChannelColor CHANNEL = ChannelColor.RED;
  private static final int NINE = 9;
  private static final int SETTLE = 10;
  private static final String NOT_OWNER = "Whoever places the flip-flop should own it";
  private static final String NOT_EDGES = "A new flip-flop should use its edges";
  private static final String FLIPPED = "The flip-flop should not have flipped";
  private static final String NOT_FLIPPED = "The flip-flop should have flipped";
  private static final String BROKEN = "Only the owner should break the flip-flop";

  private SignalFlipFlopGameTests() {}

  /** Placed, it is off, on its edges, and belongs to whoever placed it (878 and 881). */
  @GameTest(template = EMPTY)
  public static void aPlacedFlipFlopIsOffOnItsEdges(GameTestHelper helper) {
    Player player = helper.makeMockPlayer(GameType.SURVIVAL);
    helper.setBlock(LAMP_IN_FRONT, Blocks.REDSTONE_LAMP);
    FlipFlopPart placed = FlipFlopScene.place(helper, player, STONE);

    helper.assertTrue(
        placed.module().orElseThrow().isOwner(new PlayerId(player.getUUID())), NOT_OWNER);
    helper.assertValueEqual(placed.input(), Connection.EDGE, NOT_EDGES);
    helper.assertValueEqual(placed.output(), Connection.EDGE, NOT_EDGES);
    helper
        .startSequence()
        .thenIdle(SETTLE)
        .thenExecute(() -> state(helper, placed, false))
        .thenExecute(() -> helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, false))
        .thenSucceed();
  }

  /** Each signal that starts behind it flips it; holding or releasing does not (879). */
  @GameTest(template = EMPTY)
  public static void eachPulseBehindFlipsIt(GameTestHelper helper) {
    helper.setBlock(LAMP_IN_FRONT, Blocks.REDSTONE_LAMP);
    FlipFlopPart flipFlop =
        FlipFlopScene.place(helper, helper.makeMockPlayer(GameType.SURVIVAL), STONE);

    helper
        .startSequence()
        .thenIdle(SETTLE)
        .thenExecute(() -> helper.setBlock(BEHIND, Blocks.REDSTONE_BLOCK))
        .thenWaitUntil(() -> helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, true))
        .thenIdle(SETTLE)
        .thenExecute(() -> state(helper, flipFlop, true))
        .thenExecute(() -> helper.setBlock(BEHIND, Blocks.AIR))
        .thenIdle(SETTLE)
        .thenExecute(() -> state(helper, flipFlop, true))
        .thenExecute(() -> helper.setBlock(BEHIND, Blocks.REDSTONE_BLOCK))
        .thenWaitUntil(
            () -> helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, false))
        .thenSucceed();
  }

  /** A signal already behind it when placed is not an edge (answer 881). */
  @GameTest(template = EMPTY)
  public static void aSignalAlreadyBehindDoesNotFlipIt(GameTestHelper helper) {
    helper.setBlock(BEHIND, Blocks.REDSTONE_BLOCK);
    FlipFlopPart flipFlop =
        FlipFlopScene.place(helper, helper.makeMockPlayer(GameType.SURVIVAL), STONE);

    helper
        .startSequence()
        .thenIdle(SETTLE)
        .thenExecute(() -> state(helper, flipFlop, false))
        .thenExecute(() -> ModuleParts.assertPowered(helper, FLIP_FLOP, false))
        .thenSucceed();
  }

  /** The `Toggle` of the owner flips it, also with its network `Offline` (answer 882). */
  @GameTest(template = EMPTY)
  public static void theToggleFlipsItWithTheNetworkOffline(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    FlipFlopPart flipFlop = FlipFlopScene.place(helper, owner, STONE);
    FlipFlopScene.connect(helper, owner, FLIP_FLOP, FlipFlopAction.Kind.OUTPUT, CHANNEL);
    FlipFlopScene.turn(network, owner, false);

    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.TOGGLE);
    NodeScene.click(helper, owner, FLIP_FLOP);

    state(helper, flipFlop, true);
    helper.succeed();
  }

  /** Its input on a channel freezes while the network is off, so its return is no edge (889). */
  @GameTest(template = EMPTY)
  public static void aNetworkTurnedOnAgainDoesNotFlipIt(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    LeverPart writer = LampScene.writer(helper, owner, WRITER_STONE, CHANNEL, NINE);
    FlipFlopPart flipFlop = FlipFlopScene.place(helper, owner, STONE);
    FlipFlopScene.connect(helper, owner, FLIP_FLOP, FlipFlopAction.Kind.INPUT, CHANNEL);

    helper
        .startSequence()
        .thenIdle(SETTLE)
        .thenExecute(() -> writer.setLever(writer.lever().toggle()))
        .thenWaitUntil(() -> state(helper, flipFlop, true))
        .thenExecute(() -> FlipFlopScene.turn(network, owner, false))
        .thenIdle(SETTLE)
        .thenExecute(() -> FlipFlopScene.turn(network, owner, true))
        .thenIdle(SETTLE)
        .thenExecute(() -> state(helper, flipFlop, true))
        .thenSucceed();
  }

  /** Reading its own output, it settles off and stays so, with no burnout (answer 883). */
  @GameTest(template = EMPTY)
  public static void aLoopOnItsOwnOutputSettlesOff(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    FlipFlopPart flipFlop = FlipFlopScene.place(helper, owner, STONE);
    FlipFlopScene.connect(helper, owner, FLIP_FLOP, FlipFlopAction.Kind.INPUT, CHANNEL);
    FlipFlopScene.connect(helper, owner, FLIP_FLOP, FlipFlopAction.Kind.OUTPUT, CHANNEL);

    helper
        .startSequence()
        .thenIdle(SETTLE)
        .thenExecute(() -> flipFlop.setFlipFlop(flipFlop.flipFlop().toggle()))
        .thenWaitUntil(() -> state(helper, flipFlop, false))
        .thenIdle(SETTLE)
        .thenExecute(() -> state(helper, flipFlop, false))
        .thenExecute(() -> ModuleParts.assertPowered(helper, FLIP_FLOP, false))
        .thenSucceed();
  }

  /** Another player cannot break the plate in survival; its owner can (answers 378 and 394). */
  @GameTest(template = EMPTY)
  public static void onlyTheOwnerBreaksTheFlipFlop(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);
    FlipFlopScene.place(helper, owner, STONE);

    helper.assertFalse(GameTestActions.breakBy(helper, stranger, FLIP_FLOP), BROKEN);
    ModuleParts.assertPresent(helper, FLIP_FLOP);
    helper.assertTrue(GameTestActions.breakBy(helper, owner, FLIP_FLOP), BROKEN);
    helper.succeed();
  }

  private static void state(GameTestHelper helper, FlipFlopPart flipFlop, boolean on) {
    helper.assertValueEqual(flipFlop.flipFlop().on(), on, on ? NOT_FLIPPED : FLIPPED);
  }
}
