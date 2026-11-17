package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.COLOR;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NAME;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Face;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * The floating label over a node, with the Configurator in hand: the network, with its color, and
 * the node, without the channels, after a square in the color of its first enabled slot (answers
 * 268, 274 to 276 and 772).
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

    assertEquals(new NodeLabel(DEFAULT_NAME, Optional.empty(), Optional.empty()), label);
  }

  @Test
  void aLabelHasTheNodeAndTheNetworkWithItsColor() {
    NodeLabel label = NodeLabels.of(NODE, Optional.of(withNode()));

    NodeLabel expected =
        new NodeLabel(
            NODE_NAME, Optional.of(new NodeLabel.NetworkPart(NAME, COLOR)), Optional.empty());
    assertEquals(expected, label);
  }

  /** A network that no longer has the node gives the label of a node with no network. */
  @Test
  void aNetworkWithoutTheNodeGivesNoNetwork() {
    NodeLabel label = NodeLabels.of(NODE, Optional.of(network()));

    assertEquals(Optional.empty(), label.network());
  }

  /** The square has the color of the first enabled slot, in the order of the colors (772). */
  @Test
  void theSquareHasTheColorOfTheFirstEnabledSlot() {
    Network network = withNode();
    enable(network, ChannelColor.LIME);
    enable(network, ChannelColor.ORANGE);

    NodeLabel label = NodeLabels.of(NODE, Optional.of(network));

    assertEquals(Optional.of(ChannelColor.ORANGE), label.slot());
  }

  /** A slot set up but disabled does not count, even before an enabled one (772). */
  @Test
  void aDisabledSlotGivesNoSquare() {
    Network network = withNode();
    network.setUpSlot(OWNER, NODE, ChannelColor.WHITE.ordinal(), Direction.INPUT, Face.UP);

    NodeLabel label = NodeLabels.of(NODE, Optional.of(network));

    assertEquals(Optional.empty(), label.slot());
  }

  private static void enable(Network network, ChannelColor channel) {
    network.setUpSlot(OWNER, NODE, channel.ordinal(), Direction.INPUT, Face.UP);
    network.enableSlot(OWNER, NODE, channel.ordinal());
  }
}
