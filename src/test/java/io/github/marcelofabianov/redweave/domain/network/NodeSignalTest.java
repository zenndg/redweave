package io.github.marcelofabianov.redweave.domain.network;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OVERWORLD;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * The signal of a node, shown by its lens and by the node list of the Controller Screen: the
 * strongest of its last readings and of what it emits, and off when it does not count (answers 98,
 * 177 and 179).
 */
class NodeSignalTest {
  private static final int FIRST_SLOT = 0;
  private static final int SECOND_SLOT = 1;
  private static final ChannelColor DOOR = ChannelColor.RED;
  private static final Signal WEAK = new Signal(4);
  private static final Signal MIDDLE = new Signal(9);
  private static final Signal STRONG = new Signal(11);
  private static final int FAR = 500;
  private static final BlockLocation FAR_CONTROLLER = new BlockLocation(OVERWORLD, FAR, 64, FAR);

  private static void slot(Network network, NodeId node, int index, Direction direction) {
    NodePosition position = node.equals(NODE) ? NODE_POSITION : OTHER_NODE_POSITION;
    network.configureSlot(OWNER, node, index, new ChannelSlot(direction, position.face(), DOOR));
  }

  private static Network networkWithNode() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());
    return network;
  }

  /** NODE reads WEAK on its first slot, and emits the channel, set by OTHER_NODE, on the second. */
  private static Network networkWhereNodeReadsAndEmits(Signal emitted) {
    Network network = networkWithNode();
    network.attachNode(OWNER, OTHER_NODE, OTHER_NODE_POSITION, Optional.empty());
    slot(network, NODE, FIRST_SLOT, Direction.INPUT);
    slot(network, NODE, SECOND_SLOT, Direction.OUTPUT);
    slot(network, OTHER_NODE, FIRST_SLOT, Direction.INPUT);
    network.readInput(NODE, FIRST_SLOT, WEAK);
    network.readInput(OTHER_NODE, FIRST_SLOT, emitted);
    return network;
  }

  @Test
  void aNodeWithFreeSlotsIsOff() {
    assertEquals(Signal.OFF, networkWithNode().nodeSignal(NODE));
  }

  @Test
  void aNodeOutsideTheNetworkIsOff() {
    assertEquals(Signal.OFF, network().nodeSignal(NODE));
  }

  /** An input receiving a signal lights the node too (answer 98). */
  @Test
  void anInputShowsItsLastReading() {
    Network network = networkWithNode();
    slot(network, NODE, FIRST_SLOT, Direction.INPUT);
    network.readInput(NODE, FIRST_SLOT, MIDDLE);

    assertEquals(MIDDLE, network.nodeSignal(NODE));
  }

  @Test
  void theEmissionWinsWhenItIsStronger() {
    assertEquals(STRONG, networkWhereNodeReadsAndEmits(STRONG).nodeSignal(NODE));
  }

  @Test
  void theReadingWinsWhenItIsStronger() {
    Network network = networkWhereNodeReadsAndEmits(STRONG);
    network.readInput(NODE, FIRST_SLOT, Signal.MAX);

    assertEquals(Signal.MAX, network.nodeSignal(NODE));
  }

  /** A node out of range does not count, so its lens is off, even with a kept reading (98). */
  @Test
  void aNodeOutOfRangeIsOff() {
    Network network = networkWithNode();
    slot(network, NODE, FIRST_SLOT, Direction.INPUT);
    network.readInput(NODE, FIRST_SLOT, MIDDLE);
    network.removeController(OWNER);
    network.placeController(OWNER, FAR_CONTROLLER);

    assertEquals(Signal.OFF, network.nodeSignal(NODE));
  }

  /** A node of a network turned off shows its lens off (answer 177). */
  @Test
  void aNodeOfAnOfflineNetworkIsOff() {
    Network network = networkWhereNodeReadsAndEmits(STRONG);
    network.turnOff(OWNER);

    assertEquals(Signal.OFF, network.nodeSignal(NODE));
  }
}
