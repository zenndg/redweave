package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.NODE_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NodeId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** A node that fell, as when its block is gone, leaves the network without an owner (74). */
class LoseNodeTest {
  private static final NodeId NODE = new NodeId(NODE_ID);

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final LoseNode loseNode = new LoseNode(fixture.networks, fixture.networks);

  @Test
  void aLostNodeLeavesTheNetwork() {
    NetworkId network = fixture.ownersNetworkWithANode();
    int savesBefore = fixture.networks.saves();

    assertEquals(Outcome.ACCEPTED, loseNode.lose(NODE));
    assertEquals(Optional.empty(), fixture.networks.find(network).orElseThrow().node(NODE));
    assertEquals(savesBefore + 1, fixture.networks.saves());
  }

  @Test
  void aLostNodeWithoutANetworkChangesNothing() {
    assertEquals(Outcome.UNKNOWN_NODE, loseNode.lose(NODE));
  }
}
