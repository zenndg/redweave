package io.github.marcelofabianov.redweave.domain.network;

import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.CONTROLLER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OVERWORLD;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.network;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

/** The network lives in the world, and the controller only turns it on and off (answer 24). */
class NetworkControllerTest {
  private static final BlockLocation ELSEWHERE = new BlockLocation(OVERWORLD, 100, 70, -30);

  @Test
  void aNewNetworkKnowsWhereItsControllerIs() {
    assertEquals(Optional.of(CONTROLLER), network().controller());
  }

  @Test
  void removingTheControllerTakesTheNetworkOffline() {
    Network network = network();

    assertEquals(Outcome.ACCEPTED, network.removeController(OWNER));
    assertFalse(network.isOnline());
    assertEquals(Optional.empty(), network.controller());
  }

  @Test
  void placingTheControllerAgainBringsTheNetworkBackWhereItIsNow() {
    Network network = network();
    network.removeController(OWNER);

    assertEquals(Outcome.ACCEPTED, network.placeController(OWNER, ELSEWHERE));
    assertTrue(network.isOnline());
    assertEquals(Optional.of(ELSEWHERE), network.controller());
  }

  /** Only the owner breaks the controller, so nobody takes down the network of another (30). */
  @Test
  void anotherPlayerCannotRemoveTheController() {
    Network network = network();

    assertEquals(Outcome.NOT_OWNER, network.removeController(STRANGER));
    assertTrue(network.isOnline());
  }

  /** A copied item cannot give a network a second controller: the first one stays (answer 56). */
  @Test
  void aNetworkWithItsControllerPlacedRefusesAnotherOne() {
    Network network = network();

    assertEquals(Outcome.ALREADY_PLACED, network.placeController(OWNER, ELSEWHERE));
    assertEquals(Optional.of(CONTROLLER), network.controller());
  }

  /**
   * The block is already gone, as in creative or by a command, so there is nobody to ask: the
   * network goes offline anyway (answer 49).
   */
  @Test
  void losingTheControllerTakesTheNetworkOfflineWithoutAnOwner() {
    Network network = network();

    network.loseController();

    assertFalse(network.isOnline());
    assertEquals(Optional.empty(), network.controller());
  }

  @Test
  void anotherPlayerCannotPlaceTheController() {
    Network network = network();
    network.removeController(OWNER);

    assertEquals(Outcome.NOT_OWNER, network.placeController(STRANGER, ELSEWHERE));
    assertFalse(network.isOnline());
  }
}
