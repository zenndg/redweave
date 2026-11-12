package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.application.TerminalCheck;
import io.github.marcelofabianov.redweave.block.BlockLocations;
import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * The Network Terminal block in the world: one per player, broken only by its owner, and not
 * destroyed by explosions (terminal-de-rede.md, answers 137, 175, 253 and 257).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NetworkTerminalGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos FLOOR = new BlockPos(1, 0, 1);
  private static final BlockPos TERMINAL = FLOOR.above();
  private static final BlockPos OTHER_FLOOR = new BlockPos(0, 0, 0);
  private static final BlockPos OTHER_TERMINAL = OTHER_FLOOR.above();
  private static final BlockPos NOTHING_THERE = new BlockPos(2, 1, 2);
  private static final float EXPLOSION_POWER = 4.0F;
  private static final double CENTER = 0.5;
  private static final String NOT_KEPT = "The saves should keep the terminal of its owner";
  private static final String SECOND = "A player should have only one terminal";
  private static final String STRANGER_BREAKS = "Only the owner should break the terminal";
  private static final String NOT_FREED = "A broken terminal should free its owner";
  private static final String NOT_REPLACED = "A terminal gone should be replaced";

  private NetworkTerminalGameTests() {}

  @GameTest(template = EMPTY)
  public static void placingATerminalKeepsItForItsOwner(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);

    place(helper, owner, FLOOR);

    helper.assertBlockPresent(RedweaveBlocks.NETWORK_TERMINAL.get(), TERMINAL);
    helper.assertTrue(terminalOf(owner).equals(Optional.of(at(helper, TERMINAL))), NOT_KEPT);
    helper.succeed();
  }

  /** One per player on the server (answer 137). */
  @GameTest(template = EMPTY)
  public static void aSecondTerminalIsRefused(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner, FLOOR);

    place(helper, owner, OTHER_FLOOR);

    helper.assertBlockNotPresent(RedweaveBlocks.NETWORK_TERMINAL.get(), OTHER_TERMINAL);
    helper.assertTrue(terminalOf(owner).equals(Optional.of(at(helper, TERMINAL))), SECOND);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void anotherPlayerCannotBreakTheTerminal(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner, FLOOR);

    helper.assertFalse(GameTestActions.breakBy(helper, stranger, TERMINAL), STRANGER_BREAKS);

    helper.assertBlockPresent(RedweaveBlocks.NETWORK_TERMINAL.get(), TERMINAL);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void theOwnerBreaksTheTerminalAndPlacesAnother(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner, FLOOR);

    helper.assertTrue(GameTestActions.breakBy(helper, owner, TERMINAL), STRANGER_BREAKS);
    place(helper, owner, OTHER_FLOOR);

    helper.assertBlockPresent(RedweaveBlocks.NETWORK_TERMINAL.get(), OTHER_TERMINAL);
    helper.assertTrue(terminalOf(owner).equals(Optional.of(at(helper, OTHER_TERMINAL))), NOT_FREED);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void anExplosionDoesNotDestroyTheTerminal(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    place(helper, owner, FLOOR);
    Vec3 center = Vec3.atCenterOf(helper.absolutePos(TERMINAL));

    helper
        .getLevel()
        .explode(
            null,
            center.x,
            center.y + CENTER,
            center.z,
            EXPLOSION_POWER,
            Level.ExplosionInteraction.TNT);

    helper.assertBlockPresent(RedweaveBlocks.NETWORK_TERMINAL.get(), TERMINAL);
    helper.succeed();
  }

  /**
   * The saves point to a terminal that is no longer there, in a loaded chunk, as after an edit of
   * the world: the next one takes its place (answer 253).
   */
  @GameTest(template = EMPTY)
  public static void aTerminalGoneFromALoadedChunkIsReplaced(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    server()
        .terminal()
        .placeTerminal()
        .place(
            new PlayerId(owner.getUUID()),
            at(helper, NOTHING_THERE),
            location -> TerminalCheck.STANDING);

    place(helper, owner, FLOOR);

    helper.assertBlockPresent(RedweaveBlocks.NETWORK_TERMINAL.get(), TERMINAL);
    helper.assertTrue(terminalOf(owner).equals(Optional.of(at(helper, TERMINAL))), NOT_REPLACED);
    helper.succeed();
  }

  private static void place(GameTestHelper helper, Player player, BlockPos floor) {
    ItemStack item = new ItemStack(RedweaveItems.NETWORK_TERMINAL.get());
    GameTestActions.placeOn(helper, player, item, floor, Direction.UP);
  }

  private static Optional<BlockLocation> terminalOf(Player player) {
    return server().terminal().terminalOf(new PlayerId(player.getUUID()));
  }

  private static BlockLocation at(GameTestHelper helper, BlockPos relative) {
    return BlockLocations.of(helper.getLevel(), helper.absolutePos(relative));
  }

  private static RedweaveServer server() {
    return RedweaveServer.running().orElseThrow();
  }
}
