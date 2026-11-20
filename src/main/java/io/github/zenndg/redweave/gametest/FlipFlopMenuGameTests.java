package io.github.zenndg.redweave.gametest;

import io.github.zenndg.redweave.RedweaveMod;
import io.github.zenndg.redweave.block.part.FlipFlopPart;
import io.github.zenndg.redweave.domain.signal.Signal;
import io.github.zenndg.redweave.menu.FlipFlopAction;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Signal Flip-Flop Screen on the server: the owner steps `Strength` and `Delay`, and anyone
 * else changes nothing (answers 378, 830, 880 and 884, rules 16 and 17).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FlipFlopMenuGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos STONE = new BlockPos(1, 1, 1);
  private static final BlockPos FLIP_FLOP = STONE.above();
  private static final Signal FOURTEEN = new Signal(14);
  private static final int ONE_SECOND = 1;
  private static final String NOT_WEAKER = "The owner should step the strength down";
  private static final String NOT_LONGER = "The owner should step the delay up";
  private static final String STRANGER = "Another player should change nothing";

  private FlipFlopMenuGameTests() {}

  /** The owner steps the strength and the delay of the flip-flop (answers 830 and 880). */
  @GameTest(template = EMPTY)
  public static void theOwnerStepsTheStrengthAndTheDelay(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    FlipFlopPart flipFlop = FlipFlopScene.place(helper, owner, STONE);

    FlipFlopScene.click(helper, owner, FLIP_FLOP, FlipFlopAction.strength(false));
    FlipFlopScene.click(helper, owner, FLIP_FLOP, FlipFlopAction.delay(true));

    helper.assertValueEqual(flipFlop.flipFlop().strength(), FOURTEEN, NOT_WEAKER);
    helper.assertValueEqual(flipFlop.delayed().delay().seconds(), ONE_SECOND, NOT_LONGER);
    helper.succeed();
  }

  /** Another player changes nothing on the screen (answer 378). */
  @GameTest(template = EMPTY)
  public static void aStrangerChangesNothing(GameTestHelper helper) {
    FlipFlopPart flipFlop =
        FlipFlopScene.place(helper, helper.makeMockPlayer(GameType.SURVIVAL), STONE);
    Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);

    FlipFlopScene.click(helper, stranger, FLIP_FLOP, FlipFlopAction.strength(false));
    FlipFlopScene.click(helper, stranger, FLIP_FLOP, FlipFlopAction.delay(true));

    helper.assertValueEqual(flipFlop.flipFlop().strength(), Signal.MAX, STRANGER);
    helper.assertValueEqual(flipFlop.delayed().delay().seconds(), 0, STRANGER);
    helper.succeed();
  }
}
