package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.block.part.NodePart;
import io.github.marcelofabianov.redweave.block.part.PartSpot;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.Node;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.NodeState;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.server.RedweaveServer;
import java.util.List;
import java.util.Optional;
import net.minecraft.world.level.Level;

/**
 * What a node looks like in the world: the ring when it is out of range (answer 86) and the color
 * of its first 4 enabled channels on the corners (answers 87 and 229). Only a change reaches the
 * clients.
 */
public final class NodeLook {
  /** The corners of the plate show the first 4 enabled channels. */
  public static final int CORNERS = 4;

  /** A corner with no enabled channel, or of a node without a network. */
  public static final int FREE = -1;

  private NodeLook() {}

  public static void refresh(RedweaveServer server, Level level, PartSpot spot) {
    Optional<PartSpaceBlockEntity> space = PartSpaceBlockEntity.at(level, spot.pos());
    Optional<NodePart> part = NodePart.at(level, spot);
    if (part.isEmpty()) {
      return;
    }
    Optional<NodeId> id = part.get().node();
    Optional<Network> network = id.flatMap(server::networkOfNode);
    Optional<Node> node = network.flatMap(found -> found.node(id.get()));
    boolean outOfRange =
        node.isPresent() && network.get().nodeState(id.get(), true) == NodeState.OUT_OF_RANGE;
    boolean ring = part.get().setOutOfRange(outOfRange);
    if (part.get().setCorners(corners(node)) || ring) {
      space.orElseThrow().changed();
    }
  }

  private static int[] corners(Optional<Node> node) {
    int[] corners = {FREE, FREE, FREE, FREE};
    List<ChannelColor> enabled = node.map(Node::enabledChannels).orElse(List.of());
    for (int corner = 0; corner < Math.min(CORNERS, enabled.size()); corner++) {
      corners[corner] = enabled.get(corner).ordinal();
    }
    return corners;
  }
}
