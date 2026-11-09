package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.MISSING_NETWORK;
import static io.github.marcelofabianov.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.network.ChannelSlot;
import io.github.marcelofabianov.redweave.domain.network.Direction;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.Node;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.signal.ChannelColor;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** A node placed by a player joins their current network and works at once (answer 25). */
class PlaceNodeTest {
  private static final int FIRST_SLOT = 0;
  private static final int CHANNELS = 16;
  private static final NodeId NODE = new NodeId(NODE_ID);

  private final ApplicationFixture fixture = new ApplicationFixture();

  private PlaceNode placeNodeWith(IdGenerator ids) {
    return new PlaceNode(ids, fixture.current, fixture.networks, fixture.networks);
  }

  private Node placedNode(NetworkId network) {
    return fixture.networks.find(network).orElseThrow().node(NODE).orElseThrow();
  }

  @Test
  void aPlacedNodeReadsTheBlockItSticksToOnTheFirstFreeChannel() {
    NetworkId network = fixture.ownersNetwork();

    PlacedNode placed = placeNodeWith(new FixedIds(List.of(NODE_ID))).place(OWNER, NODE_POSITION);

    assertEquals(new PlacedNode(NODE, Outcome.ACCEPTED), placed);
    ChannelSlot expected =
        new ChannelSlot(Direction.INPUT, NODE_POSITION.face(), ChannelColor.WHITE);
    assertEquals(Optional.of(expected), placedNode(network).slots().get(FIRST_SLOT));
  }

  @Test
  void withoutACurrentNetworkTheNodeHasNoNetwork() {
    PlacedNode placed = placeNodeWith(new FixedIds(List.of(NODE_ID))).place(OWNER, NODE_POSITION);

    assertEquals(new PlacedNode(NODE, Outcome.NO_CURRENT_NETWORK), placed);
  }

  @Test
  void aCurrentNetworkThatIsGoneSaysSo() {
    fixture.current.choose(OWNER, MISSING_NETWORK);

    PlacedNode placed = placeNodeWith(new FixedIds(List.of(NODE_ID))).place(OWNER, NODE_POSITION);

    assertEquals(new PlacedNode(NODE, Outcome.UNKNOWN_NETWORK), placed);
  }

  /** Sixteen nodes take the sixteen channels, so the next one joins with every slot free. */
  @Test
  void withEveryChannelInUseTheNodeJoinsWithoutASlot() {
    NetworkId network = fixture.ownersNetwork();
    PlaceNode placeNode = placeNodeWith(UUID::randomUUID);
    for (int node = 0; node < CHANNELS; node++) {
      placeNode.place(OWNER, NODE_POSITION);
    }

    placeNodeWith(new FixedIds(List.of(NODE_ID))).place(OWNER, NODE_POSITION);

    assertTrue(placedNode(network).slots().stream().allMatch(Optional::isEmpty));
  }

  /** A current network of another owner, as from a damaged save, never takes the node (22). */
  @Test
  void aCurrentNetworkOfAnotherOwnerDoesNotTakeTheNode() {
    NetworkId network = fixture.ownersNetwork();
    fixture.current.choose(STRANGER, network);
    int saves = fixture.networks.saves();

    PlacedNode placed =
        placeNodeWith(new FixedIds(List.of(NODE_ID))).place(STRANGER, NODE_POSITION);

    assertEquals(new PlacedNode(NODE, Outcome.NOT_OWNER), placed);
    assertEquals(saves, fixture.networks.saves());
  }
}
