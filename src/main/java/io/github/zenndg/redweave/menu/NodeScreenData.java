package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.block.NodeSignals;
import io.github.zenndg.redweave.block.part.NodePart;
import io.github.zenndg.redweave.block.part.PartSpot;
import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.Node;
import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.PlayerId;
import io.github.zenndg.redweave.server.RedweaveServer;
import java.util.Optional;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;

/**
 * The live values of the Node Screen, read from the network each time the game asks. The game
 * compares them every tick and sends only what changed (answer 82).
 */
final class NodeScreenData implements ContainerData {
  private final Player player;
  private final Level level;
  private final PartSpot spot;
  private final PlayerId viewer;

  NodeScreenData(Player player, PartSpot spot) {
    this.player = player;
    this.level = player.level();
    this.spot = spot;
    this.viewer = NodeScreenActions.playerOf(player);
  }

  @Override
  public int get(int index) {
    if (index == NodeScreenLayout.VIEWER_PLACED) {
      return placedByViewer() ? 1 : 0;
    }
    Optional<NodeId> id = NodeSignals.nodeAt(level, spot);
    Optional<Network> network =
        id.flatMap(node -> RedweaveServer.running().flatMap(server -> server.networkOfNode(node)));
    if (network.isEmpty()) {
      return 0;
    }
    return valueOf(network.get(), network.get().node(id.get()).orElseThrow(), index);
  }

  private int valueOf(Network network, Node node, int index) {
    if (index == NodeScreenLayout.SLOT_COUNT) {
      return node.slots().size();
    }
    if (index == NodeScreenLayout.STATE) {
      return network.nodeState(node.id(), true).ordinal() + 1;
    }
    if (index == NodeScreenLayout.VIEWER_IS_OWNER) {
      return network.isOwner(viewer) ? 1 : 0;
    }
    if (index == NodeScreenLayout.DELAY) {
      return node.delay().seconds();
    }
    int slot = index - NodeScreenLayout.FIRST_SLOT;
    if (slot < NodeScreenLayout.MAX_SLOTS) {
      return slot < node.slots().size() ? NodeScreenCodes.slot(node.slots().get(slot)) : 0;
    }
    return network.slotSignal(node.id(), slot - NodeScreenLayout.MAX_SLOTS).strength();
  }

  private boolean placedByViewer() {
    return NodePart.at(level, spot).map(node -> node.wasPlacedBy(player)).orElse(false);
  }

  @Override
  public void set(int index, int value) {
    // The server reads the network, so a value from the client is never kept (rule 16).
  }

  @Override
  public int getCount() {
    return NodeScreenLayout.SIZE;
  }
}
