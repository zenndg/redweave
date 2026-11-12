package io.github.marcelofabianov.redweave.domain.network;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OTHER_NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OVERWORLD;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import io.github.marcelofabianov.redweave.domain.signal.Signal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/**
 * A Signal Remote is an input with no position: each tick, the network gets the tablets that count
 * and puts 15 on the channel of each linked input slot, with the strongest rule (27, 352 and 360).
 */
class RemoteInputsTest {
  private static final ChannelColor DOOR = ChannelColor.RED;
  private static final ChannelColor GATE = ChannelColor.BLUE;
  private static final int SLOT = DOOR.ordinal();
  private static final int GATE_SLOT = GATE.ordinal();
  private static final Signal MIDDLE = new Signal(9);
  private static final int FAR = 500;
  private static final int GROUND = 64;
  private static final BlockLocation FAR_CONTROLLER =
      new BlockLocation(OVERWORLD, FAR, GROUND, FAR);
  private static final String TABLET_UUID = "3d4e5f6a-7b8c-4d9e-8f0a-1b2c3d4e5f6a";
  private static final String OTHER_TABLET_UUID = "4e5f6a7b-8c9d-4e0f-9a1b-2c3d4e5f6a7b";
  private static final RemoteId TABLET = new RemoteId(UUID.fromString(TABLET_UUID));
  private static final RemoteId OTHER_TABLET = new RemoteId(UUID.fromString(OTHER_TABLET_UUID));
  private static final RemoteInput ON_DOOR = new RemoteInput(TABLET, NODE, SLOT);
  private static final int NONE = 0;
  private static final int ONE = 1;
  private static final int TWO = 2;

  private static Network networkWith(Direction direction) {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());
    network.configureSlot(
        OWNER, NODE, SLOT, new ChannelSlot(direction, NODE_POSITION.face(), DOOR));
    network.settle(Map.of());
    return network;
  }

  @Test
  void aTabletThatCountsPutsTheStrongestSignalOnTheChannelOfItsSlot() {
    Network network = networkWith(Direction.INPUT);

    network.remotes().apply(Set.of(ON_DOOR));

    assertEquals(Signal.MAX, network.channelValue(DOOR));
    assertTrue(network.isPending());
  }

  @Test
  void theTabletJoinsTheReadingsWithTheStrongestRule() {
    Network network = networkWith(Direction.INPUT);
    network.readInput(NODE, SLOT, MIDDLE);

    network.remotes().apply(Set.of(ON_DOOR));
    assertEquals(Signal.MAX, network.channelValue(DOOR));

    network.remotes().apply(Set.of());
    assertEquals(MIDDLE, network.channelValue(DOOR));
  }

  @Test
  void aTabletThatChangesNoChannelLeavesTheNetworkWaitingForNothing() {
    Network network = networkWith(Direction.INPUT);
    network.readInput(NODE, SLOT, Signal.MAX);
    network.settle(Map.of());

    network.remotes().apply(Set.of(ON_DOOR));

    assertFalse(network.isPending());
  }

  @Test
  void theSameTabletsAgainChangeNothing() {
    Network network = networkWith(Direction.INPUT);
    network.remotes().apply(Set.of(ON_DOOR));
    network.settle(Map.of());

    network.remotes().apply(Set.of(ON_DOOR));

    assertFalse(network.isPending());
  }

  @Test
  void aTabletOnASlotThatIsNotAnInputDoesNothing() {
    Network network = networkWith(Direction.OUTPUT);

    network.remotes().apply(Set.of(ON_DOOR, new RemoteInput(OTHER_TABLET, OTHER_NODE, SLOT)));

    assertEquals(Signal.OFF, network.channelValue(DOOR));
    assertEquals(NONE, network.remotes().on());
    assertFalse(network.isPending());
  }

  @Test
  void theTabletFollowsItsSlotToAnotherChannel() {
    Network network = networkWith(Direction.INPUT);
    network.remotes().apply(Set.of(ON_DOOR));

    network.configureSlot(
        OWNER, NODE, SLOT, new ChannelSlot(Direction.INPUT, NODE_POSITION.face(), DOOR, false));

    assertEquals(Signal.OFF, network.channelValue(DOOR));
    assertFalse(network.remotes().accepts(NODE, SLOT));
  }

  @Test
  void aNetworkThatIsNotOnlineIgnoresItsTablets() {
    Network network = networkWith(Direction.INPUT);
    network.remotes().apply(Set.of(ON_DOOR));

    network.turnOff(OWNER);

    assertEquals(Signal.OFF, network.channelValue(DOOR));
  }

  @Test
  void aNodeOutOfRangeStillTakesItsTablets() {
    Network network = networkWith(Direction.INPUT);
    network.removeController(OWNER);
    network.placeController(OWNER, FAR_CONTROLLER);

    network.remotes().apply(Set.of(ON_DOOR));

    assertEquals(Signal.MAX, network.channelValue(DOOR));
  }

  @Test
  void copiesOfOneTabletCountOnce() {
    Network network = networkWith(Direction.INPUT);
    network.configureSlot(
        OWNER, NODE, GATE_SLOT, new ChannelSlot(Direction.INPUT, NODE_POSITION.face(), GATE));

    network.remotes().apply(Set.of(ON_DOOR, new RemoteInput(TABLET, NODE, GATE_SLOT)));
    assertEquals(ONE, network.remotes().on());

    network.remotes().apply(Set.of(ON_DOOR, new RemoteInput(OTHER_TABLET, NODE, GATE_SLOT)));
    assertEquals(TWO, network.remotes().on());
  }

  @Test
  void onlyAnEnabledInputSlotTakesATablet() {
    Network network = networkWith(Direction.INPUT);

    assertTrue(network.remotes().accepts(NODE, SLOT));
    assertFalse(network.remotes().accepts(NODE, GATE_SLOT));
    assertFalse(network.remotes().accepts(OTHER_NODE, SLOT));
  }

  @Test
  void aRemoteNeedsAnId() {
    assertThrows(NullPointerException.class, () -> new RemoteId(null));
    assertThrows(NullPointerException.class, () -> new RemoteInput(null, NODE, SLOT));
  }
}
