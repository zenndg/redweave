package io.github.marcelofabianov.redweave.client.screen;

import io.github.marcelofabianov.redweave.domain.network.NodeState;
import java.util.Optional;

/** The color of a node row: amber when unloaded, red when out of range (answer 29). */
final class NodeStates {
  private NodeStates() {}

  static int colorOf(NodeState state) {
    return state == NodeState.ONLINE ? ScreenColors.TEXT : StatePill.colorOf(Optional.of(state));
  }
}
