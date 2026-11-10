package io.github.marcelofabianov.redweave.gametest;

import io.github.marcelofabianov.redweave.block.NetworkControllerBlockEntity;
import io.github.marcelofabianov.redweave.block.SignalNodeBlockEntity;
import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.registry.RedweaveItems;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/** Controllers, nodes and clicks with an item, shared by the GameTests of the tools. */
final class NodeScene {
  private NodeScene() {}

  static NetworkId placeController(
      GameTestHelper helper, Player owner, BlockPos floor, BlockPos controller) {
    ItemStack item = new ItemStack(RedweaveItems.NETWORK_CONTROLLER.get());
    GameTestActions.placeOn(helper, owner, item, floor, Direction.UP);
    NetworkControllerBlockEntity block = helper.getBlockEntity(controller);
    return block.network().orElseThrow();
  }

  static NodeId placeNode(GameTestHelper helper, Player owner, BlockPos stone, Direction face) {
    helper.setBlock(stone, Blocks.STONE);
    ItemStack item = new ItemStack(RedweaveItems.SIGNAL_NODE.get());
    GameTestActions.placeOn(helper, owner, item, stone, face);
    SignalNodeBlockEntity node = helper.getBlockEntity(stone.relative(face));
    return node.node().orElseThrow();
  }

  /** What the game does when the player right-clicks the block with the item in the hand. */
  static void click(GameTestHelper helper, Player player, BlockPos relative) {
    BlockPos pos = helper.absolutePos(relative);
    BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false);
    player
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
