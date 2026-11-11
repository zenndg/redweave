package io.github.marcelofabianov.redweave.domain.network;

import java.util.Optional;

/**
 * Inside the network: where its controller is and whether its owner turned it off. Turning off is a
 * choice of the owner, kept while the controller is away (answer 178). The network checks the owner
 * and what a change does to the signals.
 */
final class NetworkStatus {
  private Optional<BlockLocation> controller;
  private boolean off;

  NetworkStatus(Optional<BlockLocation> controller, boolean off) {
    this.controller = controller;
    this.off = off;
  }

  NetworkState state() {
    if (controller.isEmpty()) {
      return NetworkState.UNPLACED;
    }
    return off ? NetworkState.OFFLINE : NetworkState.ONLINE;
  }

  Outcome place(BlockLocation location) {
    if (controller.isPresent()) {
      return Outcome.ALREADY_PLACED;
    }
    controller = Optional.of(location);
    return Outcome.ACCEPTED;
  }

  void remove() {
    controller = Optional.empty();
  }

  /** Only an online network turns off, and only an offline one turns on (answers 132 and 135). */
  Outcome turn(boolean on) {
    NetworkState from = on ? NetworkState.OFFLINE : NetworkState.ONLINE;
    if (state() != from) {
      return on ? Outcome.NETWORK_NOT_OFFLINE : Outcome.NETWORK_NOT_ONLINE;
    }
    off = !on;
    return Outcome.ACCEPTED;
  }

  Optional<BlockLocation> controller() {
    return controller;
  }

  boolean isOff() {
    return off;
  }
}
