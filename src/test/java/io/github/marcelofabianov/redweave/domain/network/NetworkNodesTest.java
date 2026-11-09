package io.github.marcelofabianov.redweave.domain.network;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class NetworkNodesTest {
  @Test
  void theOwnerAttachesANodeWithEveryOneOfItsSlotsFree() {
    Network network = network();

    assertEquals(Outcome.ACCEPTED, network.attachNode(OWNER, NODE, NODE_POSITION));
    Node node = network.node(NODE).orElseThrow();
    assertEquals(NODE_POSITION, node.position());
    assertEquals(TestLimits.STARTING.slotsPerNode(), node.slots().size());
    assertTrue(node.slots().stream().allMatch(Optional::isEmpty));
  }

  @Test
  void theSameNodeIsNotAttachedTwice() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION);

    assertEquals(Outcome.ALREADY_ATTACHED, network.attachNode(OWNER, NODE, NODE_POSITION));
    assertEquals(1, network.nodes().size());
  }

  @Test
  void anotherPlayerCannotAttachANode() {
    Network network = network();

    assertEquals(Outcome.NOT_OWNER, network.attachNode(STRANGER, NODE, NODE_POSITION));
    assertTrue(network.nodes().isEmpty());
  }

  @Test
  void theOwnerDetachesANode() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION);

    assertEquals(Outcome.ACCEPTED, network.detachNode(OWNER, NODE));
    assertEquals(Optional.empty(), network.node(NODE));
  }

  @Test
  void detachingANodeThatIsNotThereSaysSo() {
    assertEquals(Outcome.UNKNOWN_NODE, network().detachNode(OWNER, NODE));
  }

  @Test
  void anotherPlayerCannotDetachANode() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION);

    assertEquals(Outcome.NOT_OWNER, network.detachNode(STRANGER, NODE));
    assertTrue(network.node(NODE).isPresent());
  }
}
