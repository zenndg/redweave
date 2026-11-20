package io.github.zenndg.redweave.application;

import io.github.zenndg.redweave.domain.network.NetworkId;
import java.util.Objects;
import java.util.Optional;

/**
 * What a Signal Module learns from its network on a tick: the network it is registered in now, for
 * its `registered` mark, and whether `Clear components` took it out, so it lets go of the network
 * (answers 811 and 817).
 */
public record ModuleSync(Optional<NetworkId> registeredIn, boolean cleared) {
  private static final String MISSING = "A module sync needs its network, empty or not";

  static final ModuleSync NONE = new ModuleSync(Optional.empty(), false);
  static final ModuleSync CLEARED = new ModuleSync(Optional.empty(), true);

  public ModuleSync {
    Objects.requireNonNull(registeredIn, MISSING);
  }

  static ModuleSync in(NetworkId network) {
    return new ModuleSync(Optional.of(network), false);
  }
}
