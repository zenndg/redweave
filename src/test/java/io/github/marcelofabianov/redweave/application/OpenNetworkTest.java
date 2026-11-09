package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.MISSING_NETWORK;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Opening the controller as owner makes it the current network (answer 25). */
class OpenNetworkTest {
  private final ApplicationFixture fixture = new ApplicationFixture();
  private final OpenNetwork openNetwork = new OpenNetwork(fixture.networks, fixture.current);

  @Test
  void theOwnerOpeningTheControllerMakesItTheirCurrentNetwork() {
    NetworkId id = fixture.ownersNetwork();
    fixture.current.choose(OWNER, MISSING_NETWORK);

    assertEquals(Outcome.ACCEPTED, openNetwork.open(OWNER, id));
    assertEquals(Optional.of(id), fixture.current.currentOf(OWNER));
  }

  /** Anyone may look, read only, but it never becomes their current network (answer 22). */
  @Test
  void anotherPlayerOpeningItKeepsTheirCurrentNetwork() {
    NetworkId id = fixture.ownersNetwork();

    assertEquals(Outcome.NOT_OWNER, openNetwork.open(STRANGER, id));
    assertEquals(Optional.empty(), fixture.current.currentOf(STRANGER));
  }

  @Test
  void openingANetworkThatDoesNotExistSaysSo() {
    assertEquals(Outcome.UNKNOWN_NETWORK, openNetwork.open(OWNER, MISSING_NETWORK));
  }
}
