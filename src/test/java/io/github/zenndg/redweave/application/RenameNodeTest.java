package io.github.zenndg.redweave.application;

import static io.github.zenndg.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.Outcome;
import org.junit.jupiter.api.Test;

/** The owner renames a node on the Node Screen, in the one network of the node (answer 204). */
class RenameNodeTest {
  private static final NodeId NODE = new NodeId(NODE_ID);
  private static final String NEW_NAME = "Gate";

  /** One character over the 16 of node.name_length (answer 144). */
  private static final String TOO_LONG = "Front door lights";

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final RenameNode renameNode = new RenameNode(fixture.networks, fixture.networks);

  private String nameOfTheNode() {
    return fixture.networks.findByNode(NODE).orElseThrow().node(NODE).orElseThrow().displayName();
  }

  @Test
  void theOwnerRenamesTheNodeAndItIsSaved() {
    fixture.ownersNetworkWithANode();
    int saves = fixture.networks.saves();

    assertEquals(Outcome.ACCEPTED, renameNode.rename(OWNER, NODE, NEW_NAME));
    assertEquals(NEW_NAME, nameOfTheNode());
    assertEquals(saves + 1, fixture.networks.saves());
  }

  @Test
  void aRefusedNameIsNotSaved() {
    fixture.ownersNetworkWithANode();
    int saves = fixture.networks.saves();

    assertEquals(Outcome.NOT_OWNER, renameNode.rename(STRANGER, NODE, NEW_NAME));
    assertEquals(Outcome.INVALID_NAME, renameNode.rename(OWNER, NODE, TOO_LONG));
    assertEquals(saves, fixture.networks.saves());
  }

  /** A node with no network cannot be configured, its name included (answer 139). */
  @Test
  void aNodeWithoutANetworkSaysSo() {
    assertEquals(Outcome.UNKNOWN_NODE, renameNode.rename(OWNER, NODE, NEW_NAME));
  }
}
