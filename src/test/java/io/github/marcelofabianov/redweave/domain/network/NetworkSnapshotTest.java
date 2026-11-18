package io.github.marcelofabianov.redweave.domain.network;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** A network goes to the save as a snapshot and comes back the same (answers 37, 38 and 90). */
class NetworkSnapshotTest {
  private static final String RENAMED = "Base door";
  private static final String CHANNEL_NAME = "Door";
  private static final String NODE_NAME = "Lever";
  private static final ChannelColor NEW_COLOR = ChannelColor.ORANGE;
  private static final ChannelColor CHANNEL = ChannelColor.RED;
  private static final int INPUT_SLOT = CHANNEL.ordinal();
  private static final int OUTPUT_SLOT = CHANNEL.ordinal();
  private static final Signal READ = new Signal(11);

  private static Network busyNetwork() {
    Network network = network();
    network.rename(OWNER, RENAMED);
    network.recolor(OWNER, NEW_COLOR);
    network.renameChannel(OWNER, CHANNEL, CHANNEL_NAME);
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());
    network.attachNode(OWNER, OTHER_NODE, OTHER_NODE_POSITION, Optional.empty());
    network.nodeSettings().rename(OWNER, NODE, NODE_NAME);
    network.configureSlot(
        OWNER, NODE, INPUT_SLOT, new ChannelSlot(Direction.INPUT, Face.EAST, CHANNEL));
    network.configureSlot(
        OWNER, OTHER_NODE, OUTPUT_SLOT, new ChannelSlot(Direction.OUTPUT, Face.WEST, CHANNEL));
    network.readInput(NODE, INPUT_SLOT, READ);
    return network;
  }

  private static Network restored(Network network) {
    return Network.restore(network.snapshot(), TestLimits.STARTING);
  }

  @Test
  void aRestoredNetworkIsTheSameAsTheSavedOne() {
    Network network = busyNetwork();

    assertEquals(network.snapshot(), restored(network).snapshot());
  }

  @Test
  void aRestoredNetworkKeepsTheLastReadingOfEachInput() {
    Network restored = restored(busyNetwork());

    assertEquals(READ, restored.channelValue(CHANNEL));
    assertEquals(READ, restored.output(OTHER_NODE, OUTPUT_SLOT));
  }

  @Test
  void aRestoredNetworkKeepsItsNamesAndNodes() {
    Network restored = restored(busyNetwork());

    assertEquals(RENAMED, restored.name());
    assertEquals(NEW_COLOR, restored.color());
    assertEquals(Optional.of(CHANNEL_NAME), restored.channel(CHANNEL).name());
    assertEquals(NODE_NAME, restored.node(NODE).orElseThrow().displayName());
    assertEquals(busyNetwork().nodes(), restored.nodes());
  }

  @Test
  void aRestoredNetworkWaitsForTheFirstTickEvenIfTheSavedOneDidNot() {
    Network network = busyNetwork();
    network.settle(Map.of());

    assertFalse(network.isPending());
    assertTrue(restored(network).isPending());
  }

  @Test
  void anUnplacedNetworkComesBackUnplaced() {
    Network network = busyNetwork();
    network.removeController(OWNER);

    assertEquals(NetworkState.UNPLACED, restored(network).state());
  }

  /** Turned off and unplaced, it comes back with both, and off when placed again (178). */
  @Test
  void aNetworkTurnedOffComesBackOffEvenUnplaced() {
    Network network = busyNetwork();
    network.terminal().turnOff(OWNER);
    assertEquals(NetworkState.OFFLINE, restored(network).state());

    network.removeController(OWNER);
    Network unplaced = restored(network);
    unplaced.placeController(OWNER, NetworkFixture.CONTROLLER);
    assertEquals(NetworkState.OFFLINE, unplaced.state());
  }

  @Test
  void theSnapshotHoldsOnlyTheChannelsWithAName() {
    assertEquals(List.of(), network().snapshot().channels());
    assertEquals(
        List.of(new Channel(CHANNEL, Optional.of(CHANNEL_NAME))),
        busyNetwork().snapshot().channels());
  }

  @Test
  void aReadingOfZeroIsLeftOutBecauseItIsTheSameAsNoReading() {
    Network network = busyNetwork();
    network.readInput(NODE, INPUT_SLOT, Signal.OFF);

    assertEquals(List.of(), network.snapshot().readings());
  }

  @Test
  void theSnapshotHoldsTheReadingOfEachInput() {
    assertEquals(
        List.of(new SlotReading(NODE, INPUT_SLOT, READ)), busyNetwork().snapshot().readings());
  }
}
