package io.github.marcelofabianov.redweave.domain.network;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OVERWORLD;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** The state the screens show for a node (answer 29 and rede-e-canais.md). */
class NodeStateTest {
  private static final boolean LOADED = true;
  private static final boolean UNLOADED = false;
  private static final int FAR = 500;
  private static final BlockLocation FAR_CONTROLLER = new BlockLocation(OVERWORLD, FAR, 64, 0);

  private static Network networkWithANode() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION);
    return network;
  }

  @Test
  void aLoadedNodeInRangeIsOnline() {
    assertEquals(NodeState.ONLINE, networkWithANode().nodeState(NODE, LOADED));
  }

  @Test
  void aNodeInAnUnloadedChunkIsUnloaded() {
    assertEquals(NodeState.UNLOADED, networkWithANode().nodeState(NODE, UNLOADED));
  }

  /** Out of range wins over unloaded, because it is the one that stops the node from counting. */
  @Test
  void aNodeOutOfTheRadiusIsOutOfRangeLoadedOrNot() {
    Network network = networkWithANode();
    network.removeController(OWNER);
    network.placeController(OWNER, FAR_CONTROLLER);

    assertEquals(NodeState.OUT_OF_RANGE, network.nodeState(NODE, LOADED));
    assertEquals(NodeState.OUT_OF_RANGE, network.nodeState(NODE, UNLOADED));
  }

  @Test
  void aNodeOfAnOfflineNetworkHasNoNetwork() {
    Network network = networkWithANode();
    network.removeController(OWNER);

    assertEquals(NodeState.NO_NETWORK, network.nodeState(NODE, LOADED));
  }

  @Test
  void aNodeThatIsNotInTheNetworkHasNoNetwork() {
    assertEquals(NodeState.NO_NETWORK, network().nodeState(NODE, LOADED));
  }

  @Test
  void aNodeInRangeOfAnOnlineNetworkCounts() {
    assertTrue(networkWithANode().counts(NODE));
  }

  @Test
  void aNodeThatIsNotInTheNetworkDoesNotCount() {
    assertFalse(network().counts(NODE));
  }
}
