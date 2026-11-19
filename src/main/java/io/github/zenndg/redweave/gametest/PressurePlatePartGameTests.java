package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.PartSpaceBlockEntity;
import io.github.zenndg.redweave.block.part.CablePart;
import io.github.zenndg.redweave.block.part.CableReach;
import io.github.zenndg.redweave.block.part.PressurePlatePart;
import io.github.zenndg.redweave.domain.module.Connection;
import io.github.zenndg.redweave.domain.module.PlateMode;
import io.github.zenndg.redweave.domain.module.SignalPressurePlate;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.domain.signal.Signal;
import io.github.zenndg.redweave.registry.RedweaveItems;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Signal Pressure Plate as a part: _docs/funcionalidades/placa-de-pressao.md. Placed off, at
 * 15, sensing players; only on the floor; the hand does nothing; its save; and the cable in front
 * (answers 551, 566, 575, 604, 606, 608, 611 and 621).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class PressurePlatePartGameTests {
  private static final String REMOTE_PULSE = "remote_pulse";
  private static final int PULSE_LEFT = 12;
  private static final int NEGATIVE_PULSE = -3;
  private static final int NO_PULSE = 0;
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos PLATE = STONE.above();
  private static final BlockPos BESIDE_FLOOR = PLATE.north().below();
  private static final BlockPos WALL_SPACE = STONE.north();
  private static final int SEVEN = 7;
  private static final int TOO_STRONG = 40;
  private static final String RED = "red";
  private static final String HOSTILE = "hostile";
  private static final String UNKNOWN_MODE = "mobs";
  private static final UUID MODULE = UUID.fromString("1d2e3f4a-5b6c-4d7e-8f9a-0b1c2d3e4f5a");
  private static final UUID OWNER = UUID.fromString("2e3f4a5b-6c7d-4e8f-9a0b-1c2d3e4f5a6b");
  private static final UUID NETWORK = UUID.fromString("3f4a5b6c-7d8e-4f9a-8b1c-2d3e4f5a6b7c");
  private static final String NOT_PLACED = "A new plate should be off, at 15, sensing players";
  private static final String NOT_OWNER = "Whoever places the plate should own it";
  private static final String ON_A_WALL = "The plate should only go on the floor";
  private static final String NOT_KEPT_ITEM = "A refused plate should stay in the hand";
  private static final String HAND = "The hand should do nothing on the plate";
  private static final String NOT_KEPT = "The save of the plate should come back whole";
  private static final String NOT_SAFE = "A damaged save should go back to safe values";
  private static final String NOT_PLATE = "The cable in front should reach the plate";

  private PressurePlatePartGameTests() {}

  /** Placed, it is off, at 15, sensing players, on its edge, owned by its placer (552, 604). */
  @GameTest(template = EMPTY)
  public static void aPlacedPlateIsOffSensingPlayers(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    PressurePlatePart plate = PressurePlateScene.place(helper, owner, STONE);

    helper.assertValueEqual(plate.plate(), SignalPressurePlate.placed(), NOT_PLACED);
    helper.assertValueEqual(plate.output(), Connection.EDGE, NOT_PLACED);
    helper.assertTrue(
        plate.module().orElseThrow().isOwner(new PlayerId(owner.getUUID())), NOT_OWNER);
    helper.succeed();
  }

  /** On a wall it is refused, and the item stays in the hand (answers 551 and 611). */
  @GameTest(template = EMPTY)
  public static void itOnlyGoesOnTheFloor(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    helper.setBlock(STONE, Blocks.STONE);
    ItemStack stack = new ItemStack(RedweaveItems.SIGNAL_PRESSURE_PLATE.get());

    GameTestActions.placeOn(helper, owner, stack, STONE, Direction.NORTH);

    helper.assertTrue(
        PartSpaceBlockEntity.at(helper.getLevel(), helper.absolutePos(WALL_SPACE)).isEmpty(),
        ON_A_WALL);
    helper.assertValueEqual(stack.getCount(), 1, NOT_KEPT_ITEM);
    helper.succeed();
  }

  /** The empty hand of the owner changes nothing (answer 606). */
  @GameTest(template = EMPTY)
  public static void theHandDoesNothing(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    PressurePlatePart plate = PressurePlateScene.place(helper, owner, STONE);

    LeverScene.click(helper, owner, ItemStack.EMPTY, PLATE);

    helper.assertValueEqual(plate.plate(), SignalPressurePlate.placed(), HAND);
    helper.succeed();
  }

  /** On, its front edge feeds the cable in front, which reaches the plate (566 and 575). */
  @GameTest(template = EMPTY)
  public static void thePlateFeedsTheCableInFront(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    helper.setBlock(BESIDE_FLOOR, Blocks.STONE);
    CableScene.place(helper, owner, BESIDE_FLOOR, Direction.UP, ChannelColor.RED);
    PressurePlateScene.place(helper, owner, STONE);

    PressurePlateScene.stepOn(helper, owner, PLATE);

    helper.succeedWhen(() -> {
      CableScene.assertFloor(helper, PLATE.north(), Signal.MAX.strength());
      CablePart cable = CableScene.at(helper, PLATE.north(), Direction.DOWN);
      helper.assertValueEqual(
          Optional.ofNullable(cable.arms().get(Direction.SOUTH)),
          Optional.of(CableReach.PLATE),
          NOT_PLATE);
    });
  }

  /** On, its strength, its mode and its output come back whole (answer 608). */
  @GameTest(template = EMPTY)
  public static void aPlateKeepsItsSaveWhole(GameTestHelper helper) {
    CompoundTag saved = fullSave(SEVEN, HOSTILE);
    PressurePlatePart loaded = PressurePlatePart.load(Direction.DOWN, saved);

    helper.assertValueEqual(loaded.save(), saved, NOT_KEPT);
    helper.assertValueEqual(
        loaded.plate(),
        new SignalPressurePlate(true, new Signal(SEVEN), PlateMode.HOSTILE),
        NOT_KEPT);
    helper.succeed();
  }

  /** A plate saved during the pulse of a Signal Remote comes back on and ends it (793). */
  @GameTest(template = EMPTY)
  public static void aPlateKeepsThePulseOfTheRemote(GameTestHelper helper) {
    CompoundTag saved = fullSave(SEVEN, HOSTILE);
    saved.putInt(REMOTE_PULSE, PULSE_LEFT);
    PressurePlatePart loaded = PressurePlatePart.load(Direction.DOWN, saved);

    helper.assertValueEqual(loaded.plate().pulse(), PULSE_LEFT, NOT_KEPT);
    helper.assertValueEqual(loaded.save(), saved, NOT_KEPT);
    helper.succeed();
  }

  /** A negative pulse goes back to none, with a warning (rule 14). */
  @GameTest(template = EMPTY)
  public static void aNegativePulseGoesBackToNone(GameTestHelper helper) {
    CompoundTag saved = fullSave(SEVEN, HOSTILE);
    saved.putInt(REMOTE_PULSE, NEGATIVE_PULSE);

    helper.assertValueEqual(
        PressurePlatePart.load(Direction.DOWN, saved).plate().pulse(), NO_PULSE, NOT_SAFE);
    helper.succeed();
  }

  /** A damaged save goes back to 15 and players, with a warning (rule 14). */
  @GameTest(template = EMPTY)
  public static void aDamagedSaveGoesBackToSafeValues(GameTestHelper helper) {
    SignalPressurePlate loaded =
        PressurePlatePart.load(Direction.DOWN, fullSave(TOO_STRONG, UNKNOWN_MODE)).plate();

    helper.assertValueEqual(
        loaded, new SignalPressurePlate(true, Signal.MAX, PlateMode.PLAYERS), NOT_SAFE);
    helper.succeed();
  }

  /** The save of a plate on, with a name, a network and its output on a channel. */
  private static CompoundTag fullSave(int strength, String mode) {
    CompoundTag tag = new CompoundTag();
    tag.putUUID(Keys.MODULE_ID, MODULE);
    tag.putUUID(Keys.OWNER, OWNER);
    tag.putUUID(Keys.NETWORK, NETWORK);
    tag.putString(Keys.FRONT, Direction.NORTH.getSerializedName());
    tag.putBoolean(Keys.POWERED, true);
    tag.putBoolean(Keys.ON, true);
    tag.putInt(Keys.STRENGTH, strength);
    tag.putString(Keys.MODE, mode);
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
    static final String MODE = "mode";
    static final String OUTPUT = "output";

    private Keys() {}
  }
}
