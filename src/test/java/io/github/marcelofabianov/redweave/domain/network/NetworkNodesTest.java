package io.github.marcelofabianov.redweave.domain.network;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.NODE_POSITION;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class NetworkNodesTest {
  /** The first 8 characters of the ID in NetworkFixture.NODE (answer 142). */
  private static final String DEFAULT_NODE_NAME = "1b2c3d4e";

  private static final String NODE_NAME = "Door";
  private static final String PADDED_NODE_NAME = "  Door  ";
  private static final String BLANK = "   ";

  /** One character over the 16 of node.name_length (answer 144). */
  private static final String TOO_LONG = "Front door lights";

  /** Without a name of its own, a node shows the start of its ID (answers 142 and 206). */
  @Test
  void aNewNodeHasNoNameOfItsOwnAndShowsTheStartOfItsId() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());

    Node node = network.node(NODE).orElseThrow();
    assertEquals(Optional.empty(), node.name());
    assertEquals(DEFAULT_NODE_NAME, node.displayName());
  }

  @Test
  void theOwnerAttachesANodeWithEveryOneOfItsSlotsFree() {
    Network network = network();

    assertEquals(
        Outcome.ACCEPTED, network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty()));
    Node node = network.node(NODE).orElseThrow();
    assertEquals(NODE_POSITION, node.position());
    assertEquals(Node.SLOT_COUNT, node.slots().size());
    assertTrue(node.slots().stream().allMatch(Optional::isEmpty));
  }

  @Test
  void theSameNodeIsNotAttachedTwice() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());

    assertEquals(
        Outcome.ALREADY_ATTACHED, network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty()));
    assertEquals(1, network.nodes().size());
  }

  @Test
  void anotherPlayerCannotAttachANode() {
    Network network = network();

    assertEquals(
        Outcome.NOT_OWNER, network.attachNode(STRANGER, NODE, NODE_POSITION, Optional.empty()));
    assertTrue(network.nodes().isEmpty());
  }

  /** A node only joins a network that is online (answers 157 and 172). */
  @Test
  void aNetworkThatIsNotOnlineRefusesANode() {
    Network network = network();
    network.removeController(OWNER);

    assertEquals(
        Outcome.NETWORK_NOT_ONLINE,
        network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty()));
    assertTrue(network.nodes().isEmpty());
  }

  @Test
  void theOwnerDetachesANode() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());

    assertEquals(Outcome.ACCEPTED, network.detachNode(OWNER, NODE));
    assertEquals(Optional.empty(), network.node(NODE));
  }

  /** The block of the node is gone, so there is nobody to ask: it leaves anyway (answer 74). */
  @Test
  void losingANodeTakesItOutWithoutAnOwner() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());

    assertEquals(Outcome.ACCEPTED, network.loseNode(NODE));
    assertEquals(Optional.empty(), network.node(NODE));
  }

  @Test
  void losingANodeThatIsNotThereSaysSo() {
    assertEquals(Outcome.UNKNOWN_NODE, network().loseNode(NODE));
  }

  @Test
  void detachingANodeThatIsNotThereSaysSo() {
    assertEquals(Outcome.UNKNOWN_NODE, network().detachNode(OWNER, NODE));
  }

  @Test
  void anotherPlayerCannotDetachANode() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());

    assertEquals(Outcome.NOT_OWNER, network.detachNode(STRANGER, NODE));
    assertTrue(network.node(NODE).isPresent());
  }

  @Test
  void theOwnerRenamesANodeWithoutTheSpacesAtTheEnds() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());

    assertEquals(Outcome.ACCEPTED, network.renameNode(OWNER, NODE, PADDED_NODE_NAME));
    Node node = network.node(NODE).orElseThrow();
    assertEquals(Optional.of(NODE_NAME), node.name());
    assertEquals(NODE_NAME, node.displayName());
  }

  /** An empty name goes back to the default one (answer 144). */
  @Test
  void aBlankNodeNameGoesBackToTheDefault() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());
    network.renameNode(OWNER, NODE, NODE_NAME);

    assertEquals(Outcome.ACCEPTED, network.renameNode(OWNER, NODE, BLANK));
    assertEquals(DEFAULT_NODE_NAME, network.node(NODE).orElseThrow().displayName());
  }

  @Test
  void aNodeNameLongerThanTheLimitIsRefusedAndTheNameStays() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());
    network.renameNode(OWNER, NODE, NODE_NAME);

    assertEquals(Outcome.INVALID_NAME, network.renameNode(OWNER, NODE, TOO_LONG));
    assertEquals(NODE_NAME, network.node(NODE).orElseThrow().displayName());
  }

  @Test
  void anotherPlayerCannotRenameANode() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());

    assertEquals(Outcome.NOT_OWNER, network.renameNode(STRANGER, NODE, NODE_NAME));
    assertEquals(DEFAULT_NODE_NAME, network.node(NODE).orElseThrow().displayName());
  }

  @Test
  void renamingANodeThatIsNotThereSaysSo() {
    assertEquals(Outcome.UNKNOWN_NODE, network().renameNode(OWNER, NODE, NODE_NAME));
  }

  /** Renaming keeps the slots, because a node is replaced as a whole (answer 33). */
  @Test
  void renamingANodeKeepsItsSlots() {
    Network network = network();
    network.attachNode(OWNER, NODE, NODE_POSITION, Optional.empty());
    List<Optional<ChannelSlot>> slots = network.node(NODE).orElseThrow().slots();

    network.renameNode(OWNER, NODE, NODE_NAME);

    assertEquals(slots, network.node(NODE).orElseThrow().slots());
  }

  /** A node that moves keeps the name it had, even past the limit of today (answers 93 and 200). */
  @Test
  void aNodeThatMovesInKeepsItsName() {
    Network network = network();

    assertEquals(
        Outcome.ACCEPTED, network.attachNode(OWNER, NODE, NODE_POSITION, Optional.of(TOO_LONG)));
    assertEquals(TOO_LONG, network.node(NODE).orElseThrow().displayName());
  }

  @Test
  void aNodeThatMovesInWithABlankOrPaddedNameIsRefused() {
    Network network = network();

    assertEquals(
        Outcome.INVALID_NAME, network.attachNode(OWNER, NODE, NODE_POSITION, Optional.of(BLANK)));
    assertEquals(
        Outcome.INVALID_NAME,
        network.attachNode(OWNER, NODE, NODE_POSITION, Optional.of(PADDED_NODE_NAME)));
    assertTrue(network.nodes().isEmpty());
  }
}
