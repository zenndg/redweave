package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.PartSpaceBlockEntity;
import io.github.zenndg.redweave.block.part.LockPart;
import io.github.zenndg.redweave.config.NeoForgeLockLimits;
import io.github.zenndg.redweave.domain.module.Connection;
import io.github.zenndg.redweave.domain.module.LockPulse;
import io.github.zenndg.redweave.domain.module.SignalLock;
import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Signal Lock in the world: _docs/funcionalidades/fechadura.md. Only the exact key opens it, it
 * gives its strength in front, keeps its save, and resists explosions with its block of support
 * (answers 633 to 636 and 641).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class SignalLockGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos LOCK = STONE.above();
  private static final BlockPos LAMP_IN_FRONT = LOCK.north();
  private static final BlockPos BEHIND = LOCK.south();
  private static final int FOURTEEN = 14;
  private static final int SIX = 6;
  private static final int NINE = 9;
  private static final int TEN = 10;
  private static final int TOO_STRONG = 40;
  private static final float EXPLOSION_POWER = 4;
  private static final double ABOVE = 0.5;
  private static final String RED = "red";
  private static final UUID MODULE = UUID.fromString("1d2e3f4a-5b6c-4d7e-8f9a-0b1c2d3e4f5a");
  private static final UUID OWNER = UUID.fromString("2e3f4a5b-6c7d-4e8f-9a0b-1c2d3e4f5a6b");
  private static final String NOT_PLACED = "A new lock should be locked, with key 15 and 1 s";
  private static final String WRONG_KEY = "Only the exact key should open the lock";
  private static final String WRONG_STRENGTH = "An open lock should give its strength in front";
  private static final String NOT_KEPT = "The save of the lock should come back whole";
  private static final String NOT_SAFE = "A damaged save should go back to safe values";
  private static final String BLOWN = "An explosion should not take the lock nor its support";

  private SignalLockGameTests() {}

  /** Placed, it is locked, with key 15, giving 15, a pulse of 1 s, on its edges (636, 640). */
  @GameTest(template = EMPTY)
  public static void aPlacedLockIsLocked(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    LockPart lock = LockScene.place(helper, owner, STONE);

    helper.assertValueEqual(lock.lock().key(), Signal.MAX, NOT_PLACED);
    helper.assertValueEqual(lock.lock().strength(), Signal.MAX, NOT_PLACED);
    helper.assertValueEqual(lock.lock().pulse(), new LockPulse(LockPulse.PLACED_TICKS), NOT_PLACED);
    helper.assertValueEqual(lock.input(), Connection.EDGE, NOT_PLACED);
    helper.assertValueEqual(lock.output(), Connection.EDGE, NOT_PLACED);
    helper.assertFalse(lock.powered(), NOT_PLACED);
    helper.succeed();
  }

  /**
   * A block of redstone behind gives 15: a key of 14 keeps it locked, and the key of 15 opens it,
   * which lights the lamp in front with its strength (answers 634 and 635).
   */
  @GameTest(template = EMPTY)
  public static void onlyTheExactKeyOpensIt(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    LockPart lock = LockScene.place(helper, owner, STONE);
    lock.setLock(lock.lock().withKey(FOURTEEN).withStrength(SIX));
    helper.setBlock(LAMP_IN_FRONT, Blocks.REDSTONE_LAMP);
    helper.setBlock(BEHIND, Blocks.REDSTONE_BLOCK);

    helper
        .startSequence()
        .thenIdle(TEN)
        .thenExecute(() -> {
          helper.assertFalse(lock.powered(), WRONG_KEY);
          helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, false);
          lock.setLock(lock.lock().withKey(Signal.MAX.strength()));
        })
        .thenWaitUntil(() -> {
          helper.assertTrue(lock.powered(), WRONG_KEY);
          helper.assertBlockProperty(LAMP_IN_FRONT, RedstoneLampBlock.LIT, true);
          helper.assertValueEqual(
              helper.getLevel().getSignal(helper.absolutePos(LOCK), Direction.SOUTH),
              SIX,
              WRONG_STRENGTH);
        })
        .thenSucceed();
  }

  /** Its key, its strength, its pulse, a pulse under way and its ends come back whole (rule 15). */
  @GameTest(template = EMPTY)
  public static void aLockKeepsItsSaveWhole(GameTestHelper helper) {
    CompoundTag saved = fullSave(NINE, SIX, TEN);
    LockPart loaded = LockPart.load(Direction.DOWN, saved);

    helper.assertValueEqual(loaded.save(), saved, NOT_KEPT);
    helper.assertValueEqual(loaded.lock().key(), new Signal(NINE), NOT_KEPT);
    helper.succeed();
  }

  /** A damaged save goes back to safe values: the key to 15, the pulse to 1 s (rule 14). */
  @GameTest(template = EMPTY)
  public static void aDamagedSaveGoesBackToSafeValues(GameTestHelper helper) {
    SignalLock loaded = LockPart.load(Direction.DOWN, fullSave(TOO_STRONG, 0, -1)).lock();

    helper.assertValueEqual(
        loaded,
        new SignalLock(Signal.MAX, new Signal(SIX), new LockPulse(LockPulse.PLACED_TICKS), 0),
        NOT_SAFE);
    helper.succeed();
  }

  /** An explosion right on it takes neither the lock nor the stone it rests on (answer 641). */
  @GameTest(template = EMPTY)
  public static void anExplosionTakesNeitherTheLockNorItsSupport(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    LockScene.place(helper, owner, STONE);
    Vec3 center = Vec3.atCenterOf(helper.absolutePos(LOCK));

    helper
        .getLevel()
        .explode(
            null,
            center.x,
            center.y + ABOVE,
            center.z,
            EXPLOSION_POWER,
            Level.ExplosionInteraction.TNT);

    helper.assertBlockPresent(Blocks.STONE, STONE);
    helper.assertTrue(
        PartSpaceBlockEntity.at(helper.getLevel(), helper.absolutePos(LOCK))
            .flatMap(space -> space.at(Direction.DOWN))
            .filter(LockPart.class::isInstance)
            .isPresent(),
        BLOWN);
    helper.succeed();
  }

  /** A pulse outside the config of now is fitted to it when the lock loads (answer 640). */
  @GameTest(template = EMPTY)
  public static void aPulsePastTheConfigIsFitted(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    LockPart lock = LockScene.place(helper, owner, STONE);
    lock.setLock(new SignalLock(Signal.MAX, Signal.MAX, new LockPulse(TOO_STRONG * TEN), 0));

    lock.loaded(PartSpaceBlockEntity.at(helper.getLevel(), helper.absolutePos(LOCK)).orElseThrow());

    helper.assertTrue(lock.lock().fits(new NeoForgeLockLimits()), NOT_SAFE);
    helper.succeed();
  }

  /** The save of a lock with a name, a network and its output on a channel. */
  private static CompoundTag fullSave(int key, int pulse, int remaining) {
    CompoundTag tag = new CompoundTag();
    tag.putUUID(Keys.MODULE_ID, MODULE);
    tag.putUUID(Keys.OWNER, OWNER);
    tag.putString(Keys.FRONT, Direction.NORTH.getSerializedName());
    tag.putBoolean(Keys.POWERED, false);
    tag.putInt(Keys.KEY, key);
    tag.putInt(Keys.STRENGTH, SIX);
    tag.putInt(Keys.PULSE, pulse);
    tag.putInt(Keys.REMAINING, remaining);
    tag.putString(Keys.INPUT, Keys.EDGE);
    tag.putString(Keys.OUTPUT, RED);
    return tag;
  }

  /** The keys of the save, written here again, so a renamed key fails the test (rule 15). */
  private static final class Keys {
    static final String FRONT = "front";
    static final String POWERED = "powered";
    static final String MODULE_ID = "module_id";
    static final String OWNER = "owner";
    static final String KEY = "key";
    static final String STRENGTH = "strength";
    static final String PULSE = "pulse";
    static final String REMAINING = "remaining";
    static final String INPUT = "input";
    static final String OUTPUT = "output";
    static final String EDGE = "edge";

    private Keys() {}
  }
}
