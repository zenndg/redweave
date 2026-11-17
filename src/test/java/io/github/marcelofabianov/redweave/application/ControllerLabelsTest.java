package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.COLOR;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NAME;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkState;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * The floating label over a Network Controller, with the Configurator in hand: the state of the
 * network and how many nodes it has, and below its name, after a square in its color (answer 773).
 */
class ControllerLabelsTest {
  private static final int NO_NODES = 0;
  private static final int TWO_NODES = 2;

  @Test
  void aNewNetworkIsOnlineWithNoNodes() {
    ControllerLabel label = ControllerLabels.of(network());

    assertEquals(new ControllerLabel(NAME, COLOR, NetworkState.ONLINE, NO_NODES), label);
  }

  @Test
  void theLabelCountsTheNodesOfTheNetwork() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());
    network.attachNode(OWNER, OTHER_NODE, OTHER_NODE_POSITION, Optional.empty());

    assertEquals(TWO_NODES, ControllerLabels.of(network).nodes());
  }

  /** A network its owner turned off at a Network Terminal says so (answer 132). */
  @Test
  void aNetworkTurnedOffIsOffline() {
    Network network = network();
    network.terminal().turnOff(OWNER);

    assertEquals(NetworkState.OFFLINE, ControllerLabels.of(network).state());
  }
}
