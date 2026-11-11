package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.BlockLocation;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.Outcome;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * A player places a Network Controller. An item without a network creates a new one, within the
 * limit of networks of the player (answer 145), and an item that keeps a network brings it back
 * where the block is, in the state it had (answers 24, 55 to 57 and 178).
 */
public final class PlaceController {
  private final CreateNetwork create;
  private final NetworkFinder finder;
  private final NetworkSaver saver;

  public PlaceController(CreateNetwork create, NetworkFinder finder, NetworkSaver saver) {
    this.create = create;
    this.finder = finder;
    this.saver = saver;
  }

  public PlacedController place(
      PlayerId player, Optional<NetworkId> stored, BlockLocation location) {
    Optional<Network> network = stored.flatMap(finder::find);
    if (network.isEmpty()) {
      Optional<NetworkId> created = create.create(player, location);
      Outcome outcome = created.isPresent() ? Outcome.ACCEPTED : Outcome.NETWORK_LIMIT;
      return new PlacedController(outcome, created);
    }
    Outcome outcome = network.get().placeController(player, location);
    if (outcome != Outcome.ACCEPTED) {
      return new PlacedController(outcome, Optional.empty());
    }
    saver.save(network.get());
    return new PlacedController(outcome, Optional.of(network.get().id()));
  }
}
