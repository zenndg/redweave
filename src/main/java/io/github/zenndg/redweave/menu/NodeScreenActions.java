package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.block.NodeLook;
import io.github.zenndg.redweave.block.NodeSignals;
import io.github.zenndg.redweave.block.part.NodePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.config.NeoForgeOutputDelayLimits;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.domain.signal.OutputDelayLimits;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Carries an action of the Node Screen to its use case. The domain checks the owner and the slot,
 * so a refused action changes nothing (answers 22 and 81, and rule 17).
 */
final class NodeScreenActions {
  private static final OutputDelayLimits DELAY_LIMITS = new NeoForgeOutputDelayLimits();

  private NodeScreenActions() {}

  static PlayerId playerOf(Player player) {
    return new PlayerId(player.getUUID());
  }

  static void apply(Player player, PartSpot spot, NodeAction action) {
    Level level = player.level();
    Optional<RedweaveServer> server = RedweaveServer.running();
    Optional<NodeId> node = NodeSignals.nodeAt(level, spot);
    if (level.isClientSide || server.isEmpty() || node.isEmpty()) {
      return;
    }
    run(server.get(), playerOf(player), node.get(), action);
    NodeSignals.read(server.get(), level, spot);
    if (action.kind() == NodeAction.Kind.DELAY) {
      NodeSignals.refreshEmission(server.get(), level, spot);
    }
  }

  /**
   * Picks or switches the network of the node, for whoever may (answers 141 and 172). The node
   * reads its block again and shows its new slot on the corners at once.
   */
  static void choose(Player player, PartSpot spot, NetworkId network) {
    Level level = player.level();
    Optional<RedweaveServer> server = RedweaveServer.running();
    Optional<NodeId> node = NodeSignals.nodeAt(level, spot);
    if (level.isClientSide
        || server.isEmpty()
        || node.isEmpty()
        || !NodeScreenChoices.mayChoose(player, spot)) {
      return;
    }
    server
        .get()
        .linkNode()
        .link(playerOf(player), node.get(), NodePart.positionOf(level, spot), network);
    NodeSignals.read(server.get(), level, spot);
    NodeSignals.refreshEmission(server.get(), level, spot);
    NodeLook.refresh(server.get(), level, spot);
  }

  private static void run(RedweaveServer server, PlayerId player, NodeId node, NodeAction action) {
    switch (action.kind()) {
      case ENABLE -> server.enableSlot().enable(player, node, action.index());
      case CONFIGURE ->
          server
              .configureSlot()
              .configure(player, node, action.index(), action.slot().orElseThrow());
      case DISABLE -> server.disableSlot().disable(player, node, action.index());
      case DELAY ->
          server
              .nodeOutputs()
              .changeDelay()
              .step(player, node, DelaySteps.seconds(action.index()), DELAY_LIMITS);
      default -> {
        // A choice of network goes through choose, which checks who may pick (172).
      }
    }
  }
}
