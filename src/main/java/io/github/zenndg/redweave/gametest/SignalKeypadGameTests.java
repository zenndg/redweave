package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.PartSpaceBlockEntity;
import io.github.zenndg.redweave.block.part.KeypadPart;
import io.github.zenndg.redweave.block.part.LeverPart;
import io.github.zenndg.redweave.block.part.PartSettings;
import io.github.zenndg.redweave.domain.module.KeypadCode;
import io.github.zenndg.redweave.domain.module.KeypadEntry;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.item.ConfiguratorMode;
import io.github.zenndg.redweave.menu.KeypadAction;
import io.github.zenndg.redweave.server.RedweaveServer;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Signal Keypad in the world: _docs/funcionalidades/teclado.md. Power on its input channel, the
 * right code for its open time, the misses, the warning to an offline owner, the secrets out of the
 * packet, the `Toggle` with the network off, the removal and the copy (answers 659 to 667, 731 and
 * 896 to 916).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SignalKeypadGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos KEYPAD = STONE.above();
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(2, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos WRITER_STONE = new BlockPos(0, 1, 0);
  private static final ChannelColor CHANNEL = ChannelColor.RED;
  private static final KeypadCode CODE = new KeypadCode("2468");
  private static final KeypadCode WRONG = new KeypadCode("1111");
  private static final String SALT = "0f1e2d3c4b5a69788796a5b4c3d2e1f0";
  private static final int NINE = 9;
  private static final int SETTLE = 10;
  private static final int FREE_TRIES = 3;
  private static final int OPEN_TICKS = 60;
  private static final String PARTS = "parts";
  private static final String DATA = "data";
  private static final String[] SECRETS = {"code_hash", "code_salt", "attempts", "cards"};
  private static final String STRENGTH = "strength";
  private static final String NOT_GRANTED = "The right code with power should open the keypad";
  private static final String NOT_REFUSED = "The keypad should refuse with no power";
  private static final String NOT_LOCKED = "The keypad should lock after its open time";
  private static final String NOT_WAITING = "Past the free tries, a try should wait";
  private static final String NOT_REPORTED = "An offline owner should get the misses when joining";
  private static final String SENT = "A secret of the keypad should never reach a client";
  private static final String NOT_SAVED = "A secret of the keypad should stay in the save";
  private static final String NOT_OPENED = "The Toggle should open the keypad with the network off";
  private static final String BROKEN = "Nobody should break the keypad by hand in survival";
  private static final String COPIED = "The keypad should never be copied";

  private SignalKeypadGameTests() {}

  /** With power, the right code opens it for its open time, and then it locks (663, 907). */
  @GameTest(template = EMPTY)
  public static void theRightCodeOpensItForItsOpenTime(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    KeypadPart keypad = powered(helper, owner);

    helper
        .startSequence()
        .thenWaitUntil(() -> helper.assertTrue(keypad.inputOn(), NOT_GRANTED))
        .thenExecute(() ->
            helper.assertValueEqual(
                keypad.enter(owner, CODE).kind(), KeypadEntry.Kind.GRANTED, NOT_GRANTED))
        .thenWaitUntil(() -> ModuleParts.assertPowered(helper, KEYPAD, true))
        .thenIdle(OPEN_TICKS + SETTLE)
        .thenExecute(() -> helper.assertFalse(keypad.keypad().unlocked(), NOT_LOCKED))
        .thenSucceed();
  }

  /** With no power on its channel, it refuses any code (answers 661 and 906). */
  @GameTest(template = EMPTY)
  public static void withNoPowerItRefuses(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    KeypadPart keypad = KeypadScene.place(helper, owner, STONE);
    keypad.setKeypad(keypad.keypad().withCode(CODE, SALT));

    helper
        .startSequence()
        .thenIdle(SETTLE)
        .thenExecute(() ->
            helper.assertValueEqual(
                keypad.enter(owner, CODE).kind(), KeypadEntry.Kind.NO_POWER, NOT_REFUSED))
        .thenSucceed();
  }

  /** Past the free tries a player waits, and the offline owner hears of it (664 to 666, 903). */
  @GameTest(template = EMPTY)
  public static void missesWaitAndAreKeptForAnOfflineOwner(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    KeypadPart keypad = powered(helper, owner);
    ServerPlayer thief = helper.makeMockServerPlayerInLevel();
    owner.connection.disconnect(Component.empty());

    helper
        .startSequence()
        .thenWaitUntil(() -> helper.assertTrue(keypad.inputOn(), NOT_GRANTED))
        .thenExecute(() -> {
          for (int miss = 0; miss <= FREE_TRIES; miss++) {
            keypad.enter(thief, WRONG);
          }
        })
        .thenExecute(() ->
            helper.assertValueEqual(
                keypad.enter(thief, CODE).kind(), KeypadEntry.Kind.WAIT, NOT_WAITING))
        .thenExecute(() ->
            helper.assertFalse(
                RedweaveServer.running()
                    .orElseThrow()
                    .modules()
                    .keypadReports()
                    .take(new PlayerId(owner.getUUID()))
                    .isEmpty(),
                NOT_REPORTED))
        .thenSucceed();
  }

  /** The code, the tries and the cards stay in the save and out of the packet (896 to 898). */
  @GameTest(template = EMPTY)
  public static void itsSecretsNeverReachAClient(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    KeypadPart keypad = KeypadScene.place(helper, owner, STONE);
    keypad.setKeypad(keypad.keypad().withCode(CODE, SALT));
    PartSpaceBlockEntity space =
        PartSpaceBlockEntity.at(helper.getLevel(), helper.absolutePos(KEYPAD)).orElseThrow();

    CompoundTag sent = dataOf(space.getUpdateTag(helper.getLevel().registryAccess()));
    CompoundTag saved = dataOf(space.saveWithoutMetadata(helper.getLevel().registryAccess()));

    for (String secret : SECRETS) {
      helper.assertFalse(sent.contains(secret), SENT);
      helper.assertTrue(saved.contains(secret), NOT_SAVED);
    }
    helper.assertTrue(sent.contains(STRENGTH), SENT);
    helper.succeed();
  }

  /** The `Toggle` of the owner opens it even with its network off (answers 667 and 906). */
  @GameTest(template = EMPTY)
  public static void theToggleOpensItWithTheNetworkOff(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NetworkId network = NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    KeypadPart keypad = KeypadScene.place(helper, owner, STONE);
    KeypadScene.turn(network, owner, false);

    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.TOGGLE);
    NodeScene.click(helper, owner, KEYPAD);

    helper.assertTrue(keypad.keypad().unlocked(), NOT_OPENED);
    helper.succeed();
  }

  /** Nobody breaks it by hand in survival, and it is never copied (answers 641 and 731). */
  @GameTest(template = EMPTY)
  public static void itIsGuardedAndNeverCopied(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    KeypadPart keypad = KeypadScene.place(helper, owner, STONE);

    helper.assertFalse(GameTestActions.breakBy(helper, owner, KEYPAD), BROKEN);
    ModuleParts.assertPresent(helper, KEYPAD);
    helper.assertFalse(PartSettings.copiable(keypad), COPIED);
    helper.succeed();
  }

  /** A keypad with a code, its input on the channel a lever of its network writes, on. */
  private static KeypadPart powered(GameTestHelper helper, ServerPlayer owner) {
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    LeverPart writer = LampScene.writer(helper, owner, WRITER_STONE, CHANNEL, NINE);
    writer.setLever(writer.lever().toggle());
    KeypadPart keypad = KeypadScene.place(helper, owner, STONE);
    KeypadScene.connect(helper, owner, KEYPAD, KeypadAction.Kind.INPUT, CHANNEL);
    keypad.setKeypad(keypad.keypad().withCode(CODE, SALT));
    return keypad;
  }

  /** The `data` of the only part of the space. */
  private static CompoundTag dataOf(CompoundTag space) {
    return space.getList(PARTS, Tag.TAG_COMPOUND).getCompound(0).getCompound(DATA);
  }
}
