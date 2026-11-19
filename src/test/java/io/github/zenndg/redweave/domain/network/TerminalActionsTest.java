package io.github.zenndg.redweave.domain.network;

import static io.github.zenndg.redweave.domain.network.NetworkFixture.NODE;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OTHER_NODE;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OTHER_NODE_POSITION;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.STRANGER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.redweave.domain.signal.ChannelColor;
import io.github.zenndg.redweave.domain.signal.Signal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** What the Network Terminal does to a network: clear its nodes and delete it (133 and 166). */
class TerminalActionsTest {
  private static final ChannelColor LEVER = ChannelColor.RED;
  private static final int SLOT = LEVER.ordinal();

  /** NODE reads a lever into the red channel, and OTHER_NODE is in the network too. */
  private static Network twoNodesWithASignal() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());
    network.configureSlot(
        OWNER, NODE, SLOT, new ChannelSlot(Direction.INPUT, NODE_POSITION.face(), LEVER));
    network.attachNode(OWNER, OTHER_NODE, OTHER_NODE_POSITION, Optional.empty());
    network.readInput(NODE, SLOT, Signal.MAX);
    return network;
  }

  /** The nodes leave with their slots and readings, so no channel keeps an orphan value (133). */
  @Test
  void clearingTakesEveryNodeOut() {
    Network network = twoNodesWithASignal();

    assertEquals(Outcome.ACCEPTED, network.terminal().clearComponents(OWNER));

    assertEquals(List.of(), network.nodes());
    assertEquals(Signal.OFF, network.channelValue(LEVER));
    assertTrue(network.isPending());
  }

  @Test
  void onlyTheOwnerClearsTheNodes() {
    Network network = twoNodesWithASignal();

    assertEquals(Outcome.NOT_OWNER, network.terminal().clearComponents(STRANGER));
    assertEquals(List.of(NODE, OTHER_NODE), network.nodes().stream().map(Node::id).toList());
  }

  /** An unplaced network is cleared too, because it needs no controller (answer 176). */
  @Test
  void anUnplacedNetworkIsClearedToo() {
    Network network = twoNodesWithASignal();
    network.removeController(OWNER);

    assertEquals(Outcome.ACCEPTED, network.terminal().clearComponents(OWNER));
  }

  /** Only an unplaced network may go, so a network in the world is never deleted (166). */
  @Test
  void onlyAnUnplacedNetworkCanBeDeleted() {
    Network network = network();

    assertEquals(Outcome.NETWORK_NOT_UNPLACED, network.terminal().deletion(OWNER));
    network.terminal().turnOff(OWNER);
    assertEquals(Outcome.NETWORK_NOT_UNPLACED, network.terminal().deletion(OWNER));
    network.removeController(OWNER);
    assertEquals(Outcome.ACCEPTED, network.terminal().deletion(OWNER));
  }

  @Test
  void onlyTheOwnerDeletesANetwork() {
    Network network = network();
    network.removeController(OWNER);

    assertEquals(Outcome.NOT_OWNER, network.terminal().deletion(STRANGER));
  }
}
