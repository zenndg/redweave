package io.github.zenndg.redweave.application;

import static io.github.zenndg.redweave.application.ApplicationFixture.MISSING_NETWORK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import io.github.zenndg.redweave.domain.network.NetworkId;
import io.github.zenndg.redweave.domain.network.Outcome;
import org.junit.jupiter.api.Test;

/** The controller left the world without its owner, and the network is unplaced (answer 49). */
class LoseControllerTest {
  private final ApplicationFixture fixture = new ApplicationFixture();
  private final LoseController loseController =
      new LoseController(fixture.networks, fixture.networks);

  @Test
  void aLostControllerLeavesTheNetworkUnplaced() {
    NetworkId network = fixture.strangersNetwork();
    int savesBefore = fixture.networks.saves();

    assertEquals(Outcome.ACCEPTED, loseController.lose(network));
    assertFalse(fixture.networks.find(network).orElseThrow().isOnline());
    assertEquals(savesBefore + 1, fixture.networks.saves());
  }

  @Test
  void aLostControllerWithoutItsNetworkChangesNothing() {
    assertEquals(Outcome.UNKNOWN_NETWORK, loseController.lose(MISSING_NETWORK));
  }
}
