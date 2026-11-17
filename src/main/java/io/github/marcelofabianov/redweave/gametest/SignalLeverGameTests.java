package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.part.CablePart;
import io.github.marcelofabianov.redweave.block.part.CableReach;
import io.github.marcelofabianov.redweave.block.part.LeverPart;
import io.github.marcelofabianov.redweave.domain.module.Connection;
import io.github.marcelofabianov.redweave.domain.module.SignalLever;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
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
 * The Signal Lever in the world: _docs/funcionalidades/alavanca.md, the hand of its owner, its
 * edges, its save and the cable in front (answers 553, 566, 575, 576, 577 and 579).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SignalLeverGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos LEVER = STONE.above();
  private static final BlockPos LAMP_IN_FRONT = LEVER.north();
  private static final BlockPos LAMP_BY_SUPPORT = STONE.east();
  private static final BlockPos BESIDE = LEVER.north();
  private static final BlockPos BESIDE_FLOOR = BESIDE.below();
  private static final int SEVEN = 7;
  private static final int TOO_STRONG = 40;
  private static final String RED = "red";
  private static final UUID MODULE = UUID.fromString("5d6e7f8a-9b0c-4d1e-8f2a-3b4c5d6e7f8a");
  private static final UUID OWNER = UUID.fromString("6e7f8a9b-0c1d-4e2f-9a3b-4c5d6e7f8a9b");
  private static final UUID NETWORK = UUID.fromString("7f8a9b0c-1d2e-4f3a-8b4c-5d6e7f8a9b0c");
  private static final String NOT_PLACED = "A new lever should be off, at 15, on its edge";
  private static final String NOT_OWNER = "Whoever places the lever should own it";
  private static final String NOT_ON = "The hand of the owner should turn the lever on";
  private static final String NOT_OFF = "A second click should turn it off";
  private static final String STRANGER = "Another player should not turn the lever";
  private static final String NOT_KEPT = "The save of the lever should come back whole";
  private static final String NOT_SAFE = "A damaged strength should go back to 15";
  private static final String NOT_PLATE = "The cable in front should reach the plate";

  private SignalLeverGameTests() {}

  /** Placed, it is off, at 15, on its edge, and owned by whoever placed it (552). */
  @GameTest(template = EMPTY)
  public static void aPlacedLeverIsOff(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    LeverPart lever = LeverScene.place(helper, owner, STONE);

    helper.assertValueEqual(lever.lever(), SignalLever.placed(), NOT_PLACED);
    helper.assertValueEqual(lever.output(), Connection.EDGE, NOT_PLACED);
    helper.assertTrue(
        lever.module().orElseThrow().isOwner(new PlayerId(owner.getUUID())), NOT_OWNER);
    helper.succeed();
  }

  /**
   * The empty hand of the owner turns it on, which feeds the block in front and its block of
   * support, and off again (answers 553 and 576).
   */
  @GameTest(template = EMPTY)
  public static void theHandOfTheOwnerTurnsTheLeverOnAndOff(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    LeverPart lever = LeverScene.place(helper, owner, STONE);
    helper.setBlock(LAMP_IN_FRONT, Blocks.REDSTONE_LAMP);
    helper.setBlock(LAMP_BY_SUPPORT, Blocks.REDSTONE_LAMP);

    LeverScene.click(helper, owner, ItemStack.EMPTY, LEVER);
    helper.assertTrue(lever.lever().on(), NOT_ON);

    helper
        .startSequence()
        .thenWaitUntil(() -> {
          helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, true);
          helper.assertBlockProperty(LAMP_BY_SUPPORT, RedstoneLampBlock.LIT, true);
        })
        .thenExecute(() -> LeverScene.click(helper, owner, ItemStack.EMPTY, LEVER))
        .thenExecute(() -> helper.assertFalse(lever.lever().on(), NOT_OFF))
        .thenWaitUntil(() -> {
          helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, false);
          helper.assertBlockProperty(LAMP_BY_SUPPORT, RedstoneLampBlock.LIT, false);
        })
        .thenSucceed();
  }

  /** Any item but the Configurator turns it, as the lever of the game (answer 576). */
  @GameTest(template = EMPTY)
  public static void anItemInTheHandTurnsTheLever(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    LeverPart lever = LeverScene.place(helper, owner, STONE);

    LeverScene.click(helper, owner, new ItemStack(Items.STICK), LEVER);

    helper.assertTrue(lever.lever().on(), NOT_ON);
    helper.succeed();
  }

  /** Another player cannot turn it, by hand or with an item (answer 577). */
  @GameTest(template = EMPTY)
  public static void aStrangerDoesNotTurnTheLever(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    LeverPart lever = LeverScene.place(helper, owner, STONE);
    ServerPlayer stranger = helper.makeMockServerPlayerInLevel();

    LeverScene.click(helper, stranger, ItemStack.EMPTY, LEVER);
    LeverScene.click(helper, stranger, new ItemStack(Items.STICK), LEVER);

    helper.assertFalse(lever.lever().on(), STRANGER);
    helper.succeed();
  }

  /** On, its front edge feeds the cable in front, which reaches the plate (566 and 575). */
  @GameTest(template = EMPTY)
  public static void theLeverFeedsTheCableInFront(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    helper.setBlock(BESIDE_FLOOR, Blocks.STONE);
    CableScene.place(helper, owner, BESIDE_FLOOR, Direction.UP, ChannelColor.RED);
    LeverScene.place(helper, owner, STONE);

    LeverScene.click(helper, owner, ItemStack.EMPTY, LEVER);

    helper.succeedWhen(() -> {
      CableScene.assertFloor(helper, BESIDE, Signal.MAX.strength());
      CablePart cable = CableScene.at(helper, BESIDE, Direction.DOWN);
      helper.assertValueEqual(
          Optional.ofNullable(cable.arms().get(Direction.SOUTH)),
          Optional.of(CableReach.PLATE),
          NOT_PLATE);
    });
  }

  /** Whether it is on, its strength and its output come back whole (answer 579). */
  @GameTest(template = EMPTY)
  public static void aLeverKeepsItsSaveWhole(GameTestHelper helper) {
    CompoundTag saved = fullSave(SEVEN);

    helper.assertValueEqual(LeverPart.load(Direction.DOWN, saved).save(), saved, NOT_KEPT);
    helper.succeed();
  }

  /** A strength out of range goes back to 15, with a warning (rule 14). */
  @GameTest(template = EMPTY)
  public static void aDamagedStrengthGoesBackToFifteen(GameTestHelper helper) {
    LeverPart loaded = LeverPart.load(Direction.DOWN, fullSave(TOO_STRONG));

    helper.assertValueEqual(loaded.lever().strength(), Signal.MAX, NOT_SAFE);
    helper.assertTrue(loaded.lever().on(), NOT_SAFE);
    helper.succeed();
  }

  /** The save of a lever turned on, with a name, a network and its output on a channel. */
  private static CompoundTag fullSave(int strength) {
    CompoundTag tag = new CompoundTag();
    tag.putUUID(Keys.MODULE_ID, MODULE);
    tag.putUUID(Keys.OWNER, OWNER);
    tag.putUUID(Keys.NETWORK, NETWORK);
    tag.putString(Keys.FRONT, Direction.NORTH.getSerializedName());
    tag.putBoolean(Keys.POWERED, true);
    tag.putBoolean(Keys.ON, true);
    tag.putInt(Keys.STRENGTH, strength);
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
    static final String OUTPUT = "output";

    private Keys() {}
  }
}
