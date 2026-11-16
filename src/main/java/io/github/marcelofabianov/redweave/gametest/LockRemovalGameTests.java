package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.ModuleMessages;
import io.github.marcelofabianov.redweave.block.part.LockPart;
import io.github.marcelofabianov.redweave.item.ConfiguratorMode;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * How a Signal Lock comes out, and its `Toggle`: nobody breaks it by hand in survival, not even its
 * owner, who takes it out with the `Wrench`; in creative, anyone breaks it; the `Toggle` opens it
 * for its pulse (answers 640 and 641).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class LockRemovalGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos LOCK = STONE.above();
  private static final String BROKEN = "Nobody should break the lock by hand in survival";
  private static final String NOT_BROKEN = "A creative player should break the lock";
  private static final String NOT_PICKED = "The Wrench of the owner should take the lock out";
  private static final String NOT_OPEN = "The Toggle should open the lock for its pulse";
  private static final String NOT_CLOSED = "The lock should close after its pulse";
  private static final String WRONG_MESSAGE = "The Toggle should say the lock is unlocked";
  private static final String UNLOCKED = ModuleMessages.LOCK_UNLOCKED;

  private LockRemovalGameTests() {}

  /** In survival, neither a stranger nor its owner breaks it by hand (answer 641). */
  @GameTest(template = EMPTY)
  public static void nobodyBreaksTheLockByHandInSurvival(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);
    LockScene.place(helper, owner, STONE);

    helper.assertFalse(GameTestActions.breakBy(helper, stranger, LOCK), BROKEN);
    helper.assertFalse(GameTestActions.breakBy(helper, owner, LOCK), BROKEN);
    ModuleParts.assertPresent(helper, LOCK);
    helper.succeed();
  }

  /** In creative, anyone breaks it (answer 641). */
  @GameTest(template = EMPTY)
  public static void aCreativePlayerBreaksTheLock(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    Player creative = helper.makeMockPlayer(GameType.CREATIVE);
    LockScene.place(helper, owner, STONE);

    helper.assertTrue(GameTestActions.breakBy(helper, creative, LOCK), NOT_BROKEN);
    helper.assertBlockNotPresent(RedweaveBlocks.PART_SPACE.get(), LOCK);
    helper.succeed();
  }

  /** The `Wrench` of its owner takes it out to the inventory (answer 641). */
  @GameTest(template = EMPTY)
  public static void theWrenchOfTheOwnerTakesItOut(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    LockScene.place(helper, owner, STONE);
    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.WRENCH);

    NodeScene.click(helper, owner, LOCK);

    helper.assertBlockNotPresent(RedweaveBlocks.PART_SPACE.get(), LOCK);
    helper.assertTrue(
        owner.getInventory().contains(RedweaveItems.SIGNAL_LOCK.get().getDefaultInstance()),
        NOT_PICKED);
    helper.succeed();
  }

  /** The `Toggle` says so and opens it for its pulse, and then it closes (answer 640). */
  @GameTest(template = EMPTY)
  public static void theToggleOpensTheLockForItsPulse(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    LockPart lock = LockScene.place(helper, owner, STONE);

    Optional<Component> said = lock.toggle();

    helper.assertValueEqual(
        said.map(LockRemovalGameTests::keyOf), Optional.of(UNLOCKED), WRONG_MESSAGE);
    helper.assertTrue(lock.lock().pulsing(), NOT_OPEN);
    helper
        .startSequence()
        .thenWaitUntil(() -> helper.assertTrue(lock.powered(), NOT_OPEN))
        .thenWaitUntil(() -> helper.assertFalse(lock.powered(), NOT_CLOSED))
        .thenSucceed();
  }

  private static String keyOf(Component component) {
    return component.getContents() instanceof TranslatableContents translatable
        ? translatable.getKey()
        : component.getString();
  }
}
