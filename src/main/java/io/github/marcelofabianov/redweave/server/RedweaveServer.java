package io.github.marcelofabianov.redweave.server;

import io.github.marcelofabianov.redweave.application.BreakController;
import io.github.marcelofabianov.redweave.application.CreateNetwork;
import io.github.marcelofabianov.redweave.application.LoseController;
import io.github.marcelofabianov.redweave.application.OpenNetwork;
import io.github.marcelofabianov.redweave.application.PlaceController;
import io.github.marcelofabianov.redweave.domain.network.Network;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import io.github.marcelofabianov.redweave.domain.network.NetworkLimits;
import io.github.marcelofabianov.redweave.domain.network.PlayerId;
import java.util.Optional;

/**
 * The state of one running server and the use cases over it. It is born when the world opens and
 * dropped when it closes, so nothing leaks from one world to the next (answers 53 and 63).
 */
public final class RedweaveServer {
  private static RedweaveServer running;

  private final MemoryNetworks networks = new MemoryNetworks();
  private final MemoryCurrentNetworks current = new MemoryCurrentNetworks();
  private final PlaceController placeController;
  private final BreakController breakController;
  private final LoseController loseController;
  private final OpenNetwork openNetwork;

  private RedweaveServer(NetworkLimits limits) {
    CreateNetwork create = new CreateNetwork(new RandomIds(), networks, networks, current, limits);
    placeController = new PlaceController(create, networks, networks);
    breakController = new BreakController(networks, networks);
    loseController = new LoseController(networks, networks);
    openNetwork = new OpenNetwork(networks, current);
  }

  /** The server that is running now, or none, as on a client that only shows the world. */
  public static Optional<RedweaveServer> running() {
    return Optional.ofNullable(running);
  }

  static void start(NetworkLimits limits) {
    running = new RedweaveServer(limits);
  }

  static void stop() {
    running = null;
  }

  public PlaceController placeController() {
    return placeController;
  }

  public BreakController breakController() {
    return breakController;
  }

  public LoseController loseController() {
    return loseController;
  }

  public OpenNetwork openNetwork() {
    return openNetwork;
  }

  public Optional<Network> network(NetworkId id) {
    return networks.find(id);
  }

  public Optional<NetworkId> currentOf(PlayerId player) {
    return current.currentOf(player);
  }
}
