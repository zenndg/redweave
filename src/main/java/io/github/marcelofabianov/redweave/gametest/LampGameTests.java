package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.part.LampPart;
import io.github.marcelofabianov.redweave.block.part.LeverPart;
import io.github.marcelofabianov.redweave.domain.module.LampMode;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.item.ConfiguratorMode;
import io.github.marcelofabianov.redweave.menu.LampAction;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LightLayer;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Signal Lamp in the world: _docs/funcionalidades/lampada.md. Its light is the strength of its
 * intensity channel, it gives the strength of its screen to its block of support while lit, it is
 * dark with its network off, its control turns it off in `Hold` and in `Pulse`, the `Toggle` of its
 * owner switches it, a stranger changes nothing, and the client draws it as the server does
 * (answers 650 to 658).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class LampGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(2, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos WRITER_STONE = new BlockPos(0, 1, 0);
  private static final BlockPos CONTROL_STONE = new BlockPos(0, 1, 2);
  private static final BlockPos LAMP_STONE = new BlockPos(2, 1, 0);
  private static final BlockPos LAMP = LAMP_STONE.above();
  private static final ChannelColor INTENSITY = ChannelColor.RED;
  private static final ChannelColor CONTROL = ChannelColor.BLUE;
  private static final int NINE = 9;
  private static final int SIX = 6;
  private static final int NINE_STEPS_DOWN = 9;
  private static final String WRONG_LIGHT = "The light should be the strength of the channel";
  private static final String WRONG_WORLD_LIGHT = "The world should be lit by the lamp";
  private static final String WRONG_SUPPORT = "The block of support should get the strength";
  private static final String NOT_DARK = "The lamp should be dark";
  private static final String NOT_SWITCHED = "The Toggle should switch the lamp";
  private static final String STRANGER = "Another player should change nothing";
  private static final String CLIENT_DIFFERS = "The client should draw the lamp as the server";
  private static final String NOT_KEPT = "The lamp should keep its setting in its save";

  private LampGameTests() {}

  /**
   * The light is the strength of the channel, and the support gets the strength set (650 to 653).
   */
  @GameTest(template = EMPTY)
  public static void itsLightFollowsTheChannelAndItFeedsItsSupport(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    LampPart lamp = litScene(helper, owner);
    for (int step = 0; step < NINE_STEPS_DOWN; step++) {
      LampScene.click(helper, owner, LAMP, LampAction.strength(false));
    }

    helper.succeedWhen(() -> {
      helper.assertValueEqual(lamp.light(), NINE, WRONG_LIGHT);
      helper.assertValueEqual(
          helper.getLevel().getBrightness(LightLayer.BLOCK, helper.absolutePos(LAMP)),
          NINE,
          WRONG_WORLD_LIGHT);
      helper.assertValueEqual(
          helper.getLevel().getSignal(helper.absolutePos(LAMP), Direction.UP), SIX, WRONG_SUPPORT);
    });
  }

  /** With its network off, the lamp is dark and gives nothing (answer 654). */
  @GameTest(template = EMPTY)
  public static void itIsDarkWithItsNetworkOff(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    LampPart lamp = lampOn(helper, owner);
    RedweaveServer.running()
        .orElseThrow()
        .network(network)
        .orElseThrow()
        .terminal()
        .turnOff(new PlayerId(owner.getUUID()));

    helper.succeedWhen(() -> {
      helper.assertValueEqual(lamp.light(), 0, NOT_DARK);
      helper.assertFalse(lamp.powered(), NOT_DARK);
    });
  }

  /** In `Hold`, the control keeps the lamp dark while it has a signal (answers 656 and 657). */
  @GameTest(template = EMPTY)
  public static void inHoldTheControlKeepsItDark(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    LampPart lamp = litScene(helper, owner);
    LeverPart control = LampScene.writer(helper, owner, CONTROL_STONE, CONTROL, NINE);
    LampScene.listen(helper, owner, LAMP, LampAction.Kind.CONTROL, CONTROL);
    control.setLever(control.lever().toggle());

    helper.succeedWhen(() -> {
      helper.assertValueEqual(lamp.light(), 0, NOT_DARK);
      helper.assertTrue(lamp.lamp().off(true), NOT_DARK);
    });
  }

  /** In `Pulse`, a pulse of the control switches the lamp off, and it stays off (answer 656). */
  @GameTest(template = EMPTY)
  public static void inPulseAPulseSwitchesIt(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    LampPart lamp = litScene(helper, owner);
    LeverPart control = LampScene.writer(helper, owner, CONTROL_STONE, CONTROL, NINE);
    LampScene.listen(helper, owner, LAMP, LampAction.Kind.CONTROL, CONTROL);
    LampScene.click(helper, owner, LAMP, LampAction.mode(LampMode.PULSE.ordinal()));

    helper
        .startSequence()
        .thenExecuteAfter(1, () -> control.setLever(control.lever().toggle()))
        .thenExecuteAfter(SIX, () -> control.setLever(control.lever().toggle()))
        .thenWaitUntil(() -> {
          helper.assertTrue(lamp.lamp().switchedOff(), NOT_SWITCHED);
          helper.assertValueEqual(lamp.light(), 0, NOT_DARK);
        })
        .thenSucceed();
  }

  /** The `Toggle` of the owner switches the lamp off and on again (answer 658). */
  @GameTest(template = EMPTY)
  public static void theToggleSwitchesIt(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    LampPart lamp = litScene(helper, owner);
    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.TOGGLE);

    NodeScene.click(helper, owner, LAMP);
    helper.assertTrue(lamp.lamp().switchedOff(), NOT_SWITCHED);
    NodeScene.click(helper, owner, LAMP);
    helper.assertFalse(lamp.lamp().switchedOff(), NOT_SWITCHED);
    helper.succeed();
  }

  /** The `Toggle` switches the lamp even with its network off (answer 716). */
  @GameTest(template = EMPTY)
  public static void theToggleSwitchesItWithTheNetworkOffline(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    LampPart lamp = lampOn(helper, owner);
    RedweaveServer.running()
        .orElseThrow()
        .network(network)
        .orElseThrow()
        .terminal()
        .turnOff(new PlayerId(owner.getUUID()));

    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.TOGGLE);
    NodeScene.click(helper, owner, LAMP);

    helper.assertTrue(lamp.lamp().switchedOff(), NOT_SWITCHED);
    helper.succeed();
  }

  /** Another player changes nothing on the screen (answers 378 and 658). */
  @GameTest(template = EMPTY)
  public static void aStrangerChangesNothing(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    LampPart lamp = LampScene.place(helper, owner, LAMP_STONE);
    LampScene.click(
        helper,
        helper.makeMockPlayer(GameType.SURVIVAL),
        LAMP,
        LampAction.mode(LampMode.PULSE.ordinal()));

    helper.assertValueEqual(lamp.lamp().mode(), LampMode.HOLD, STRANGER);
    helper.succeed();
  }

  /**
   * The save keeps its setting, and a copy from it, as the client gets, looks the same (rule 15).
   */
  @GameTest(template = EMPTY)
  public static void theSaveKeepsItAndTheClientDrawsIt(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    LampPart lamp = litScene(helper, owner);
    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.TOGGLE);
    NodeScene.click(helper, owner, LAMP);

    helper.succeedWhen(() -> {
      LampPart client = LampPart.load(lamp.face(), lamp.save());
      helper.assertValueEqual(client.look(), lamp.look(), CLIENT_DIFFERS);
      helper.assertValueEqual(client.light(), lamp.light(), CLIENT_DIFFERS);
      helper.assertValueEqual(client.lamp(), lamp.lamp(), NOT_KEPT);
      helper.assertValueEqual(client.intensity(), lamp.intensity(), NOT_KEPT);
      helper.assertValueEqual(client.color(), lamp.color(), NOT_KEPT);
    });
  }

  /** A network, a lever writing 9 on red, and a lamp listening to red. */
  private static LampPart litScene(GameTestHelper helper, ServerPlayer owner) {
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    return lampOn(helper, owner);
  }

  private static LampPart lampOn(GameTestHelper helper, ServerPlayer owner) {
    LeverPart writer = LampScene.writer(helper, owner, WRITER_STONE, INTENSITY, NINE);
    writer.setLever(writer.lever().toggle());
    LampPart lamp = LampScene.place(helper, owner, LAMP_STONE);
    LampScene.listen(helper, owner, LAMP, LampAction.Kind.INTENSITY, INTENSITY);
    return lamp;
  }
}
