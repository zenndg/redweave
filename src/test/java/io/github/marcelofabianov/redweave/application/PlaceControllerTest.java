package io.github.marcelofabianov.redweave.application;

import static io.github.marcelofabianov.redweave.application.ApplicationFixture.MISSING_NETWORK;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.CONTROLLER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OVERWORLD;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.OWNER;
import static io.github.marcelofabianov.redweave.domain.network.NetworkFixture.STRANGER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.TestLimits;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

/** Placing a controller creates a network or brings back the one its item keeps (answer 24). */
class PlaceControllerTest {
  private static final UUID NEW_ID = UUID.fromString("9c8b7a6d-5e4f-4a3b-8c2d-1e0f9a8b7c6d");
  private static final NetworkId NEW_NETWORK = new NetworkId(NEW_ID);
  private static final BlockLocation HERE = new BlockLocation(OVERWORLD, 40, 64, 12);

  private final ApplicationFixture fixture = new ApplicationFixture();

  private PlaceController placeController() {
    CreateNetwork create =
        new CreateNetwork(
            new FixedIds(List.of(NEW_ID)), fixture.networks, fixture.networks, TestLimits.STARTING);
    return new PlaceController(create, fixture.networks, fixture.networks);
  }

  @Test
  void anItemWithoutANetworkCreatesANewOneWhereTheBlockIs() {
    PlacedController placed = placeController().place(OWNER, Optional.empty(), HERE);

    assertEquals(new PlacedController(Outcome.ACCEPTED, Optional.of(NEW_NETWORK)), placed);
    assertEquals(Optional.of(HERE), fixture.networks.find(NEW_NETWORK).orElseThrow().controller());
  }

  @Test
  void anItemWithAnOfflineNetworkOfThePlayerBringsItBackWhereTheBlockIs() {
    NetworkId stored = fixture.ownersNetwork();
    fixture.networks.find(stored).orElseThrow().removeController(OWNER);
    int savesBefore = fixture.networks.saves();

    PlacedController placed = placeController().place(OWNER, Optional.of(stored), HERE);

    assertEquals(new PlacedController(Outcome.ACCEPTED, Optional.of(stored)), placed);
    assertEquals(savesBefore + 1, fixture.networks.saves());
    assertEquals(Optional.of(HERE), fixture.networks.find(stored).orElseThrow().controller());
    assertEquals(1, fixture.networks.ownedBy(OWNER).size());
  }

  /** A copied item does not give a network a second controller (answer 56). */
  @Test
  void anItemWithANetworkThatHasItsControllerIsNotPlaced() {
    NetworkId stored = fixture.ownersNetwork();
    int savesBefore = fixture.networks.saves();

    PlacedController placed = placeController().place(OWNER, Optional.of(stored), HERE);

    assertEquals(new PlacedController(Outcome.ALREADY_PLACED, Optional.empty()), placed);
    assertEquals(savesBefore, fixture.networks.saves());
    assertEquals(Optional.of(CONTROLLER), fixture.networks.find(stored).orElseThrow().controller());
  }

  /** Only the owner places the network again (answer 55). */
  @Test
  void anItemWithTheNetworkOfAnotherPlayerIsNotPlaced() {
    NetworkId stored = fixture.strangersNetwork();
    fixture.networks.find(stored).orElseThrow().removeController(STRANGER);

    PlacedController placed = placeController().place(OWNER, Optional.of(stored), HERE);

    assertEquals(new PlacedController(Outcome.NOT_OWNER, Optional.empty()), placed);
    assertEquals(Optional.empty(), fixture.networks.find(stored).orElseThrow().controller());
  }

  /** The network is gone, as after a restart before saving: a new one is born (answer 57). */
  @Test
  void anItemWithANetworkThatDoesNotExistCreatesANewOne() {
    PlacedController placed = placeController().place(OWNER, Optional.of(MISSING_NETWORK), HERE);

    assertEquals(new PlacedController(Outcome.ACCEPTED, Optional.of(NEW_NETWORK)), placed);
  }
}
