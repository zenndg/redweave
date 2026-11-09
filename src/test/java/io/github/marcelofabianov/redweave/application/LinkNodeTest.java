package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.MISSING_NETWORK;
import static io.github.marcelofabianov.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** The Configurator moves a node to another network, and a node has one network at most. */
class LinkNodeTest {
  private static final NodeId NODE = new NodeId(NODE_ID);

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final LinkNode linkNode =
      new LinkNode(fixture.networks, fixture.networks, fixture.networks);

  private Optional<NetworkId> networkOfTheNode() {
    return fixture.networks.findByNode(NODE).map(Network::id);
  }

  /** Places the node in the current network of OWNER, as the game would. */
  private void placeTheNode() {
    new PlaceNode(
            new FixedIds(List.of(NODE_ID)), fixture.current, fixture.networks, fixture.networks)
        .place(OWNER, NODE_POSITION);
  }

  @Test
  void aNodeWithoutANetworkJoinsTheChosenOne() {
    NetworkId network = fixture.ownersNetwork();

    assertEquals(Outcome.ACCEPTED, linkNode.link(OWNER, NODE, NODE_POSITION, network));
    assertEquals(Optional.of(network), networkOfTheNode());
  }

  @Test
  void aNodeMovesToTheNewNetworkAndLeavesTheOldOne() {
    NetworkId old = fixture.ownersNetwork();
    placeTheNode();
    NetworkId target = fixture.ownersOtherNetwork();

    assertEquals(Outcome.ACCEPTED, linkNode.link(OWNER, NODE, NODE_POSITION, target));
    assertEquals(Optional.of(target), networkOfTheNode());
    assertTrue(fixture.networks.find(old).orElseThrow().node(NODE).isEmpty());
  }

  /** Nobody takes a node out of the network of another player (answer 42). */
  @Test
  void aNodeOfAnotherPlayerStaysInTheirNetwork() {
    NetworkId old = fixture.ownersNetwork();
    placeTheNode();
    NetworkId strangers = fixture.strangersNetwork();

    assertEquals(Outcome.NOT_OWNER, linkNode.link(STRANGER, NODE, NODE_POSITION, strangers));
    assertEquals(Optional.of(old), networkOfTheNode());
  }

  @Test
  void aNodeDoesNotMoveIntoTheNetworkOfAnotherPlayer() {
    NetworkId old = fixture.ownersNetwork();
    placeTheNode();
    NetworkId strangers = fixture.strangersNetwork();

    assertEquals(Outcome.NOT_OWNER, linkNode.link(OWNER, NODE, NODE_POSITION, strangers));
    assertEquals(Optional.of(old), networkOfTheNode());
  }

  @Test
  void linkingToANetworkThatDoesNotExistSaysSo() {
    assertEquals(
        Outcome.UNKNOWN_NETWORK, linkNode.link(OWNER, NODE, NODE_POSITION, MISSING_NETWORK));
  }

  @Test
  void linkingToTheNetworkItIsAlreadyInSaysSo() {
    NetworkId network = fixture.ownersNetwork();
    placeTheNode();

    assertEquals(Outcome.ALREADY_ATTACHED, linkNode.link(OWNER, NODE, NODE_POSITION, network));
  }
}
