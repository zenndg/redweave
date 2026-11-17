package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.Network;

/**
 * Builds the label of a Network Controller from its network (answer 773). It only reads the
 * network, and the world decides which controllers are near the player, as for the nodes (268).
 */
public final class ControllerLabels {
  private ControllerLabels() {}

  public static ControllerLabel of(Network network) {
    return new ControllerLabel(
        network.name(), network.color(), network.state(), network.nodes().size());
  }
}
