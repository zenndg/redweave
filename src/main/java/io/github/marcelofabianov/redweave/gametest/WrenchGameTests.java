package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.item.ConfiguratorMode;
import io.github.marcelofabianov.redweave.registry.RedweaveBlocks;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/** The Wrench mode of the Configurator: configurador.md (answers 161 to 165, 191 and 193). */
@GameTestHolder(RedweaveMod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class WrenchGameTests {
  private static final String EMPTY = "empty";
  private static final BlockPos CONTROLLER_FLOOR = new BlockPos(0, 0, 2);
  private static final BlockPos CONTROLLER = CONTROLLER_FLOOR.above();
  private static final BlockPos STONE = new BlockPos(0, 1, 0);
  private static final BlockPos NODE = STONE.east();
  private static final double DROP_REACH = 3;
  private static final String NOT_PICKED = "The owner should pick the node up";
  private static final String PICKED = "Another player should not pick the node up";
  private static final String NOT_IN_INVENTORY = "The node should go to the inventory";
  private static final String NOT_DROPPED = "With a full inventory, the node should drop";
  private static final String STILL_IN_NETWORK = "The node should leave its network";

  private WrenchGameTests() {}

  @GameTest(template = EMPTY)
  public static void theOwnerPicksTheNodeUpIntoTheInventory(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    var node = NodeScene.placeNode(helper, owner, STONE, Direction.EAST);
    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.WRENCH);

    NodeScene.click(helper, owner, NODE);

    helper.assertBlockNotPresent(RedweaveBlocks.SIGNAL_NODE.get(), NODE);
    helper.assertTrue(
        owner.getInventory().contains(new ItemStack(RedweaveItems.SIGNAL_NODE.get())),
        NOT_IN_INVENTORY);
    helper.assertTrue(
        RedweaveServer.running().orElseThrow().networkOfNode(node).isEmpty(), STILL_IN_NETWORK);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void aFullInventoryDropsTheNode(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeScene.placeNode(helper, owner, STONE, Direction.EAST);
    ConfiguratorGameTests.holdConfigurator(owner, ConfiguratorMode.WRENCH);
    fillInventory(owner);

    NodeScene.click(helper, owner, NODE);

    helper.assertBlockNotPresent(RedweaveBlocks.SIGNAL_NODE.get(), NODE);
    helper.assertFalse(
        helper
            .getLevel()
            .getEntities(
                EntityType.ITEM,
                owner.getBoundingBox().inflate(DROP_REACH),
                item -> item.getItem().is(RedweaveItems.SIGNAL_NODE.get()))
            .isEmpty(),
        NOT_DROPPED);
    helper.succeed();
  }

  @GameTest(template = EMPTY)
  public static void anotherPlayerDoesNotPickTheNodeUp(GameTestHelper helper) {
    Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
    Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeController(helper, owner, CONTROLLER_FLOOR, CONTROLLER);
    NodeScene.placeNode(helper, owner, STONE, Direction.EAST);
    ConfiguratorGameTests.holdConfigurator(stranger, ConfiguratorMode.WRENCH);

    NodeScene.click(helper, stranger, NODE);

    helper.assertBlockPresent(RedweaveBlocks.SIGNAL_NODE.get(), NODE);
    helper.succeed();
  }

  /** Without a network, only who placed the node picks it up (answers 162 and 165). */
  @GameTest(template = EMPTY)
  public static void onlyWhoPlacedANodeWithoutANetworkPicksItUp(GameTestHelper helper) {
    Player placer = helper.makeMockPlayer(GameType.SURVIVAL);
    Player stranger = helper.makeMockPlayer(GameType.SURVIVAL);
    NodeScene.placeNode(helper, placer, STONE, Direction.EAST);
    ConfiguratorGameTests.holdConfigurator(stranger, ConfiguratorMode.WRENCH);
    ConfiguratorGameTests.holdConfigurator(placer, ConfiguratorMode.WRENCH);

    NodeScene.click(helper, stranger, NODE);
    helper.assertBlockPresent(RedweaveBlocks.SIGNAL_NODE.get(), NODE);
    NodeScene.click(helper, placer, NODE);
    helper.assertBlockNotPresent(RedweaveBlocks.SIGNAL_NODE.get(), NODE);
    helper.assertFalse(
        stranger.getInventory().contains(new ItemStack(RedweaveItems.SIGNAL_NODE.get())), PICKED);
    helper.assertTrue(
        placer.getInventory().contains(new ItemStack(RedweaveItems.SIGNAL_NODE.get())), NOT_PICKED);
    helper.succeed();
  }

  /** Every slot but the main hand, where the Configurator is, so nothing else fits. */
  private static void fillInventory(Player player) {
    var inventory = player.getInventory();
    for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
      if (slot != inventory.selected) {
        inventory.setItem(slot, new ItemStack(Items.STONE, Items.STONE.getDefaultMaxStackSize()));
      }
    }
  }
}
