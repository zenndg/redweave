package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.COLOR;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NAME;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.network.Network;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * The floating label over a node, with the Configurator in hand: the network, with its color, and
 * the node, without the channels (answers 268 and 274 to 276).
 */
class NodeLabelsTest {
  private static final String NODE_NAME = "Lamp";

  /** The default name of the node of the fixture: the first 8 characters of its ID (142). */
  private static final String DEFAULT_NAME = "1b2c3d4e";

  private static Network withNode() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.of(NODE_NAME));
    return network;
  }

  @Test
  void aNodeWithNoNetworkSaysSo() {
    NodeLabel label = NodeLabels.of(NODE, Optional.empty());

    assertEquals(new NodeLabel(DEFAULT_NAME, Optional.empty()), label);
  }

  @Test
  void aLabelHasTheNodeAndTheNetworkWithItsColor() {
    NodeLabel label = NodeLabels.of(NODE, Optional.of(withNode()));

    NodeLabel expected =
        new NodeLabel(NODE_NAME, Optional.of(new NodeLabel.NetworkPart(NAME, COLOR)));
    assertEquals(expected, label);
  }

  /** A network that no longer has the node gives the label of a node with no network. */
  @Test
  void aNetworkWithoutTheNodeGivesNoNetwork() {
    NodeLabel label = NodeLabels.of(NODE, Optional.of(network()));

    assertEquals(Optional.empty(), label.network());
  }
}
