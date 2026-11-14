package io.github.marcelofabianov.redweave.application;

import io.github.marcelofabianov.redweave.domain.network.ModuleInput;
import io.github.marcelofabianov.redweave.domain.network.NetworkId;
import java.util.Objects;

/**
 * A loaded Signal Module that writes into a channel of its network this tick, with its name of the
 * moment for the log (answers 387 and 392).
 */
public record ModuleSighting(NetworkId network, ModuleInput input, String name) {
  private static final String MISSING = "A sighting needs the network, the input and the name";

  public ModuleSighting {
    Objects.requireNonNull(network, MISSING);
    Objects.requireNonNull(input, MISSING);
    Objects.requireNonNull(name, MISSING);
  }
}
