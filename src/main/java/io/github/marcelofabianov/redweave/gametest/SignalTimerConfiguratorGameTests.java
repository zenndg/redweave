package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.part.ModulePart;
import io.github.marcelofabianov.redweave.block.part.TimerPart;
import io.github.marcelofabianov.redweave.domain.module.SignalTimer;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.item.ConfiguratorMode;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** The Configurator on a Signal Timer: modulos-de-sinal.md, Configurator e mão vazia (406). */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SignalTimerConfiguratorGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos TIMER = STONE.above();
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final float LOOK_NORTH = 180;
  private static final int QUARTERS = 4;
  private static final String NETWORK = "network";
  private static final String NOT_ACROSS = "The front should stay across the face of the plate";
  private static final String NOT_ROUND = "Four turns should give four fronts and come back";
  private static final String NOT_CLOCKWISE = "Seen from above, north should turn to east";
  private static final String TURNED = "Only the owner should turn the plate";
  private static final String NOT_RUNNING = "The Toggle should start the timer";
  private static final String NOT_STOPPED = "The Toggle should stop the timer";
  private static final String TOGGLED = "A network turned off should take no Toggle";
  private static final String NOT_PICKED = "The Wrench of the owner should pick the timer up";
  private static final String BROKEN = "Only the owner should break the timer in survival";

  private SignalTimerConfiguratorGameTests() {}

  /** On every face, four turns go round the four edges, clockwise from the front (403). */
  @GameTest(template = EMPTY)
  public static void theOwnerTurnsThePlateOnEveryFace(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    for (Direction face : Direction.values()) {
      BlockPos plate = STONE.relative(face);
      helper.setBlock(plate, Blocks.AIR);
      place(helper, owner, face);
      Direction start = frontOf(helper, plate);
      Set<Direction> fronts = new HashSet<>();
      ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.OPEN);
      for (int turn = 0; turn < QUARTERS; turn++) {
        NodeScene.shiftClick(helper, owner, plate);
        Direction front = frontOf(helper, plate);
        helper.assertTrue(front.getAxis() != face.getAxis(), NOT_ACROSS);
        fronts.add(front);
      }
      helper.assertTrue(fronts.size() == QUARTERS && frontOf(helper, plate) == start, NOT_ROUND);
      helper.setBlock(plate, Blocks.AIR);
    }
    helper.succeed();
  }

  /** On the floor, looking at the plate from above, north turns to east (answer 403). */
  @GameTest(template = EMPTY)
  public static void aTurnOnTheFloorIsClockwiseSeenFromAbove(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    owner.setYHeadRot(LOOK_NORTH);
    place(helper, owner, Direction.UP);
    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.OPEN);

    NodeScene.shiftClick(helper, owner, TIMER);

    helper.assertValueEqual(frontOf(helper, TIMER), Direction.EAST, NOT_CLOCKWISE);
    helper.succeed();
  }

  /** Another player neither turns, picks up nor breaks the plate in survival (378 and 394). */
  @GameTest(template = EMPTY)
  public static void aStrangerCannotChangeTheTimer(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner, Direction.UP);
    Direction front = frontOf(helper, TIMER);

    ConfiguratorGameTests.holdConfigurator(stranger, ConfiguratorMode.OPEN);
    NodeScene.shiftClick(helper, stranger, TIMER);
    helper.assertValueEqual(frontOf(helper, TIMER), front, TURNED);
    ConfiguratorGameTests.holdConfigurator(stranger, ConfiguratorMode.WRENCH);
    NodeScene.click(helper, stranger, TIMER);
    ModuleParts.assertPresent(helper, TIMER);
    helper.assertFalse(GameTestActions.breakBy(helper, stranger, TIMER), BROKEN);
    helper.assertTrue(GameTestActions.breakBy(helper, owner, TIMER), BROKEN);
    helper.succeed();
  }

  /** The Toggle starts the timer, which lights its LED, and stops it again (answer 395). */
  @GameTest(template = EMPTY)
  public static void theToggleStartsAndStopsTheTimer(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner, Direction.UP);
    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.TOGGLE);
    TimerPart timer = ModuleParts.only(helper, TIMER, TimerPart.class);

    NodeScene.click(helper, owner, TIMER);

    helper
        .startSequence()
        .thenExecute(() -> helper.assertTrue(isRunning(timer), NOT_RUNNING))
        .thenWaitUntil(() -> ModuleParts.assertTimerRunning(helper, TIMER, true))
        .thenExecute(() -> NodeScene.click(helper, owner, TIMER))
        .thenExecute(() -> helper.assertFalse(isRunning(timer), NOT_STOPPED))
        .thenWaitUntil(() -> ModuleParts.assertTimerRunning(helper, TIMER, false))
        .thenSucceed();
  }

  /** A network turned off takes no Toggle, but the plate still turns (answers 402 and 431). */
  @GameTest(template = EMPTY)
  public static void aNetworkTurnedOffTakesNoToggleButTheTurn(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    place(helper, owner, Direction.UP);
    join(helper, network);
    PlayerId id = new PlayerId(owner.getUUID());
    server().network(network).orElseThrow().terminal().turnOff(id);
    Direction front = frontOf(helper, TIMER);

    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.TOGGLE);
    NodeScene.click(helper, owner, TIMER);
    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.OPEN);
    NodeScene.shiftClick(helper, owner, TIMER);

    helper.assertFalse(isRunning(ModuleParts.only(helper, TIMER, TimerPart.class)), TOGGLED);
    helper.assertFalse(frontOf(helper, TIMER) == front, TURNED);
    helper.succeed();
  }

  /** The Wrench of the owner takes the timer to the inventory as a plain item (answer 395). */
  @GameTest(template = EMPTY)
  public static void theWrenchPicksUpTheTimer(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner, Direction.UP);
    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.WRENCH);

    NodeScene.click(helper, owner, TIMER);

    ModuleParts.assertAbsent(helper, TIMER);
    helper.assertTrue(owner.getInventory().contains(timerItem()), NOT_PICKED);
    helper.succeed();
  }

  private static void place(GameTestHelper helper, Player player, Direction face) {
    helper.setBlock(STONE, Blocks.STONE);
    GameTestActions.placeOn(helper, player, timerItem(), STONE, face);
  }

  /** Joins the timer to the network in its NBT; its screen comes later (answer 397). */
  private static void join(GameTestHelper helper, NetworkId network) {
    CompoundTag tag = ModuleParts.only(helper, TIMER, ModulePart.class).save();
    tag.putUUID(NETWORK, network.value());
    ModuleParts.reload(helper, TIMER, tag);
  }

  private static Direction frontOf(GameTestHelper helper, BlockPos plate) {
    return ModuleParts.only(helper, plate, TimerPart.class).front();
  }

  private static boolean isRunning(TimerPart timer) {
    return timer.timer().map(SignalTimer::isRunning).orElse(false);
  }

  private static ItemStack timerItem() {
    return new ItemStack(RedweaveItems.SIGNAL_TIMER.get());
  }

  private static RedweaveServer server() {
    return RedweaveServer.running().orElseThrow();
  }
}
