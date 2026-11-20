package io.github.zenndg.redweave.menu;

import io.github.zenndg.redweave.domain.network.NetworkState;
import net.minecraft.network.chat.Component;

/**
 * The texts of a network that the Network Terminal and the label over a Network Controller share:
 * its state and how many components it has (answers 252, 773 and 804).
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

  /** As `1 component` or `12 components`: the nodes and the modules (answers 802 and 804). */
  public static Component components(int components) {
    String key =
        components == SINGLE
            ? NodeScreenTexts.COMPONENT_COUNT_ONE
            : NodeScreenTexts.COMPONENT_COUNT_MANY;
    return Component.translatable(key, components);
  }

  /** The numbers come from the server, so one outside the states shows the safest one. */
  public static NetworkState stateOf(int state) {
    NetworkState[] states = NetworkState.values();
    return state >= 0 && state < states.length ? states[state] : NetworkState.OFFLINE;
  }
}
