package io.github.marcelofabianov.redweave.menu;

import io.github.marcelofabianov.redweave.domain.network.NetworkState;
import net.minecraft.network.chat.Component;

/**
 * The texts of a network that the Network Terminal and the label over a Network Controller share:
 * its state and how many nodes it has (answers 252 and 773).
 */
public final class NetworkTexts {
  private static final int SINGLE = 1;

  private NetworkTexts() {}

  public static Component state(NetworkState state) {
    return Component.translatable(
        switch (state) {
          case ONLINE -> NodeScreenTexts.STATE_ONLINE;
          case OFFLINE -> NodeScreenTexts.STATE_OFFLINE;
          case UNPLACED -> TerminalScreenTexts.STATE_UNPLACED;
        });
  }

  /** As `1 node` or `3 nodes`. */
  public static Component nodes(int nodes) {
    String key = nodes == SINGLE ? NodeScreenTexts.NODE_COUNT_ONE : NodeScreenTexts.NODE_COUNT_MANY;
    return Component.translatable(key, nodes);
  }

  /** The numbers come from the server, so one outside the states shows the safest one. */
  public static NetworkState stateOf(int state) {
    NetworkState[] states = NetworkState.values();
    return state >= 0 && state < states.length ? states[state] : NetworkState.OFFLINE;
  }
}
