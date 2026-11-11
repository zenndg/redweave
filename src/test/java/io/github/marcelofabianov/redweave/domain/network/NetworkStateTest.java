package io.github.marcelofabianov.redweave.domain.network;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.CONTROLLER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * A network is Online, Offline when its owner turns it off, or Unplaced without its controller in
 * the world. Off and Unplaced, every channel is off (answers 132, 135, 146, 178 and 209).
 */
class NetworkStateTest {
  private static final ChannelColor CHANNEL = ChannelColor.RED;
  private static final int SLOT = CHANNEL.ordinal();
  private static final Signal READ = new Signal(9);

  /** A network with an input on CHANNEL that reads READ, already evaluated. */
  private static Network readingNetwork() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());
    network.configureSlot(
        OWNER, NODE, SLOT, new ChannelSlot(Direction.INPUT, NODE_POSITION.face(), CHANNEL));
    network.readInput(NODE, SLOT, READ);
    network.settle(Map.of());
    return network;
  }

  @Test
  void aNewNetworkIsOnline() {
    assertEquals(NetworkState.ONLINE, network().state());
  }

  @Test
  void theOwnerTurnsTheNetworkOffAndEveryChannelGoesOff() {
    Network network = readingNetwork();

    assertEquals(Outcome.ACCEPTED, network.turnOff(OWNER));
    assertEquals(NetworkState.OFFLINE, network.state());
    assertFalse(network.isOnline());
    assertEquals(Signal.OFF, network.channelValue(CHANNEL));
    assertEquals(NodeState.OFFLINE, network.nodeState(NODE, true));
    assertTrue(network.isPending());
  }

  @Test
  void theOwnerTurnsTheNetworkBackOnWithTheLastReading() {
    Network network = readingNetwork();
    network.turnOff(OWNER);
    network.settle(Map.of());

    assertEquals(Outcome.ACCEPTED, network.turnOn(OWNER));
    assertEquals(NetworkState.ONLINE, network.state());
    assertEquals(READ, network.channelValue(CHANNEL));
    assertTrue(network.isPending());
  }

  @Test
  void anotherPlayerCannotTurnTheNetworkOffNorOn() {
    Network network = network();

    assertEquals(Outcome.NOT_OWNER, network.turnOff(STRANGER));
    network.turnOff(OWNER);
    assertEquals(Outcome.NOT_OWNER, network.turnOn(STRANGER));
    assertEquals(NetworkState.OFFLINE, network.state());
  }

  /** Only an online network turns off, and only an offline one turns on (answer 132). */
  @Test
  void aNetworkThatIsNotInTheRightStateIsRefused() {
    Network network = network();

    assertEquals(Outcome.NETWORK_NOT_OFFLINE, network.turnOn(OWNER));
    network.turnOff(OWNER);
    assertEquals(Outcome.NETWORK_NOT_ONLINE, network.turnOff(OWNER));
  }

  /** Without its controller, the network is Unplaced and its nodes have no network (146). */
  @Test
  void aNetworkWithoutItsControllerIsUnplacedAndCannotBeTurnedOn() {
    Network network = readingNetwork();
    network.removeController(OWNER);

    assertEquals(NetworkState.UNPLACED, network.state());
    assertEquals(NodeState.NO_NETWORK, network.nodeState(NODE, true));
    assertEquals(Outcome.NETWORK_NOT_ONLINE, network.turnOff(OWNER));
    assertEquals(Outcome.NETWORK_NOT_OFFLINE, network.turnOn(OWNER));
  }

  /** Turning off is a choice of the owner, kept while the controller is away (answer 178). */
  @Test
  void aNetworkTurnedOffComesBackOffWithItsController() {
    Network network = network();
    network.turnOff(OWNER);
    network.removeController(OWNER);

    assertEquals(NetworkState.UNPLACED, network.state());
    network.placeController(OWNER, CONTROLLER);
    assertEquals(NetworkState.OFFLINE, network.state());
  }

  @Test
  void aNodeOfAnOfflineNetworkDoesNotCount() {
    Network network = readingNetwork();
    network.turnOff(OWNER);

    assertFalse(network.counts(NODE));
  }
}
