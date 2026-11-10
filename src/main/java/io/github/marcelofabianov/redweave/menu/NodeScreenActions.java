package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.block.NodeSignals;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Carries an action of the Node Screen to its use case. The domain checks the owner and the slot,
 * so a refused action changes nothing (answers 22 and 81, and rule 17).
 */
final class NodeScreenActions {
  private NodeScreenActions() {}

  static PlayerId playerOf(Player player) {
    return new PlayerId(player.getUUID());
  }

  static void apply(Player player, BlockPos pos, NodeAction action) {
    Level level = player.level();
    Optional<RedweaveServer> server = RedweaveServer.running();
    Optional<NodeId> node = NodeSignals.nodeAt(level, pos);
    if (level.isClientSide || server.isEmpty() || node.isEmpty()) {
      return;
    }
    run(server.get(), playerOf(player), node.get(), action);
    NodeSignals.read(server.get(), level, pos, level.getBlockState(pos));
  }

  private static void run(RedweaveServer server, PlayerId player, NodeId node, NodeAction action) {
    switch (action.kind()) {
      case USE -> server.useFreeSlot().use(player, node, action.index());
      case CONFIGURE ->
          server
              .configureSlot()
              .configure(player, node, action.index(), action.slot().orElseThrow());
      case CLEAR -> server.clearSlot().clear(player, node, action.index());
    }
  }
}
