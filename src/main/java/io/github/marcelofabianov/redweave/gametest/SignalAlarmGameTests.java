package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.block.part.AlarmCall;
import io.github.marcelofabianov.redweave.block.part.AlarmPart;
import io.github.marcelofabianov.redweave.block.part.LeverPart;
import io.github.marcelofabianov.redweave.config.NeoForgeAlarmLimits;
import io.github.marcelofabianov.redweave.domain.module.AlarmListeners;
import io.github.marcelofabianov.redweave.domain.module.AlarmSettings;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.menu.AlarmAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Signal Alarm in the world: _docs/funcionalidades/alarme.md. A rising input rings it for its
 * duration and then it stops, even with the input on; it rings again after the input turns off and
 * on; the `Toggle` silences it; only its listeners hear it; and the client draws what the server
 * has (answers 643 to 649).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SignalAlarmGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos ALARM = STONE.above();
  private static final BlockPos BEHIND = ALARM.south();
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(2, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos WRITER_STONE = new BlockPos(0, 1, 0);
  private static final int SHORT = 20;
  private static final int PAST_THE_END = 30;
  private static final int NINE = 9;
  private static final int FOUR = 4;
  private static final double FAR = 100;
  private static final double NEAR = 2;
  private static final String NOT_PLACED = "A new alarm should be quiet, heard by its owner";
  private static final String NOT_RINGING = "A rising input should ring the alarm";
  private static final String STILL_RINGING = "The alarm should stop after its duration";
  private static final String NOT_SILENCED = "The Toggle should silence the alarm";
  private static final String WRONG_LISTENERS = "Only the chosen players in reach should hear";
  private static final String CLIENT_DIFFERS = "The client should draw the alarm as the server";
  private static final String NOT_KEPT = "The alarm should keep its settings in its save";

  private SignalAlarmGameTests() {}

  /** Placed, it is quiet, heard by its owner, 3 rings 1 s apart, for 5 s (answer 648). */
  @GameTest(template = EMPTY)
  public static void aPlacedAlarmIsQuiet(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    AlarmPart alarm = AlarmScene.place(helper, owner, STONE);

    helper.assertFalse(alarm.alarm().ringing(), NOT_PLACED);
    helper.assertValueEqual(
        alarm.alarm().settings(), AlarmSettings.placed(new NeoForgeAlarmLimits()), NOT_PLACED);
    helper.succeed();
  }

  /** A block of redstone behind rings it, and it stops after its duration with it still there. */
  @GameTest(template = EMPTY)
  public static void aRisingInputRingsItForItsDurationOnly(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    AlarmPart alarm = AlarmScene.place(helper, owner, STONE);
    shortened(alarm);
    helper.setBlock(BEHIND, Blocks.REDSTONE_BLOCK);

    helper
        .startSequence()
        .thenWaitUntil(() -> helper.assertTrue(alarm.alarm().ringing(), NOT_RINGING))
        .thenIdle(PAST_THE_END)
        .thenExecute(() -> helper.assertFalse(alarm.alarm().ringing(), STILL_RINGING))
        .thenExecute(() -> helper.setBlock(BEHIND, Blocks.AIR))
        .thenIdle(NINE)
        .thenExecute(() -> helper.setBlock(BEHIND, Blocks.REDSTONE_BLOCK))
        .thenWaitUntil(() -> helper.assertTrue(alarm.alarm().ringing(), NOT_RINGING))
        .thenSucceed();
  }

  /** The `Toggle` silences a ringing alarm and says so with its name (answer 649). */
  @GameTest(template = EMPTY)
  public static void theToggleSilencesIt(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    AlarmPart alarm = AlarmScene.place(helper, owner, STONE);
    helper.setBlock(BEHIND, Blocks.REDSTONE_BLOCK);

    helper
        .startSequence()
        .thenWaitUntil(() -> helper.assertTrue(alarm.alarm().ringing(), NOT_RINGING))
        .thenExecute(() -> {
          Optional<Component> said = alarm.toggle();
          helper.assertFalse(alarm.alarm().ringing(), NOT_SILENCED);
          helper.assertValueEqual(
              said.map(Component::getContents)
                  .filter(TranslatableContents.class::isInstance)
                  .map(contents -> ((TranslatableContents) contents).getKey()),
              Optional.of(ModuleMessages.ALARM_SILENCED),
              NOT_SILENCED);
        })
        .thenIdle(NINE)
        .thenExecute(() -> helper.assertFalse(alarm.alarm().ringing(), NOT_SILENCED))
        .thenSucceed();
  }

  /**
   * Its owner hears it; with `Everyone in range`, a stranger nearby hears it too; nobody far away
   * hears it (answers 645 and 646).
   */
  @GameTest(template = EMPTY)
  public static void onlyItsListenersInReachHearIt(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    ServerPlayer near = helper.makeMockServerPlayerInLevel();
    ServerPlayer far = helper.makeMockServerPlayerInLevel();
    AlarmPart alarm = AlarmScene.place(helper, owner, STONE);
    Vec3 center = Vec3.atCenterOf(helper.absolutePos(ALARM));
    owner.setPos(center.add(NEAR, 0, 0));
    near.setPos(center.add(0, 0, NEAR));
    far.setPos(center.add(FAR, 0, 0));

    List<ServerPlayer> ownerOnly =
        AlarmCall.listeners(helper.getLevel(), helper.absolutePos(ALARM), alarm);
    alarm.setAlarm(
        alarm.alarm().withSettings(alarm.alarm().settings().withListeners(AlarmListeners.RANGE)));
    List<ServerPlayer> everyone =
        AlarmCall.listeners(helper.getLevel(), helper.absolutePos(ALARM), alarm);

    helper.assertTrue(ownerOnly.contains(owner), WRONG_LISTENERS);
    helper.assertFalse(ownerOnly.contains(near), WRONG_LISTENERS);
    helper.assertTrue(everyone.contains(owner) && everyone.contains(near), WRONG_LISTENERS);
    helper.assertFalse(everyone.contains(far), WRONG_LISTENERS);
    helper.succeed();
  }

  /** A lever of the network writes the channel of its input, which rings it (answer 643). */
  @GameTest(template = EMPTY)
  public static void aChannelRingsIt(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    AlarmPart alarm = AlarmScene.place(helper, owner, STONE);
    LeverPart writer = LampScene.writer(helper, owner, WRITER_STONE, ChannelColor.RED, NINE);
    AlarmScene.listen(helper, owner, ALARM, ChannelColor.RED);
    writer.setLever(writer.lever().toggle());

    helper.succeedWhen(() -> helper.assertTrue(alarm.alarm().ringing(), NOT_RINGING));
  }

  /** The owner sets who hears it and its rings on the screen (answers 645 and 646). */
  @GameTest(template = EMPTY)
  public static void theOwnerSetsItOnTheScreen(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    AlarmPart alarm = AlarmScene.place(helper, owner, STONE);

    AlarmScene.click(helper, owner, ALARM, AlarmAction.listeners());
    AlarmScene.click(helper, owner, ALARM, AlarmAction.repeats(true));

    helper.assertValueEqual(alarm.alarm().settings().listeners(), AlarmListeners.NETWORK, NOT_KEPT);
    helper.assertValueEqual(alarm.alarm().settings().repeats(), FOUR, NOT_KEPT);
    helper.succeed();
  }

  /** A copy loaded from its save, as the client gets it, draws it ringing as the server (649). */
  @GameTest(template = EMPTY)
  public static void theClientDrawsItRinging(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    AlarmPart alarm = AlarmScene.place(helper, owner, STONE);
    helper.setBlock(BEHIND, Blocks.REDSTONE_BLOCK);

    helper.succeedWhen(() -> {
      helper.assertTrue(alarm.alarm().ringing(), NOT_RINGING);
      AlarmPart client = AlarmPart.load(Direction.DOWN, alarm.save());
      helper.assertValueEqual(client.look(), alarm.look(), CLIENT_DIFFERS);
      helper.assertValueEqual(client.alarm(), alarm.alarm(), NOT_KEPT);
    });
  }

  /** The settings, without the alarm under way, go to another alarm (answers 727 and 732). */
  @GameTest(template = EMPTY)
  public static void anAlarmPastesItsSettings(GameTestHelper helper) {
    BlockPos otherStone = STONE.west();
    Player owner = CopyScene.copier(helper, new ArrayList<>());
    AlarmPart from = AlarmScene.place(helper, owner, STONE);
    AlarmPart to = AlarmScene.place(helper, owner, otherStone);
    from.setAlarm(
        from.alarm().withSettings(from.alarm().settings().withListeners(AlarmListeners.RANGE)));

    CopyScene.copyThenPaste(helper, owner, ALARM, otherStone.above());

    helper.assertValueEqual(to.alarm().settings(), from.alarm().settings(), NOT_KEPT);
    helper.assertFalse(to.alarm().ringing(), NOT_KEPT);
    helper.succeed();
  }

  /** One ring, so the duration may be as short as one interval (answer 737). */
  private static void shortened(AlarmPart alarm) {
    NeoForgeAlarmLimits limits = new NeoForgeAlarmLimits();
    alarm.setAlarm(
        alarm
            .alarm()
            .withSettings(
                alarm.alarm().settings().withRepeats(1, limits).withDuration(SHORT, limits)));
  }
}
