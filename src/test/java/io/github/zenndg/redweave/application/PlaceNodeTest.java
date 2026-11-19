package io.github.zenndg.redweave.application;

import static io.github.zenndg.redweave.application.ApplicationFixture.NODE_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.zenndg.redweave.domain.network.NodeId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** A placed node is born with no network, and its owner picks one later (answer 139). */
class PlaceNodeTest {
  private final ApplicationFixture fixture = new ApplicationFixture();

  @Test
  void aPlacedNodeGetsANewIdAndJoinsNoNetwork() {
    fixture.ownersNetwork();
    int saves = fixture.networks.saves();

    NodeId placed = new PlaceNode(new FixedIds(List.of(NODE_ID))).place();

    assertEquals(new NodeId(NODE_ID), placed);
    assertEquals(Optional.empty(), fixture.networks.findByNode(placed));
    assertEquals(saves, fixture.networks.saves());
  }
}
