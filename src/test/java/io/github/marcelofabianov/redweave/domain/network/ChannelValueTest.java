package io.github.marcelofabianov.redweave.domain.network;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.CONTROLLER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OVERWORLD;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import org.junit.jupiter.api.Test;

/**
 * A channel is worth the strongest of the inputs that count, and 0 with none (rede-e-canais.md).
 */
class ChannelValueTest {
  private static final int SLOT = 0;
  private static final int PAST_THE_LAST_SLOT = 4;
  private static final ChannelColor DOOR = ChannelColor.RED;
  private static final Signal WEAK = new Signal(4);
  private static final Signal MIDDLE = new Signal(9);
  private static final Signal STRONG = new Signal(11);
  private static final int FAR = 500;
  private static final BlockLocation FAR_CONTROLLER = new BlockLocation(OVERWORLD, FAR, 64, FAR);

  private static Network networkReadingDoorOn(NodeId... nodes) {
    Network network = network();
    for (NodeId node : nodes) {
      NodePosition position = node.equals(NODE) ? NODE_POSITION : OTHER_NODE_POSITION;
      network.attachNode(OWNER, node, position);
      network.configureSlot(
          OWNER, node, SLOT, new ChannelSlot(Direction.INPUT, position.face(), DOOR));
    }
    return network;
  }

  @Test
  void aChannelWithNoInputIsOff() {
    assertEquals(Signal.OFF, network().channelValue(DOOR));
  }

  @Test
  void aChannelWithOneInputIsWorthWhatItRead() {
    Network network = networkReadingDoorOn(NODE);
    network.readInput(NODE, SLOT, MIDDLE);

    assertEquals(MIDDLE, network.channelValue(DOOR));
  }

  @Test
  void aChannelWithTwoInputsIsWorthTheStrongest() {
    Network network = networkReadingDoorOn(NODE, OTHER_NODE);
    network.readInput(NODE, SLOT, WEAK);
    network.readInput(OTHER_NODE, SLOT, STRONG);

    assertEquals(STRONG, network.channelValue(DOOR));
  }

  @Test
  void anInputThatWasNeverReadIsOff() {
    assertEquals(Signal.OFF, networkReadingDoorOn(NODE).channelValue(DOOR));
  }

  /** The simulator only evaluates a channel whose input changed (issue #12). */
  @Test
  void readingTellsWhetherTheInputChanged() {
    Network network = networkReadingDoorOn(NODE);

    assertTrue(network.readInput(NODE, SLOT, MIDDLE));
    assertFalse(network.readInput(NODE, SLOT, MIDDLE));
    assertTrue(network.readInput(NODE, SLOT, WEAK));
  }

  /** Without the controller, every channel is off (answer 24). */
  @Test
  void anOfflineNetworkHasEveryChannelOff() {
    Network network = networkReadingDoorOn(NODE);
    network.readInput(NODE, SLOT, STRONG);
    network.removeController(OWNER);

    assertEquals(Signal.OFF, network.channelValue(DOOR));
  }

  /** A node out of the radius does not count (answer 23). */
  @Test
  void aNodeOutOfRangeDoesNotCount() {
    Network network = networkReadingDoorOn(NODE);
    network.readInput(NODE, SLOT, STRONG);
    network.removeController(OWNER);
    network.placeController(OWNER, FAR_CONTROLLER);

    assertEquals(Signal.OFF, network.channelValue(DOOR));
  }

  @Test
  void aNodeBackInRangeCountsAgainWithItsLastReading() {
    Network network = networkReadingDoorOn(NODE);
    network.readInput(NODE, SLOT, STRONG);
    network.removeController(OWNER);
    network.placeController(OWNER, FAR_CONTROLLER);
    network.removeController(OWNER);
    network.placeController(OWNER, CONTROLLER);

    assertEquals(STRONG, network.channelValue(DOOR));
  }

  @Test
  void aReadingOnAnOutputIsIgnored() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION);
    network.configureSlot(
        OWNER, NODE, SLOT, new ChannelSlot(Direction.OUTPUT, NODE_POSITION.face(), DOOR));

    assertFalse(network.readInput(NODE, SLOT, STRONG));
    assertEquals(Signal.OFF, network.channelValue(DOOR));
  }

  /** A bad reading from the world never breaks the tick (AGENTS.md, rule 14). */
  @Test
  void aReadingForANodeThatIsNotThereIsIgnored() {
    assertFalse(network().readInput(NODE, SLOT, STRONG));
  }

  @Test
  void aReadingPastTheSlotsOfTheNodeIsIgnored() {
    Network network = networkReadingDoorOn(NODE);

    assertFalse(network.readInput(NODE, PAST_THE_LAST_SLOT, STRONG));
    assertEquals(Signal.OFF, network.channelValue(DOOR));
  }
}
