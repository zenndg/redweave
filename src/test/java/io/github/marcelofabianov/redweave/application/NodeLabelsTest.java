package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NAME;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkState;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** The floating label over a node, with the Configurator in hand (answers 268 and 270). */
class NodeLabelsTest {
  private static final String NODE_NAME = "Lamp";
  private static final String DOOR = "Main door";
  private static final Signal LEVER = new Signal(12);
  private static final int NO_MORE = 0;
  private static final int TWO_MORE = 2;
  private static final int SHOWN = 4;

  /** The default name of the node of the fixture: the first 8 characters of its ID (142). */
  private static final String DEFAULT_NAME = "1b2c3d4e";

  private static Network withNode() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.of(NODE_NAME));
    return network;
  }

  private static void enable(Network network, ChannelColor color, Direction direction) {
    network.configureSlot(
        OWNER, NODE, color.ordinal(), new ChannelSlot(direction, NODE_POSITION.face(), color));
  }

  @Test
  void aNodeWithNoNetworkSaysSo() {
    NodeLabel label = NodeLabels.of(NODE, Optional.empty());

    assertEquals(DEFAULT_NAME, label.node());
    assertEquals(Optional.empty(), label.network());
    assertEquals(List.of(), label.channels());
  }

  @Test
  void aLabelHasTheNodeTheNetworkAndItsState() {
    NodeLabel label = NodeLabels.of(NODE, Optional.of(withNode()));

    assertEquals(NODE_NAME, label.node());
    assertEquals(
        Optional.of(new NodeLabel.NetworkPart(NAME, NetworkState.ONLINE)), label.network());
  }

  /** Each enabled channel with its name, direction and signal, in the order of the colors. */
  @Test
  void theChannelsComeInTheOrderOfTheColors() {
    Network network = withNode();
    enable(network, ChannelColor.RED, Direction.INPUT);
    enable(network, ChannelColor.ORANGE, Direction.OUTPUT);
    network.renameChannel(OWNER, ChannelColor.RED, DOOR);
    network.readInput(NODE, ChannelColor.RED.ordinal(), LEVER);

    NodeLabel label = NodeLabels.of(NODE, Optional.of(network));

    List<NodeLabel.Channel> expected =
        List.of(
            new NodeLabel.Channel(
                ChannelColor.ORANGE, Optional.empty(), Direction.OUTPUT, Signal.OFF),
            new NodeLabel.Channel(ChannelColor.RED, Optional.of(DOOR), Direction.INPUT, LEVER));
    assertEquals(expected, label.channels());
    assertEquals(NO_MORE, label.more());
  }

  /** At most 4 channels, the same as the dots of the plate, and how many more (answer 229). */
  @Test
  void atMostFourChannelsAndHowManyMore() {
    Network network = withNode();
    for (int color = 0; color < SHOWN + TWO_MORE; color++) {
      enable(network, ChannelColor.values()[color], Direction.INPUT);
    }

    NodeLabel label = NodeLabels.of(NODE, Optional.of(network));

    assertEquals(SHOWN, label.channels().size());
    assertEquals(TWO_MORE, label.more());
  }

  /** A network turned off shows its state (answer 270). */
  @Test
  void anOfflineNetworkShowsItsState() {
    Network network = withNode();
    network.turnOff(OWNER);

    NodeLabel label = NodeLabels.of(NODE, Optional.of(network));

    assertEquals(NetworkState.OFFLINE, label.network().orElseThrow().state());
  }

  /** A network that no longer has the node gives the label of a node with no network. */
  @Test
  void aNetworkWithoutTheNodeGivesNoNetwork() {
    NodeLabel label = NodeLabels.of(NODE, Optional.of(network()));

    assertEquals(Optional.empty(), label.network());
  }
}
