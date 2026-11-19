package io.github.zenndg.redweave.application;

import static io.github.zenndg.redweave.application.ApplicationFixture.NODE_ID;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.zenndg.redweave.domain.network.NodeId;
import io.github.zenndg.redweave.domain.network.Outcome;
import io.github.zenndg.redweave.domain.signal.OutputDelay;
import io.github.zenndg.redweave.domain.signal.TestDelayLimits;
import org.junit.jupiter.api.Test;

/** The owner steps the output delay of a node on the Node Screen (answers 859 and 864). */
class ChangeNodeDelayTest {
  private static final NodeId NODE = new NodeId(NODE_ID);
  private static final TestDelayLimits LIMITS = TestDelayLimits.STARTING;
  private static final OutputDelay ONE_SECOND = new OutputDelay(OutputDelay.STEP_TICKS);
  private static final int LONGER = 1;

  private final ApplicationFixture fixture = new ApplicationFixture();
  private final ChangeNodeDelay change = new ChangeNodeDelay(fixture.networks, fixture.networks);

  private OutputDelay delayOfTheNode() {
    return fixture.networks.findByNode(NODE).orElseThrow().node(NODE).orElseThrow().delay();
  }

  @Test
  void theOwnerStepsTheDelayAndItIsSaved() {
    fixture.ownersNetworkWithANode();
    int saves = fixture.networks.saves();

    assertEquals(Outcome.ACCEPTED, change.step(OWNER, NODE, LONGER, LIMITS));
    assertEquals(ONE_SECOND, delayOfTheNode());
    assertEquals(saves + 1, fixture.networks.saves());
  }

  @Test
  void anotherPlayerChangesNothingAndNothingIsSaved() {
    fixture.ownersNetworkWithANode();
    int saves = fixture.networks.saves();

    assertEquals(Outcome.NOT_OWNER, change.step(STRANGER, NODE, LONGER, LIMITS));
    assertEquals(OutputDelay.NONE, delayOfTheNode());
    assertEquals(saves, fixture.networks.saves());
  }

  /** A node with no network cannot be configured, its delay included (answer 139). */
  @Test
  void aNodeWithoutANetworkSaysSo() {
    assertEquals(Outcome.UNKNOWN_NODE, change.step(OWNER, NODE, LONGER, LIMITS));
  }
}
