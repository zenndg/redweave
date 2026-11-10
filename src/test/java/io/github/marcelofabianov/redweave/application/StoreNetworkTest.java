package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.MISSING_NETWORK;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.network.Outcome;
import org.junit.jupiter.api.Test;

/** Only the owner stores a network in the Configurator, because only they link nodes (104). */
class StoreNetworkTest {
  private final ApplicationFixture fixture = new ApplicationFixture();
  private final StoreNetwork storeNetwork = new StoreNetwork(fixture.networks);

  @Test
  void theOwnerStoresTheNetwork() {
    assertEquals(Outcome.ACCEPTED, storeNetwork.store(OWNER, fixture.ownersNetwork()));
  }

  @Test
  void anotherPlayerDoesNotStoreIt() {
    assertEquals(Outcome.NOT_OWNER, storeNetwork.store(STRANGER, fixture.ownersNetwork()));
  }

  @Test
  void aNetworkThatIsGoneIsNotStored() {
    assertEquals(Outcome.UNKNOWN_NETWORK, storeNetwork.store(OWNER, MISSING_NETWORK));
  }

  @Test
  void storingDoesNotChangeTheCurrentNetwork() {
    var first = fixture.ownersNetwork();
    var second = fixture.ownersOtherNetwork();

    storeNetwork.store(OWNER, first);

    assertEquals(second, fixture.current.currentOf(OWNER).orElseThrow());
  }
}
