package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.PartSpaceBlockEntity;
import io.github.zenndg.redweave.block.part.ButtonPart;
import io.github.zenndg.redweave.block.part.CablePart;
import io.github.zenndg.redweave.block.part.CableReach;
import io.github.zenndg.redweave.domain.module.ButtonTime;
import io.github.zenndg.redweave.domain.module.Connection;
import io.github.zenndg.redweave.domain.module.SignalButton;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Signal Button in the world: _docs/funcionalidades/botao.md, the hand of its owner, the
 * release on its own, its edges, its save and the cable in front (answers 553, 556, 566, 575, 588,
 * 590, 591 and 592).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SignalButtonGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos BUTTON = STONE.above();
  private static final BlockPos LAMP_IN_FRONT = BUTTON.north();
  private static final BlockPos LAMP_BY_SUPPORT = STONE.east();
  private static final BlockPos BESIDE_FLOOR = BUTTON.north().below();
  private static final int SEVEN = 7;
  private static final int FIVE = 5;
  private static final int TOO_STRONG = 40;
  private static final int TOO_LONG = 9000;
  private static final int LONGEST = 6000;
  private static final int A_FEW_TICKS = 5;
  private static final String RED = "red";
  private static final UUID MODULE = UUID.fromString("8a9b0c1d-2e3f-4a5b-9c6d-7e8f9a0b1c2d");
  private static final UUID OWNER = UUID.fromString("9b0c1d2e-3f4a-4b6c-8d7e-8f9a0b1c2d3e");
  private static final UUID NETWORK = UUID.fromString("0c1d2e3f-4a5b-4c7d-9e8f-9a0b1c2d3e4f");
  private static final String NOT_PLACED = "A new button should be off, at 15, for 1 s";
  private static final String NOT_OWNER = "Whoever places the button should own it";
  private static final String NOT_ON = "The hand of the owner should press the button";
  private static final String NOT_RELEASED = "The button should turn off on its own";
  private static final String STARTED_OVER = "A second press should not start the time over";
  private static final String STRANGER = "Another player should not press the button";
  private static final String NOT_KEPT = "The save of the button should come back whole";
  private static final String NOT_SAFE = "A damaged save should go back to safe values";
  private static final String NOT_FITTED = "An on time past the config should be fitted";
  private static final String NOT_PLATE = "The cable in front should reach the plate";

  private SignalButtonGameTests() {}

  /** Placed, it is off, at 15, for 1 s, on its edge, owned by whoever placed it (552, 556). */
  @GameTest(template = EMPTY)
  public static void aPlacedButtonIsOff(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    ButtonPart button = ButtonScene.place(helper, owner, STONE);

    helper.assertFalse(button.button().on(), NOT_PLACED);
    helper.assertValueEqual(button.button().strength(), Signal.MAX, NOT_PLACED);
    helper.assertValueEqual(
        button.button().time(), new ButtonTime(ButtonTime.PLACED_TICKS), NOT_PLACED);
    helper.assertValueEqual(button.output(), Connection.EDGE, NOT_PLACED);
    helper.assertTrue(
        button.module().orElseThrow().isOwner(new PlayerId(owner.getUUID())), NOT_OWNER);
    helper.succeed();
  }

  /**
   * The empty hand of the owner presses it, which feeds the block in front and its block of
   * support, and it turns off on its own after its on time (answers 553, 556 and 588).
   */
  @GameTest(template = EMPTY)
  public static void aPressedButtonReleasesOnItsOwn(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    ButtonPart button = ButtonScene.place(helper, owner, STONE);
    helper.setBlock(LAMP_IN_FRONT, Blocks.REDSTONE_LAMP);
    helper.setBlock(LAMP_BY_SUPPORT, Blocks.REDSTONE_LAMP);

    LeverScene.click(helper, owner, ItemStack.EMPTY, BUTTON);
    helper.assertTrue(button.button().on(), NOT_ON);

    helper
        .startSequence()
        .thenWaitUntil(() -> {
          helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, true);
          helper.assertBlockProperty(LAMP_BY_SUPPORT, RedstoneLampBlock.LIT, true);
        })
        .thenWaitUntil(() -> helper.assertFalse(button.button().on(), NOT_RELEASED))
        .thenWaitUntil(() -> {
          helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, false);
          helper.assertBlockProperty(LAMP_BY_SUPPORT, RedstoneLampBlock.LIT, false);
        })
        .thenSucceed();
  }

  /** Pressed again while on, nothing changes and the time goes on (answer 590). */
  @GameTest(template = EMPTY)
  public static void aSecondPressDoesNotStartTheTimeOver(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    ButtonPart button = ButtonScene.place(helper, owner, STONE);

    LeverScene.click(helper, owner, new ItemStack(Items.STICK), BUTTON);

    helper
        .startSequence()
        .thenIdle(A_FEW_TICKS)
        .thenExecute(() -> LeverScene.click(helper, owner, ItemStack.EMPTY, BUTTON))
        .thenExecute(() ->
            helper.assertTrue(button.button().remaining() < ButtonTime.PLACED_TICKS, STARTED_OVER))
        .thenSucceed();
  }

  /** Another player cannot press it, by hand or with an item (answers 577 and 588). */
  @GameTest(template = EMPTY)
  public static void aStrangerDoesNotPressTheButton(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    ButtonPart button = ButtonScene.place(helper, owner, STONE);
    ServerPlayer stranger = helper.makeMockServerPlayerInLevel();

    LeverScene.click(helper, stranger, ItemStack.EMPTY, BUTTON);
    LeverScene.click(helper, stranger, new ItemStack(Items.STICK), BUTTON);

    helper.assertFalse(button.button().on(), STRANGER);
    helper.succeed();
  }

  /** Pressed, its front edge feeds the cable in front, which reaches the plate (566 and 575). */
  @GameTest(template = EMPTY)
  public static void theButtonFeedsTheCableInFront(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    helper.setBlock(BESIDE_FLOOR, Blocks.STONE);
    CableScene.place(helper, owner, BESIDE_FLOOR, Direction.UP, ChannelColor.RED);
    ButtonScene.place(helper, owner, STONE);

    LeverScene.click(helper, owner, ItemStack.EMPTY, BUTTON);

    helper.succeedWhen(() -> {
      CableScene.assertFloor(helper, LAMP_IN_FRONT, Signal.MAX.strength());
      CablePart cable = CableScene.at(helper, LAMP_IN_FRONT, Direction.DOWN);
      helper.assertValueEqual(
          Optional.ofNullable(cable.arms().get(Direction.SOUTH)),
          Optional.of(CableReach.PLATE),
          NOT_PLATE);
    });
  }

  /** A press under way, its strength, its on time and its output come back whole (591). */
  @GameTest(template = EMPTY)
  public static void aButtonKeepsItsSaveWhole(GameTestHelper helper) {
    CompoundTag saved = fullSave(SEVEN, ButtonTime.PLACED_TICKS, FIVE);
    ButtonPart loaded = ButtonPart.load(Direction.DOWN, saved);

    helper.assertValueEqual(loaded.save(), saved, NOT_KEPT);
    helper.assertValueEqual(loaded.button().remaining(), FIVE, NOT_KEPT);
    helper.succeed();
  }

  /**
   * A damaged save goes back to safe values, with a warning: the strength to 15, the on time to 1
   * s, and a button on with no time left to off (rule 14).
   */
  @GameTest(template = EMPTY)
  public static void aDamagedSaveGoesBackToSafeValues(GameTestHelper helper) {
    SignalButton loaded = ButtonPart.load(Direction.DOWN, fullSave(TOO_STRONG, 1, 0)).button();

    helper.assertValueEqual(
        loaded,
        new SignalButton(false, Signal.MAX, new ButtonTime(ButtonTime.PLACED_TICKS), 0),
        NOT_SAFE);
    helper.succeed();
  }

  /** Loaded with an on time past the limits of the config, it goes to the nearest one (592). */
  @GameTest(template = EMPTY)
  public static void anOnTimePastTheConfigIsFitted(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    ButtonPart button = ButtonScene.place(helper, owner, STONE);
    button.setButton(new SignalButton(false, Signal.MAX, new ButtonTime(TOO_LONG), 0));

    button.loaded(
        PartSpaceBlockEntity.at(helper.getLevel(), helper.absolutePos(BUTTON)).orElseThrow());

    helper.assertValueEqual(button.button().time(), new ButtonTime(LONGEST), NOT_FITTED);
    helper.succeed();
  }

  /** The save of a button pressed, with a name, a network and its output on a channel. */
  private static CompoundTag fullSave(int strength, int interval, int remaining) {
    CompoundTag tag = new CompoundTag();
    tag.putUUID(Keys.MODULE_ID, MODULE);
    tag.putUUID(Keys.OWNER, OWNER);
    tag.putUUID(Keys.NETWORK, NETWORK);
    tag.putString(Keys.FRONT, Direction.NORTH.getSerializedName());
    tag.putBoolean(Keys.POWERED, true);
    tag.putBoolean(Keys.ON, true);
    tag.putInt(Keys.STRENGTH, strength);
    tag.putInt(Keys.INTERVAL, interval);
    tag.putInt(Keys.REMAINING, remaining);
    tag.putString(Keys.OUTPUT, RED);
    return DelayKeys.addTo(tag);
  }

  /** The keys of the save, written here again, so a renamed key fails the test (rule 15). */
  private static final class Keys {
    static final String FRONT = "front";
    static final String POWERED = "powered";
    static final String MODULE_ID = "module_id";
    static final String OWNER = "owner";
    static final String NETWORK = "network";
    static final String ON = "on";
    static final String STRENGTH = "strength";
    static final String INTERVAL = "interval";
    static final String REMAINING = "remaining";
    static final String OUTPUT = "output";

    private Keys() {}
  }
}
