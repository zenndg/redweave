package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.block.NetworkControllerBlockEntity;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.menu.NodeMenu;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * Only an empty hand opens the Node Screen or shows the click of the controller, and a click with
 * an item goes to the item, through the same path as a player (answer 113).
 */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class HandClickGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos OTHER_CONTROLLER_FLOOR = new BlockPos(2, 0, 2);
  private static final BlockPos OTHER_CONTROLLER = OTHER_CONTROLLER_FLOOR.above();
  private static final BlockPos STONE = new BlockPos(0, 1, 0);
  private static final BlockPos NODE = STONE.east();
  private static final String OPENED = "A click with an item should not open the Node Screen";
  private static final String OPENED_BY_HAND = "An empty hand should not open the Node Screen";
  private static final String CHANGED = "A click with an item should keep the current network";
  private static final String NOT_CHANGED = "A click with an empty hand should choose the network";

  private HandClickGameTests() {}

  /**
   * The mock player cannot receive the screen, so the test checks that the node did not take the
   * click: with the Node Screen it would be consumed (answer 75).
   */
  @GameTest(template = EMPTY)
  public static void aClickWithAnItemDoesNotOpenTheNodeScreen(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    helper.setBlock(STONE, Blocks.STONE);
    GameTestActions.placeOn(
        helper, owner, new ItemStack(RedweaveItems.SIGNAL_NODE.get()), STONE, Direction.EAST);

    InteractionResult result = click(helper, owner, new ItemStack(Items.STICK), NODE);

    helper.assertFalse(result.consumesAction(), OPENED);
    helper.assertFalse(owner.containerMenu instanceof NodeMenu, OPENED);
    helper.succeed();
  }

  /** Only the Configurator in the Open mode opens the node (answers 154 and 188). */
  @GameTest(template = EMPTY)
  public static void anEmptyHandDoesNotOpenTheNodeScreen(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    helper.setBlock(STONE, Blocks.STONE);
    GameTestActions.placeOn(
        helper, owner, new ItemStack(RedweaveItems.SIGNAL_NODE.get()), STONE, Direction.EAST);

    click(helper, owner, ItemStack.EMPTY, NODE);

    helper.assertFalse(owner.containerMenu instanceof NodeMenu, OPENED_BY_HAND);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void onlyAnEmptyHandChoosesTheNetworkOfAController(GameTestHelper helper) {
    ServerPlayer owner = helper.makeMockServerPlayerInLevel();
    NetworkId first = placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NetworkId second = placeController(helper, owner, OTHER_CONTROLLER_FLOOR, OTHER_CONTROLLER);

    click(helper, owner, new ItemStack(Items.STICK), CONTROLLER);
    helper.assertTrue(currentOf(owner).equals(Optional.of(second)), CHANGED);
    click(helper, owner, ItemStack.EMPTY, CONTROLLER);
    helper.assertTrue(currentOf(owner).equals(Optional.of(first)), NOT_CHANGED);
    helper.succeed();
  }

  /** A right-click on the block, as the game handles the packet of a player. */
  private static InteractionResult click(
      GameTestHelper helper, ServerPlayer player, ItemStack stack, BlockPos relative) {
    player.setItemInHand(InteractionHand.MAIN_HAND, stack);
    BlockPos pos = helper.absolutePos(relative);
    BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
    return player.gameMode.useItemOn(
        player, helper.getLevel(), stack, InteractionHand.MAIN_HAND, hit);
  }

  private static NetworkId placeController(
      GameTestHelper helper, ServerPlayer owner, BlockPos floor, BlockPos controller) {
    ItemStack item = new ItemStack(RedweaveItems.NETWORK_CONTROLLER.get());
    GameTestActions.placeOn(helper, owner, item, floor, Direction.UP);
    NetworkControllerBlockEntity block = helper.getBlockEntity(controller);
    return block.network().orElseThrow();
  }

  private static Optional<NetworkId> currentOf(ServerPlayer player) {
    return RedweaveServer.running().orElseThrow().currentOf(new PlayerId(player.getUUID()));
  }
}
