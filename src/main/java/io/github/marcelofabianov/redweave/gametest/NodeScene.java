package io.github.marcelofabianov.redweave.gametest;

import com.mojang.authlib.GameProfile;
import io.github.marcelofabianov.redweave.block.NetworkControllerBlockEntity;
import io.github.marcelofabianov.redweave.block.SignalNodeBlockEntity;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.menu.NodeAction;
import io.github.marcelofabianov.redweave.menu.NodeMenu;
import io.github.marcelofabianov.redweave.menu.NodeScreenCodes;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

/** Controllers, nodes and clicks with an item, shared by the GameTests of the tools. */
final class NodeScene {
  static final int FIRST_CHOICE = 0;
  private static final int MENU_ID = 1;

  private NodeScene() {}

  static NetworkId placeController(
      GameTestHelper helper, Player owner, BlockPos floor, BlockPos controller) {
    ItemStack item = new ItemStack(RedweaveItems.NETWORK_CONTROLLER.get());
    GameTestActions.placeOn(helper, owner, item, floor, Direction.UP);
    NetworkControllerBlockEntity block = helper.getBlockEntity(controller);
    return block.network().orElseThrow();
  }

  /**
   * Places a node and picks the first network of the list, as the placer does on the Node Screen
   * (answers 139 and 171). With no network to pick, the node stays without one.
   */
  static NodeId placeNode(GameTestHelper helper, Player owner, BlockPos stone, Direction face) {
    NodeId node = placeLooseNode(helper, owner, stone, face);
    choose(helper, owner, stone.relative(face), FIRST_CHOICE);
    return node;
  }

  /** Only places the node, which is born with no network (answer 139). */
  static NodeId placeLooseNode(
      GameTestHelper helper, Player owner, BlockPos stone, Direction face) {
    helper.setBlock(stone, Blocks.STONE);
    ItemStack item = new ItemStack(RedweaveItems.SIGNAL_NODE.get());
    GameTestActions.placeOn(helper, owner, item, stone, face);
    SignalNodeBlockEntity node = helper.getBlockEntity(stone.relative(face));
    return node.node().orElseThrow();
  }

  /**
   * Picks a network on the Node Screen of a player standing next to the node: the menu keeps the
   * list, and the click sends only the position (answers 157 and 172).
   */
  static void choose(GameTestHelper helper, Player player, BlockPos node, int position) {
    ServerPlayer viewer =
        FakePlayerFactory.get(
            helper.getLevel(), new GameProfile(player.getUUID(), player.getScoreboardName()));
    viewer.setPos(Vec3.atCenterOf(helper.absolutePos(node.above())));
    NodeMenu menu = new NodeMenu(MENU_ID, viewer, helper.absolutePos(node));
    menu.broadcastChanges();
    menu.clickMenuButton(viewer, NodeScreenCodes.button(NodeAction.choose(position)));
  }

  /** What the game does when the player right-clicks the block with the item in the hand. */
  static InteractionResult click(GameTestHelper helper, Player player, BlockPos relative) {
    BlockPos pos = helper.absolutePos(relative);
    BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
    return player
        .getItemInHand(InteractionHand.MAIN_HAND)
        .useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit));
  }

  static void shiftClick(GameTestHelper helper, Player player, BlockPos relative) {
    player.setShiftKeyDown(true);
    click(helper, player, relative);
    player.setShiftKeyDown(false);
  }

  static Network networkOf(NodeId node) {
    return RedweaveServer.running().orElseThrow().networkOfNode(node).orElseThrow();
  }

  static List<Optional<ChannelSlot>> slotsOf(NodeId node) {
    return networkOf(node).node(node).orElseThrow().slots();
  }
}
