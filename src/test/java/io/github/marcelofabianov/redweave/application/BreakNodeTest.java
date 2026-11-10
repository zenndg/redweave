package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** In survival, only the owner of the network breaks one of its nodes (answer 74). */
class BreakNodeTest {
  private static final NodeId NODE = new NodeId(NODE_ID);

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final BreakNode breakNode = new BreakNode(fixture.networks, fixture.networks);

  @Test
  void theOwnerBreaksANodeAndItLeavesTheNetwork() {
    NetworkId network = fixture.ownersNetworkWithANode();
    int savesBefore = fixture.networks.saves();

    assertEquals(Outcome.ACCEPTED, breakNode.breakBy(OWNER, NODE));
    assertEquals(Optional.empty(), fixture.networks.find(network).orElseThrow().node(NODE));
    assertEquals(savesBefore + 1, fixture.networks.saves());
  }

  @Test
  void anotherPlayerCannotBreakANodeOfTheNetwork() {
    NetworkId network = fixture.ownersNetworkWithANode();

    assertEquals(Outcome.NOT_OWNER, breakNode.breakBy(STRANGER, NODE));
    assertTrue(fixture.networks.find(network).orElseThrow().node(NODE).isPresent());
  }

  /** A node without a network changes no network, so anyone breaks it. */
  @Test
  void anyoneBreaksANodeWithoutANetwork() {
    assertEquals(Outcome.ACCEPTED, breakNode.breakBy(STRANGER, NODE));
  }
}
