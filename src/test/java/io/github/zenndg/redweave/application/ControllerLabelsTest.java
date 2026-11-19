package io.github.zenndg.redweave.application;

import static io.github.zenndg.redweave.domain.network.NetworkFixture.COLOR;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.NAME;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.NODE;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OTHER_NODE;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OTHER_NODE_POSITION;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.zenndg.redweave.domain.network.ModuleEntry;
import io.github.zenndg.redweave.domain.network.ModuleId;
import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.NetworkState;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * The floating label over a Network Controller, with the Configurator in hand: the state of the
 * network and how many components it has, and below its name, after a square in its color (answer
 * 773).
 */
class ControllerLabelsTest {
  private static final int NO_NODES = 0;
  private static final int THREE_COMPONENTS = 3;
  private static final String KIND = "signal_timer";
  private static final String MODULE_UUID = "8a9b0c1d-2e3f-4a4b-9c5d-6e7f8a9b0c1d";
  private static final ModuleId MODULE = new ModuleId(UUID.fromString(MODULE_UUID));

  @Test
  void aNewNetworkIsOnlineWithNoNodes() {
    ControllerLabel label = ControllerLabels.of(network());

    assertEquals(new ControllerLabel(NAME, COLOR, NetworkState.ONLINE, NO_NODES), label);
  }

  @Test
  void theLabelCountsTheNodesAndTheModules() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());
    network.attachNode(OWNER, OTHER_NODE, OTHER_NODE_POSITION, Optional.empty());

    network
        .register()
        .register(OWNER, new ModuleEntry(MODULE, KIND, Optional.empty(), NODE_POSITION, List.of()));

    assertEquals(THREE_COMPONENTS, ControllerLabels.of(network).components());
  }

  /** A network its owner turned off at a Network Terminal says so (answer 132). */
  @Test
  void aNetworkTurnedOffIsOffline() {
    Network network = network();
    network.terminal().turnOff(OWNER);

    assertEquals(NetworkState.OFFLINE, ControllerLabels.of(network).state());
  }
}
