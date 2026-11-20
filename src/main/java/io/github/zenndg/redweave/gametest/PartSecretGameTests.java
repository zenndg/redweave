package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.PartSpaceBlockEntity;
import io.github.zenndg.redweave.block.part.LockPart;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * What the block of a part space sends to the clients: the NBT of its parts without their secret
 * keys, which only the server and the save keep (answers 637, 896 and 899).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class PartSecretGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos LOCK = STONE.above();
  private static final int SEVEN = 7;

  /** The keys of answers 543 and 710, written here again, so a renamed key fails the test. */
  private static final String PARTS = "parts";

  private static final String DATA = "data";
  private static final String KEY = "key";
  private static final String STRENGTH = "strength";
  private static final String SENT = "The key of the lock should never reach a client";
  private static final String NOT_SAVED = "The key of the lock should stay in the save";
  private static final String MISSING = "The client should still get the public keys of the lock";

  private PartSecretGameTests() {}

  /** The key of the Signal Lock stays in the save and out of the packet (answers 637 and 899). */
  @GameTest(template = EMPTY)
  public static void theKeyOfALockNeverReachesAClient(GameTestHelper helper) {
    LockPart lock = LockScene.place(helper, helper.makeMockPlayer(GameType.SURVIVAL), STONE);
    lock.setLock(lock.lock().withKey(SEVEN));
    PartSpaceBlockEntity space =
        PartSpaceBlockEntity.at(helper.getLevel(), helper.absolutePos(LOCK)).orElseThrow();

    CompoundTag sent = dataOf(space.getUpdateTag(helper.getLevel().registryAccess()));
    CompoundTag saved = dataOf(space.saveWithoutMetadata(helper.getLevel().registryAccess()));

    helper.assertFalse(sent.contains(KEY), SENT);
    helper.assertTrue(sent.contains(STRENGTH), MISSING);
    helper.assertValueEqual(saved.getInt(KEY), SEVEN, NOT_SAVED);
    helper.succeed();
  }

  /** The `data` of the only part of the space. */
  private static CompoundTag dataOf(CompoundTag space) {
    return space.getList(PARTS, Tag.TAG_COMPOUND).getCompound(0).getCompound(DATA);
  }
}
