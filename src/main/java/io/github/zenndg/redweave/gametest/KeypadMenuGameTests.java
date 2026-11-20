package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.part.KeypadPart;
import io.github.zenndg.redweave.domain.module.KeypadCode;
import io.github.zenndg.redweave.domain.signal.Signal;
import io.github.zenndg.redweave.menu.KeypadAction;
import io.github.zenndg.redweave.menu.KeypadCodes;
import io.github.zenndg.redweave.menu.KeypadEntryMenu;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The screens and the save of the Signal Keypad on the server: the owner sets the code and steps
 * the strength and the open time, anyone else changes nothing, the keypad screen sends a try, and
 * the save comes back whole (answers 378, 660, 663, 905 and 910, rules 15 to 17).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class KeypadMenuGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos KEYPAD = STONE.above();
  private static final KeypadCode CODE = new KeypadCode("0042");
  private static final Signal FOURTEEN = new Signal(14);
  private static final int FOUR_SECONDS = 80;
  private static final int SEVEN = 7;
  private static final int MENU_ID = 1;
  private static final String SALT = "00ff11ee22dd33cc44bb55aa66997788";
  private static final String NOT_SET = "The owner should set the code on the screen";
  private static final String NOT_STEPPED = "The owner should step the strength and open time";
  private static final String STRANGER = "Another player should change nothing";
  private static final String NO_ANSWER = "The keypad screen should answer a try";
  private static final String NOT_KEPT = "The save of the keypad should come back whole";
  private static final String NOT_SAFE = "A damaged code should leave the keypad with none";
  private static final String DAMAGED_HASH = "not-a-hash";
  private static final String HASH_KEY = "code_hash";

  private KeypadMenuGameTests() {}

  /** The owner sets a code, with leading zeros, and steps the strength and the open time. */
  @GameTest(template = EMPTY)
  public static void theOwnerSetsTheCodeAndStepsTheSettings(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    KeypadPart keypad = KeypadScene.place(helper, owner, STONE);

    KeypadScene.click(helper, owner, KEYPAD, KeypadAction.code(CODE));
    KeypadScene.click(helper, owner, KEYPAD, KeypadAction.strength(false));
    KeypadScene.click(helper, owner, KEYPAD, KeypadAction.open(true));

    helper.assertTrue(keypad.keypad().secret().orElseThrow().matches(CODE), NOT_SET);
    helper.assertValueEqual(keypad.keypad().strength(), FOURTEEN, NOT_STEPPED);
    helper.assertValueEqual(keypad.keypad().openTicks(), FOUR_SECONDS, NOT_STEPPED);
    helper.succeed();
  }

  /** Another player sets no code and steps nothing (answer 378). */
  @GameTest(template = EMPTY)
  public static void aStrangerChangesNothing(GameTestHelper helper) {
    KeypadPart keypad = KeypadScene.place(helper, helper.makeMockPlayer(GameType.SURVIVAL), STONE);
    Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);

    KeypadScene.click(helper, stranger, KEYPAD, KeypadAction.code(CODE));
    KeypadScene.click(helper, stranger, KEYPAD, KeypadAction.strength(false));

    helper.assertTrue(keypad.keypad().secret().isEmpty(), STRANGER);
    helper.assertValueEqual(keypad.keypad().strength(), Signal.MAX, STRANGER);
    helper.succeed();
  }

  /** `Enter` on the keypad screen sends the code, and the screen gets what came of it (662). */
  @GameTest(template = EMPTY)
  public static void theKeypadScreenSendsATry(GameTestHelper helper) {
    ServerPlayer player = helper.makeMockServerPlayerInLevel();
    KeypadPart keypad = KeypadScene.place(helper, player, STONE);
    keypad.setKeypad(keypad.keypad().withCode(CODE, SALT));
    KeypadEntryMenu menu = new KeypadEntryMenu(MENU_ID, player, ModuleParts.spotOf(helper, KEYPAD));

    helper.assertTrue(menu.clickMenuButton(player, KeypadCodes.encode(CODE)), NO_ANSWER);
    helper.assertTrue(menu.result().isPresent(), NO_ANSWER);
    helper.succeed();
  }

  /** Its settings, code, tries and cards come back whole (answer 910, rule 15). */
  @GameTest(template = EMPTY)
  public static void aKeypadKeepsItsSaveWhole(GameTestHelper helper) {
    KeypadPart keypad = KeypadScene.place(helper, helper.makeMockPlayer(GameType.SURVIVAL), STONE);
    keypad.setKeypad(keypad.keypad().withCode(CODE, SALT).withStrength(SEVEN).toggle());
    CompoundTag saved = keypad.save();

    helper.assertValueEqual(KeypadPart.load(Direction.DOWN, saved).save(), saved, NOT_KEPT);
    helper.succeed();
  }

  /** A damaged code leaves the keypad with none, closed to everyone (answer 660, rule 14). */
  @GameTest(template = EMPTY)
  public static void aDamagedCodeLeavesNoCode(GameTestHelper helper) {
    KeypadPart keypad = KeypadScene.place(helper, helper.makeMockPlayer(GameType.SURVIVAL), STONE);
    keypad.setKeypad(keypad.keypad().withCode(CODE, SALT));
    CompoundTag saved = keypad.save();
    saved.putString(HASH_KEY, DAMAGED_HASH);

    Optional<?> secret = KeypadPart.load(Direction.DOWN, saved).keypad().secret();
    helper.assertTrue(secret.isEmpty(), NOT_SAFE);
    helper.succeed();
  }
}
