package io.github.marcelofabianov.redweave.block;

import io.github.marcelofabianov.redweave.RedweaveMod;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import net.minecraft.network.chat.Component;

/** What the action bar shows about a node, until the screen of issue #19 (answer 75). */
public final class NodeMessages {
  private static final String PREFIX = "message." + RedweaveMod.MOD_ID + ".signal_node.";

  public static final String ONLINE = PREFIX + "online";
  public static final String OUT_OF_RANGE = PREFIX + "out_of_range";
  public static final String NO_NETWORK = PREFIX + "no_network";

  private NodeMessages() {}

  /** The state of a clicked node, whose chunk is loaded, so it is never shown as unloaded. */
  static Component stateOf(Network network, NodeId node) {
    return switch (network.nodeState(node, true)) {
      case ONLINE -> Component.translatable(ONLINE, network.name());
      case OUT_OF_RANGE -> Component.translatable(OUT_OF_RANGE, network.name());
      default -> noNetwork();
    };
  }

  static Component noNetwork() {
    return Component.translatable(NO_NETWORK);
  }
}
