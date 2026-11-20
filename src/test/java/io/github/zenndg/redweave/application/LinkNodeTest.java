package io.github.zenndg.redweave.application;

import static io.github.zenndg.redweave.application.ApplicationFixture.MISSING_NETWORK;
import static io.github.zenndg.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.redweave.domain.network.Network;
import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.Outcome;
import io.github.zenndg.redweave.domain.signal.OutputDelay;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** The Node Screen picks or switches the network of a node, which has one at most (141). */
class LinkNodeTest {
  private static final NodeId NODE = new NodeId(NODE_ID);
  private static final String NODE_NAME = "Gate lever";

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final LinkNode linkNode =
      new LinkNode(fixture.networks, fixture.networks, fixture.networks, fixture.lastUsed);

  private Optional<NetworkId> networkOfTheNode() {
    return fixture.networks.findByNode(NODE).map(Network::id);
  }

  @Test
  void aNodeWithoutANetworkJoinsTheChosenOne() {
    NetworkId network = fixture.ownersNetwork();

    assertEquals(Outcome.ACCEPTED, linkNode.link(OWNER, NODE, NODE_POSITION, network));
    assertEquals(Optional.of(network), networkOfTheNode());
  }

  @Test
  void aNodeMovesToTheNewNetworkAndLeavesTheOldOne() {
    NetworkId old = fixture.ownersNetworkWithANode();
    NetworkId target = fixture.ownersOtherNetwork();

    assertEquals(Outcome.ACCEPTED, linkNode.link(OWNER, NODE, NODE_POSITION, target));
    assertEquals(Optional.of(target), networkOfTheNode());
    assertTrue(fixture.networks.find(old).orElseThrow().node(NODE).isEmpty());
  }

  /** Nobody takes a node out of the network of another player (answer 42). */
  @Test
  void aNodeOfAnotherPlayerStaysInTheirNetwork() {
    NetworkId old = fixture.ownersNetworkWithANode();
    NetworkId strangers = fixture.strangersNetwork();

    assertEquals(Outcome.NOT_OWNER, linkNode.link(STRANGER, NODE, NODE_POSITION, strangers));
    assertEquals(Optional.of(old), networkOfTheNode());
  }

  @Test
  void aNodeDoesNotMoveIntoTheNetworkOfAnotherPlayer() {
    NetworkId old = fixture.ownersNetworkWithANode();
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
    NetworkId network = fixture.ownersNetworkWithANode();

    assertEquals(Outcome.ALREADY_ATTACHED, linkNode.link(OWNER, NODE, NODE_POSITION, network));
  }

  /** Only an online network takes a node, and a refusal leaves the old one as it was (157). */
  @Test
  void aNodeDoesNotJoinANetworkThatIsNotOnline() {
    NetworkId old = fixture.ownersNetworkWithANode();
    NetworkId target = fixture.ownersOtherNetwork();
    fixture.networks.find(target).orElseThrow().removeController(OWNER);

    assertEquals(Outcome.NETWORK_NOT_ONLINE, linkNode.link(OWNER, NODE, NODE_POSITION, target));
    assertEquals(Optional.of(old), networkOfTheNode());
  }

  /** The name belongs to the node, not to the network (answer 200). */
  @Test
  void aNodeKeepsItsNameInTheNewNetwork() {
    NetworkId old = fixture.ownersNetworkWithANode();
    fixture.networks.find(old).orElseThrow().nodeSettings().rename(OWNER, NODE, NODE_NAME);
    NetworkId target = fixture.ownersOtherNetwork();

    linkNode.link(OWNER, NODE, NODE_POSITION, target);

    Network network = fixture.networks.find(target).orElseThrow();
    assertEquals(NODE_NAME, network.node(NODE).orElseThrow().displayName());
  }

  /** The network a player last put a node in is highlighted on the next choice (answer 157). */
  @Test
  void theChosenNetworkBecomesTheLastUsedOne() {
    NetworkId network = fixture.ownersNetwork();

    linkNode.link(OWNER, NODE, NODE_POSITION, network);

    assertEquals(Optional.of(network), fixture.lastUsed.lastUsedOf(OWNER));
  }

  @Test
  void aRefusedChoiceIsNotTheLastUsedOne() {
    NetworkId strangers = fixture.strangersNetwork();

    linkNode.link(OWNER, NODE, NODE_POSITION, strangers);

    assertEquals(Optional.empty(), fixture.lastUsed.lastUsedOf(OWNER));
  }

  /** A node that moves keeps its output delay, as it keeps its name (answer 870). */
  @Test
  void aNodeThatMovesKeepsItsDelay() {
    NetworkId old = fixture.ownersNetworkWithANode();
    NetworkId target = fixture.ownersOtherNetwork();
    OutputDelay oneSecond = new OutputDelay(OutputDelay.STEP_TICKS);
    fixture.networks.find(old).orElseThrow().nodeSettings().delay(OWNER, NODE, oneSecond);

    linkNode.link(OWNER, NODE, NODE_POSITION, target);

    Network moved = fixture.networks.find(target).orElseThrow();
    assertEquals(oneSecond, moved.node(NODE).orElseThrow().delay());
  }
}
