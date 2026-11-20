package io.github.zenndg.redweave.application;

import static io.github.zenndg.redweave.application.ApplicationFixture.MISSING_NETWORK;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.zenndg.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.Outcome;
import org.junit.jupiter.api.Test;

/** Only the owner breaks the controller, and the network is unplaced (answers 30 and 146). */
class BreakControllerTest {
  private final ApplicationFixture fixture = new ApplicationFixture();
  private final BreakController breakController =
      new BreakController(fixture.networks, fixture.networks);

  @Test
  void theOwnerBreaksTheControllerAndTheNetworkIsUnplaced() {
    NetworkId network = fixture.ownersNetwork();
    int savesBefore = fixture.networks.saves();

    assertEquals(Outcome.ACCEPTED, breakController.breakBy(OWNER, network));
    assertFalse(fixture.networks.find(network).orElseThrow().isOnline());
    assertEquals(savesBefore + 1, fixture.networks.saves());
  }

  @Test
  void anotherPlayerCannotBreakTheController() {
    NetworkId network = fixture.ownersNetwork();
    int savesBefore = fixture.networks.saves();

    assertEquals(Outcome.NOT_OWNER, breakController.breakBy(STRANGER, network));
    assertTrue(fixture.networks.find(network).orElseThrow().isOnline());
    assertEquals(savesBefore, fixture.networks.saves());
  }

  /** A controller left in the world after a restart, before saving, has no network (53). */
  @Test
  void aControllerWithoutItsNetworkBreaksWithNothingToChange() {
    assertEquals(Outcome.UNKNOWN_NETWORK, breakController.breakBy(OWNER, MISSING_NETWORK));
  }
}
